package com.sparta.mulmul.service;
import com.sparta.mulmul.dto.NotificationType;
import com.sparta.mulmul.dto.item.*;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.model.*;
import com.sparta.mulmul.repository.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.mulmul.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BagRepository bagRepositroy;
    private final ScrabRepository scrabRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ReportRepository reportRepository;
    private final BarterRepository barterRepository;
    private final NotificationRepository notificationRepository;

    // 이승재 / 보따리 아이템 등록하기
    public Long createItem(ItemRequestDto itemRequestDto, UserDetailsImpl userDetails){
        Bag bag = bagRepositroy.findByUserId(userDetails.getUserId());
        List<Item> itemList = itemRepository.findAllByBagId(bag.getId());
        int itemCnt = 0;
        for(Item item : itemList){
            if(item.getStatus() == 0 || item.getStatus() == 1 || item.getStatus() ==2){
                itemCnt++;
            }
        }
        if(itemCnt>=9){
            return 0L;
        }
        List<String> imgUrlList = itemRequestDto.getImgUrl();
        List<String> favoredList = itemRequestDto.getFavored();
        String imgUrl = String.join(",", imgUrlList);
        String favored = String.join(",", favoredList);
        Optional<Item> existItem = itemRepository.findByTitleAndContents(itemRequestDto.getTitle(), itemRequestDto.getContents());
        if(existItem.isPresent()){
             return 1L;
        }else {

            User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                    () -> new CustomException(NOT_FOUND_USER)
            );

            // 유저 아이디를 통해 보따리 정보를 가져오고 후에 아이템을 저장할때 보따리 정보 넣어주기 & 아이템 개수 +
            bag.update(bag.getItemCnt() + 1);


            Item item = Item.builder()
                    .title(itemRequestDto.getTitle())
                    .contents(itemRequestDto.getContents())
                    .address(user.getAddress())  //유저 정보에서 가져오기
                    .category(itemRequestDto.getCategory())
                    .scrabCnt(0)
                    .commentCnt(0) // 사용할지 안할지 확정안됨
                    .viewCnt(0)
                    .reportCnt(0)
                    .status(0)
                    .itemImg(imgUrl)
                    .type(itemRequestDto.getType())
                    .favored(favored)
                    .bag(bag)
                    .build();

            item = itemRepository.save(item);
            return item.getId();
        }

    }
    //이승재 / 전체 아이템 조회(카테고리별)
    public ItemMainResponseDto getItems(int page, String category, UserDetailsImpl userDetails) {
        Pageable pageable = getPageable(page);
        if(category.isEmpty()){
            Page<Item> itemList = itemRepository.findAllItemOrderByCreatedAtDesc(pageable);
            List<ItemResponseDto> items = new ArrayList<>();
            Long totalCnt = itemList.getTotalElements();
            for(Item item : itemList) {
                    //구독 개수
                    List<Scrab> scrabs = scrabRepository.findAllItemById(item.getId());
                    int scrabCnt = scrabs.size();
                    //구독 정보 확인
                    boolean isScarb = checkScrab(userDetails.getUserId(), item.getId());
                    // 거리 계산
                    String distance = getDistance(userDetails, item);

                    User user = userRepository.findById(item.getBag().getUserId()).orElseThrow(
                            () -> new CustomException(NOT_FOUND_USER)
                    );
                    String nickname = user.getNickname();
                    ItemResponseDto itemResponseDto = new ItemResponseDto(
                            item.getId(),
                            nickname,
                            item.getCategory(),
                            item.getTitle(),
                            item.getContents(),
                            item.getItemImg().split(",")[0],
                            distance,
                            scrabCnt,
                            item.getViewCnt(),
                            item.getStatus(),
                    isScarb);
                    items.add(itemResponseDto);
            }
            ItemMainResponseDto itemMainResponseDto = new ItemMainResponseDto(totalCnt, items);
            return itemMainResponseDto;
        }
        Page<Item> itemList = itemRepository.findAllItemByCategoryOrderByCreatedAtDesc(category, pageable);
        Long totalCnt = itemList.getTotalElements();
        List<ItemResponseDto> items = new ArrayList<>();
        for(Item item : itemList) {
                Long itemId = item.getId();

                //구독 개수
                List<Scrab> scrabs = scrabRepository.findAllItemById(itemId);
                int scrabCnt = scrabs.size();
                //구독 정보 확인
                boolean isScarb = checkScrab(userDetails.getUserId(), itemId);
                // 거리 계산
                String distance = getDistance(userDetails, item);

                User user = userRepository.findById(item.getBag().getUserId()).orElseThrow(
                     () -> new CustomException(NOT_FOUND_USER)
                );
                 String nickname = user.getNickname();
                ItemResponseDto itemResponseDto = new ItemResponseDto(
                        itemId,
                        nickname,
                        item.getCategory(),
                        item.getTitle(),
                        item.getContents(),
                        item.getItemImg().split(",")[0],
                        distance,
                        scrabCnt,
                        item.getViewCnt(),
                        item.getStatus(),
                        isScarb);
                items.add(itemResponseDto);

            }
        ItemMainResponseDto itemMainResponseDto = new ItemMainResponseDto(totalCnt, items);
            return itemMainResponseDto;
        }

    // 이승재 / 페이징 처리
    private Pageable getPageable(int page) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = Sort.by(direction, "createdAt");
        return PageRequest.of(page, 10, sort);
    }

    //이승재 / 구독정보 확인
    private boolean checkScrab(Long userId, Long itemId){
        if(scrabRepository.findByUserIdAndItemId(userId, itemId).isPresent()){
            return true;
        }else{
            return false;
        }
    }
    //이승재 / 거리 계산
    private String getDistance(UserDetailsImpl userDetails, Item item){
        String distance;
        if(userDetails == null){
             distance = "null";
             return distance;
        }else{

            distance = caculateDistance(userDetails.getUserId(), item.getAddress());
        }
        return distance;
    }

    // 이승재 / 아이템 상세페이지
    @Transactional
    public ItemDetailResponseDto getItemDetail(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_ITEM)
        );

        // 이승재 / 아이템 조회수 계산
        int viewCnt = item.getViewCnt();
        viewCnt += 1;
        item.update(itemId, viewCnt);

        // 이승재 / 아이템 구독 정보 유저 정보를 통해 확인
        Long userId = userDetails.getUserId();
        boolean isScrab = checkScrab(userId, itemId);

        User user = userRepository.findById(item.getBag().getUserId()).orElseThrow(
                ()-> new CustomException(NOT_FOUND_USER)
        );

        List<String> itemImgList = new ArrayList<>();
        for(int i = 0; i<item.getItemImg().split(",").length; i++){
            itemImgList.add(item.getItemImg().split(",")[i]);
        }
        
        // 구독 개수 계산
        List<Scrab> scrabs = scrabRepository.findAllItemById(itemId);
        int scrabCnt = scrabs.size();

        item.scrabCntUpdate(itemId, scrabCnt);
        String[] favored = item.getFavored().split(",");


        // 거리 계산
        String distance = getDistance(userDetails, item);

        // 교환신청했는지 확인하기
        String traded = null;
              Long barterId = Long.valueOf(0);
        Long buyerId = userDetails.getUserId();
        Long sellerId = user.getId();
        List<Barter> barterList = barterRepository.findAllByBuyerIdAndSellerId(buyerId, sellerId);
        int tradeCnt = 0;
        for(Barter barter : barterList){
            if(barter.getBarter().split(";")[1].equals(itemId.toString())){
                barterId = barter.getId();
                tradeCnt++;
            }
        }
        if(barterList.isEmpty() || tradeCnt==0 ){
            traded = "false";
        }else if(tradeCnt>0){
            traded = "true";

     }

        ItemDetailResponseDto itemDetailResponseDto = new ItemDetailResponseDto(
                user.getId(),
                itemId,
                user.getNickname(),
                user.getDegree(),
                user.getGrade(),
                user.getProfile(),
                item.getStatus(),
                item.getCategory(),
                itemImgList,
                item.getTitle(),
                item.getContents(),
                distance,
                item.getCreatedAt(),
                item.getViewCnt(),
                scrabCnt,
                item.getType(),
                favored,
                isScrab,
                traded,
                barterId);
        return itemDetailResponseDto;
    }

    // 이승재 / 위도 경도 거리계산하기
    private String caculateDistance(Long userId, String address) {

        if(userId == null){
            return "null";
        }else {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(NOT_FOUND_USER)
            );
            if (user.getAddress() == null) {
                return "null";
            } else {
                String userAddress = user.getAddress().split(" ")[0] + " " + user.getAddress().split(" ")[1];
                String itemAddress = address.split(" ")[0] + " " + address.split(" ")[1];
                Location userLocation = locationRepository.findByArea(userAddress);
                Location itemLocation = locationRepository.findByArea(itemAddress);
                double userLat = userLocation.getLatitude();
                double userLon = userLocation.getLongitude();
                double itemLat = itemLocation.getLatitude();
                double itemLon = itemLocation.getLongitude();

                if (userLat == itemLat && userLon == itemLon) {
                    return "인근";
                } else {
                    double theta = userLon - itemLon;
                    double dist = Math.sin(deg2rad(userLat)) * Math.sin(deg2rad(itemLat)) + Math.cos(deg2rad(userLat)) * Math.cos(deg2rad(itemLat)) * Math.cos(deg2rad(theta));

                    dist = Math.acos(dist);
                    dist = rad2deg(dist);
                    dist = dist * 60 * 1.1515;
                    dist = dist * 1.609344;
                    double dist1 = Math.round(dist * 10) / 10.0;
                    String distance = Double.toString(dist1);
                    return "약 " + distance + "km";
                }
            }
        }
    }
    private double deg2rad(double deg){
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad){
        return (rad * 180 / Math.PI);
    }

    // 이승재 / 아이템 구독하기
    @Transactional
    public void scrabItem(Long itemId, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUserId();
        Optional<Scrab> scrab = scrabRepository.findByUserIdAndItemId(userId, itemId);
        if(scrab.isPresent()){
            Long scrabId = scrab.get().getId();
            Scrab scrab1 = scrabRepository.findById(scrabId).orElseThrow(
                    ()-> new IllegalArgumentException("구독 정보가 없습니다.")
            );
            if(scrab1.getScrab().equals(true)) {
                scrab1.update(scrabId, false);
            }else{
                scrab1.update(scrabId, true);
            }
        }else{
            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            System.out.println(userDetails.getUserId());
            System.out.println(item.getBag().getUserId());
            if (userDetails.getUserId().equals(item.getBag().getUserId())) {
                throw new CustomException(CANT_SCRAB_OWN_ITEM);
            }else {

                Scrab newScrab = Scrab.builder()
                        .userId(userId)
                        .itemId(itemId)
                        .scrab(true)
                        .build();
                scrabRepository.save(newScrab);
            }
        }
    }

    // 이승재 / 아이템 수정
    @Transactional
    public void updateItem(ItemUpdateRequestDto itemUpdateRequestDto, UserDetailsImpl userDetails, Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_ITEM)
        );
        List<String> images = itemUpdateRequestDto.getImages();
        List<String> imagesUrl = itemUpdateRequestDto.getImagesUrl();
        List<String> imagesJoind = new ArrayList<>();
        if(images.contains("null")) {
            imagesJoind.addAll(imagesUrl);
        }else{
            imagesJoind.addAll(imagesUrl);
            imagesJoind.addAll(images);
        }
        List<String> favoredList = itemUpdateRequestDto.getFavored();
        String imgUrl = String.join(",", imagesJoind);
        String favored = String.join(",", favoredList);
        if(item.getBag().getUserId().equals(userDetails.getUserId())){
            item.itemUpdate(itemUpdateRequestDto, imgUrl, favored);
        }
    }


    // 이승재 / 아이템 삭제
    @Transactional
    public void deleteItem(Long itemId, UserDetailsImpl userDetails) {
        Item item = itemRepository.findById(itemId).orElseThrow(
                ()-> new CustomException(NOT_FOUND_ITEM)
        );
        if(item.getBag().getUserId().equals(userDetails.getUserId())){
            item.setDeleted(itemId, 6);
        }

        // 아이템 이 삭제 되면 연관된 거래내역 삭제 및 아이템 상태 0으로 변환
        List<Barter> sellerBarterList = barterRepository.findAllBySellerId(userDetails.getUserId());
        for(Barter eachBarter : sellerBarterList){
            String[] eachBarterIdList = eachBarter.getBarter().split(";");
            String[] eachBuyerItemIds = eachBarterIdList[0].split(",");
            String eachSellerItemId = eachBarterIdList[1];
            Long sellerItemId =  Long.parseLong(eachSellerItemId);
            if(sellerItemId.equals(itemId)){
                for(String eachBuyerItemId : eachBuyerItemIds){
                    Long buyerItemId = Long.parseLong(eachBuyerItemId);
                    Item eachBuyerItem = itemRepository.findById(buyerItemId).orElseThrow(()->new CustomException(NOT_FOUND_ITEM));
                    eachBuyerItem.statusUpdate(buyerItemId, 0);

                    barterRepository.delete(eachBarter);
                    notificationRepository.deleteByChangeIdAndType(eachBarter.getId(), NotificationType.BARTER);
                }
            }
        }
        // 아이템이 삭제되었다면, 거래중인 아이템 거래내역 전원이 삭제되어야 합니다.
        // 혹시 알림에 있었다면 삭제되어야 합니다.
    }

    // 이승재 / 아이템 신고하기
    @Transactional
    public String reportItem(Long itemId, UserDetailsImpl userDetails) {
        Optional<Report> findReport = reportRepository.findByReporterIdAndReportedItemId(userDetails.getUserId(), itemId);
        if (findReport.isPresent()){
            return "false";
        }else {

            Item item = itemRepository.findById(itemId).orElseThrow(
                    () -> new CustomException(NOT_FOUND_ITEM)
            );
            int reportCnt = item.getReportCnt();
            item.reportCntUpdate(itemId, reportCnt + 1);

            Report report = Report.builder()
                    .reportedItemId(itemId)
                    .reporterId(userDetails.getUserId())
                    .build();
            reportRepository.save(report);
            if (item.getReportCnt() == 5) {
                item.statusUpdate(itemId, 5);
                List<Scrab> scrabs = scrabRepository.findAllByItemId(itemId);
                for(Scrab scrab : scrabs){
                    scrab.update(scrab.getId(), false);
                }
            }
            return "true";
        }
    }


    // 이승재 / 아이템 검색
    public List<ItemSearchResponseDto> searchItem(String keyword, UserDetailsImpl userDetails) {
        List<Item> itemList = itemRepository.searchByKeyword(keyword);
        List<ItemSearchResponseDto> itemResponseDtos = new ArrayList<>();
        Long userId = userDetails.getUserId();
        for(Item item : itemList){
            if(item.getStatus() ==1 || item.getStatus() == 0){
                Boolean isScrab;
                if(scrabRepository.findByUserIdAndItemId(userId, item.getId()).isPresent()){
                    isScrab = true;
                }else {
                    isScrab = false;
                }
                List<Scrab> scrabs = scrabRepository.findAllByItemId(item.getId());
                int scrabCnt = 0;
                for(Scrab scrab : scrabs){
                    if(scrab.getScrab().equals(true)){
                        scrabCnt++;
                    }
                }
                ItemSearchResponseDto itemSearchResponseDto = new ItemSearchResponseDto(
                        item.getId(),
                        item.getCategory(),
                        item.getTitle(),
                        item.getContents(),
                        item.getItemImg().split(",")[0],
                        item.getAddress(),
                        scrabCnt,
                        item.getViewCnt(),
                        item.getStatus(),
                        isScrab);
                itemResponseDtos.add(itemSearchResponseDto);
            }
        }
        return itemResponseDtos;
    }
}

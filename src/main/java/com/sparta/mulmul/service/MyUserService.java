package com.sparta.mulmul.service;


import com.sparta.mulmul.dto.item.ItemUserResponseDto;
import com.sparta.mulmul.dto.scrab.MyScrabItemDto;
import com.sparta.mulmul.dto.user.MyPageResponseDto;
import com.sparta.mulmul.dto.user.UserEditDtailResponseDto;
import com.sparta.mulmul.dto.user.UserEditResponseDto;
import com.sparta.mulmul.dto.user.UserStoreResponseDto;
import com.sparta.mulmul.model.Item;
import com.sparta.mulmul.model.Report;
import com.sparta.mulmul.model.Scrab;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MyUserService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ScrabRepository scrabRepository;
    private final BagRepository bagRepository;
    private final ReportRepository reportRepository;

    // 성훈_마이페이지_내 정보보기
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        Long userId = userDetails.getUserId();

        // 한 유저의 모든 아이템을 보여줌
        List<Item> myItemList = itemRepository.findAllMyItem(userId);
        List<ItemUserResponseDto> myItemResponseList = addItemList(myItemList);
        // 스크랩 정도 넣어주기
        List<Scrab> myScrabList = scrabRepository.findTop3ByUserIdAndScrabOrderByModifiedAtDesc(userId, true);
        List<ItemUserResponseDto> myScrapItemList = addScrapItemList(myScrabList);
        // 보내줄 내용을 MyPageResponseDto에 넣어주기
        return new MyPageResponseDto(
                user.getNickname(),
                user.getProfile(),
                user.getDegree(),
                user.getGrade(),
                user.getAddress(),
                user.getStoreInfo(),
                myItemResponseList,
                myScrapItemList
        );
    }


    // 성훈_마이페이지_내 정보수정
    @Transactional
    public UserEditResponseDto editMyPage(String nickname, String address, String
            storeInfo, String imgUrl, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("user not found")
        );
        // 유저 정보 수정
        updateInfo(nickname, address, storeInfo, imgUrl, user);
        // 수정된 정보를 Response하기위해 정보를 넣어 줌
        UserEditDtailResponseDto userEditDtailResponseDto = new UserEditDtailResponseDto(
                nickname,
                imgUrl,
                address,
                storeInfo
        );

        // 요청값 반환
        return new UserEditResponseDto(true, userEditDtailResponseDto);
    }

    // 이승재 / 찜한 아이템 보여주기
    public List<MyScrabItemDto> scrabItem(UserDetailsImpl userDetails) {
        List<Scrab> scrabList = scrabRepository.findAllByUserIdOrderByModifiedAtDesc(userDetails.getUserId());

        List<MyScrabItemDto> myScrabItemDtoList = new ArrayList<>();
        for (Scrab scrab : scrabList) {
            if (scrab.getScrab().equals(true)) {
                Item item = itemRepository.findById(scrab.getItemId()).orElseThrow(
                        () -> new IllegalArgumentException("아이템 정보가 없습니다.")
                );
                Long itemId = item.getId();
                String title = item.getTitle();
                String contents = item.getContents();
                String image = item.getItemImg().split(",")[0];
                int status = item.getStatus();
                MyScrabItemDto myScrabItemDto = new MyScrabItemDto(itemId, title, contents, image, status);
                myScrabItemDtoList.add(myScrabItemDto);
            }
        }
        return myScrabItemDtoList;

    }

    // 이승재 / 유저 스토어 목록 보기
    public UserStoreResponseDto showStore(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("유저 정보가 없습니다.")
        );
        String nickname = user.getNickname();
        String profile = user.getProfile();
        float grade = user.getGrade();
        String degree = user.getDegree();
        String address = user.getAddress();
        String storeInfo = user.getStoreInfo();

        Long userBadId = bagRepository.findByUserId(userId).getId();
        List<Item> myItemList = itemRepository.findAllByBagId(userBadId);
        List<ItemUserResponseDto> itemUserResponseDtos = new ArrayList<>();

        for(Item item : myItemList){
            Long itemId = item.getId();
            String itemImg = item.getItemImg().split(",")[0];
            int status = item.getStatus();
            ItemUserResponseDto itemUserResponseDto = new ItemUserResponseDto(itemId, itemImg, status);
            itemUserResponseDtos.add(itemUserResponseDto);
        }

        return new UserStoreResponseDto(nickname, profile, degree, grade, address, storeInfo, itemUserResponseDtos);

    }


    // 이승재 / 유저 신고하기 기능
    @Transactional
    public String reportUser(Long userId, UserDetailsImpl userDetails) {
        Optional<Report> findReport = reportRepository.findByReporterIdAndReportedUserId(userDetails.getUserId(), userId);
        if (findReport.isPresent()) {
            return "false";
        } else {
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new IllegalArgumentException("유저 정보가 없습니다.")
            );
            int reportCnt = user.getReportCnt();

            user.reportCntUpdate(userId, reportCnt + 1);

            Report report = Report.builder()
                    .reportedUserId(userId)
                    .reporterId(userDetails.getUserId())
                    .build();

            reportRepository.save(report);
            if (user.getReportCnt() == 5) {
                user.banUser(userId, true);
            }
            return "true";
        }
    }


    // 한 유저의 모든 아이템을 보여줌
    private List<ItemUserResponseDto> addItemList(List<Item> myItemList) {
        List<ItemUserResponseDto> myItemResponseList = new ArrayList<>();
        // 내 보유 아이템을 리스트 형식으로 담기
        for (Item items : myItemList) {
            ItemUserResponseDto itemResponseDto = getItemUserDto(items);
            myItemResponseList.add(itemResponseDto);
        }
        return myItemResponseList;
    }

    // DTO에 아이템정보 넣기
    private ItemUserResponseDto getItemUserDto(Item scrabItem) {
        ItemUserResponseDto scrabitemDto = new ItemUserResponseDto(
                scrabItem.getId(),
                scrabItem.getItemImg().split(",")[0],
                scrabItem.getStatus()
        );
        return scrabitemDto;
    }

    // 스크랩 정도 넣어주기
    private List<ItemUserResponseDto> addScrapItemList(List<Scrab> myScrabList) {
        List<ItemUserResponseDto> myScrapItemList = new ArrayList<>();

        for (Scrab myscrap : myScrabList) {
            Long myScrapItemId = myscrap.getItemId();
            Item scrabItem = itemRepository.findById(myScrapItemId).orElseThrow(
                    () -> new IllegalArgumentException("Item not found"));
            ItemUserResponseDto scrabitemDto = getItemUserDto(scrabItem);
            myScrapItemList.add(scrabitemDto);
        }
        return myScrapItemList;
    }

    // 유저 정보 수정
    private void updateInfo(String nickname, String address, String storeInfo, String imgUrl, User user) {
        if (imgUrl.equals("empty")){
            user.execptImageUpdate(
                    nickname,
                    address,
                    storeInfo
            );
        }else {
            // 유저 정보를 수정
            user.update(
                    nickname,
                    imgUrl,
                    address,
                    storeInfo
            );
        }
    }
}
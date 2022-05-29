package com.sparta.mulmul.user;


import com.sparta.mulmul.item.itemDto.ItemUserResponseDto;
import com.sparta.mulmul.item.scrabDto.MyScrabItemDto;
import com.sparta.mulmul.user.userDto.MyPageResponseDto;
import com.sparta.mulmul.user.userDto.UserEditDtailResponseDto;
import com.sparta.mulmul.user.userDto.UserEditResponseDto;
import com.sparta.mulmul.user.userDto.UserStoreResponseDto;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.item.Item;
import com.sparta.mulmul.item.ItemRepository;
import com.sparta.mulmul.item.ScrabRepository;
import com.sparta.mulmul.model.Report;
import com.sparta.mulmul.item.Scrab;
import com.sparta.mulmul.repository.*;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.sparta.mulmul.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.sparta.mulmul.exception.ErrorCode.NOT_FOUND_USER;


@Service
@RequiredArgsConstructor
public class MyUserService {


    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ScrabRepository scrabRepository;
    private final BagRepository bagRepository;
    private final ReportRepository reportRepository;

    // 성훈_마이페이지_내 정보보기
    @Transactional
    @Cacheable(cacheNames = "userProfile", key = "#userDetails.userId")
    public MyPageResponseDto showMyPage(UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        Long userId = userDetails.getUserId();

        // 한 유저의 모든 아이템을 보여줌
        List<ItemUserResponseDto> myItemList = itemRepository.findByMyPageItems(userId);
        // 한 유저의 모든 아이템을 보여줌
        List<ItemUserResponseDto> myItemResponseList = addItemList(myItemList);

        // 스크랩 정도 넣어주기
        List<ItemUserResponseDto> myScrabList = itemRepository.findByMyScrabItems(userId);
        // 스크랩 정도 넣어주기
        List<ItemUserResponseDto> myScrapItemList = addItemList(myScrabList);

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

    // 이미지 파싱 대표이미지럴 넣어준다
    private List<ItemUserResponseDto> addItemList(List<ItemUserResponseDto> ItemList) {
        List<ItemUserResponseDto> myItemList = new ArrayList<>();

        for (ItemUserResponseDto eachItem : ItemList) {
            ItemUserResponseDto Item = new ItemUserResponseDto(
                    eachItem.getItemId(),
                    eachItem.getImage().split(",")[0],
                    eachItem.getStatus()
            );
            myItemList.add(Item);
        }
        return myItemList;
    }


    // 성훈_마이페이지_내 정보수정
    @Transactional
    @CacheEvict(cacheNames = "userProfile", key = "#userDetails.userId", allEntries = true)
    public UserEditResponseDto editMyPage(String nickname, String address, String
            storeInfo, String imgUrl, UserDetailsImpl userDetails) {
        User user = userRepository.findById(userDetails.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
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
                        () -> new CustomException(NOT_FOUND_ITEM)
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
                () -> new CustomException(NOT_FOUND_USER)
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

        for (Item item : myItemList) {
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
                    () -> new CustomException(NOT_FOUND_USER)
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


    // 유저 정보 수정
    private void updateInfo(String nickname, String address, String storeInfo, String imgUrl, User user) {
        if (imgUrl.equals("empty")) {
            user.execptImageUpdate(
                    nickname,
                    address,
                    storeInfo
            );
        } else {
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
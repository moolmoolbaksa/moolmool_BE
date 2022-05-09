package com.sparta.mulmul.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.mulmul.model.Image;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.ImageRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import jdk.internal.util.xml.impl.Input;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public List<String> uploadFile(List<MultipartFile> multipartFiles) {
        List<String> imageUrlList = new ArrayList<>();

        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch(IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
            }
            String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
            Image image = new Image(fileName, imgUrl);
            imageRepository.save(image);
            imageUrlList.add(imgUrl);
        });
        return imageUrlList;
    }

    public void deleteFile(String fileName) {
        amazonS3.deleteObject(new DeleteObjectRequest(bucket, fileName));
    }

    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 형식의 파일(" + fileName + ") 입니다.");
        }
    }


//     성훈 - user 프로필 수정하기기
//   public List<String> uploadFile(List<MultipartFile> multipartFile, UserDetailsImpl userDetails) {
//        List<String> imageUrlList = new ArrayList<>();
////        User user = userDetails.getUser;
//
//        // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
//        multipartFile.forEach(file -> {
//            String fileName = createFileName(file.getOriginalFilename());
//            ObjectMetadata objectMetadata = new ObjectMetadata();
//            objectMetadata.setContentLength(file.getSize());
//            objectMetadata.setContentType(file.getContentType());
//
//            // 이미지에 대한 url
//            String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
//
//            // 기존 ImageRepository에서 삭제
//            Long userId = userDetails.getUserId();
//            User user = userRepository.findById(userId).orElseThrow(()-> new IllegalArgumentException("user not found"));;
//
//            String userImgUrl = user.getProfile();
//
//           if (!userImgUrl.equals("http://kaihuastudio.com/common/img/default_profile.png")) {
//                Image nowImage = imageRepository.findByImgUrl(userImgUrl);
//                String nowFileName = nowImage.getFileName();
//                Long nowImgeId = nowImage.getId();
//                amazonS3.deleteObject(new DeleteObjectRequest(bucket, nowFileName));
//                imageRepository.deleteById(nowImgeId);
//            }
//
//            try(InputStream inputStream = file.getInputStream()) {
//                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
//                        .withCannedAcl(CannedAccessControlList.PublicRead));
//            } catch(IOException e) {
//                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
//            }
//
//            Image image = new Image(fileName, imgUrl);
//            imageRepository.save(image);
//            imageUrlList.add(imgUrl);
//        });
//
//
//        return imageUrlList;
//    }



    public String mypageUpdate(MultipartFile multipartFile, UserDetailsImpl userDetails){
        String fileName = createFileName(multipartFile.getOriginalFilename());
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        String imgUrl = amazonS3.getUrl(bucket, fileName).toString();

        Long userId = userDetails.getUserId();
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new IllegalArgumentException("user not found")
        );

        String userImgUrl = user.getProfile();

        if(!userImgUrl.equals("http://kaihuastudio.com/common/img/default_profile.png")){
            Image nowImage = imageRepository.findByImgUrl(userImgUrl);
            String nowFileName = nowImage.getFileName();
            Long nowImgId = nowImage.getId();
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, nowFileName));
            imageRepository.deleteById(nowImgId);
        }

        try(InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.");
        }

        Image image = new Image(fileName, imgUrl);
        imageRepository.save(image);
        return  imgUrl;
    }
}


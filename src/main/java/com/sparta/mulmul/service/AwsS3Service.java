package com.sparta.mulmul.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sparta.mulmul.exception.CustomException;
import com.sparta.mulmul.exception.ErrorCode;
import com.sparta.mulmul.model.Image;
import com.sparta.mulmul.model.User;
import com.sparta.mulmul.repository.ImageRepository;
import com.sparta.mulmul.repository.UserRepository;
import com.sparta.mulmul.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AwsS3Service {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    public List<String> uploadFile(List<MultipartFile> multipartFiles) {
        try {
            List<String> imageUrlList = new ArrayList<>();

            // forEach 구문을 통해 multipartFile로 넘어온 파일들 하나씩 fileNameList에 추가
            multipartFiles.forEach(file -> {
                String fileName = createFileName(file.getOriginalFilename());
                String fileFormatName = file.getContentType().substring(file.getContentType().lastIndexOf("/") + 1);


                // 이미지 리사이즈 작업
                MultipartFile resizedFile = resizeImage(fileName, fileFormatName, file, 768);

                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentLength(resizedFile.getSize());
                objectMetadata.setContentType(file.getContentType());

                try (InputStream inputStream = resizedFile.getInputStream()) {
                    amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
                } catch (IOException e) {
                    throw new CustomException(ErrorCode.FAILIED_UPLOAD_IMAGE);
                }
                String imgUrl = amazonS3.getUrl(bucket, fileName).toString();
                Image image = new Image(fileName, imgUrl);
                imageRepository.save(image);
                imageUrlList.add(imgUrl);
            });
            return imageUrlList;
        }catch (NullPointerException e){
            List<String> imageUrlList = new ArrayList<>();
            imageUrlList.add("null");
            return imageUrlList;
        }
    }

    MultipartFile resizeImage(String fileName, String fileFormatName, MultipartFile originalImage, int targetWidth) {
        try {
            // MultipartFile -> BufferedImage Convert
            BufferedImage inputImage = ImageIO.read(originalImage.getInputStream());
            int originWidth = inputImage.getWidth();
            if(originWidth < targetWidth )
                return originalImage;

            BufferedImage outputImage = new BufferedImage(targetWidth, targetWidth, inputImage.getType());
            Graphics2D graphics2D = outputImage.createGraphics();
            graphics2D.drawImage(inputImage, 0, 0, targetWidth, targetWidth, null);
            graphics2D.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(outputImage, fileFormatName, baos);
            baos.flush();
            return new MockMultipartFile(fileName, baos.toByteArray());
        } catch (IOException e) {
            throw new CustomException(ErrorCode.FAILIED_RESIZE_IMAGE);
        }
    }



    private String createFileName(String fileName) { // 먼저 파일 업로드 시, 파일명을 난수화하기 위해 random으로 돌립니다.
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) { // file 형식이 잘못된 경우를 확인하기 위해 만들어진 로직이며, 파일 타입과 상관없이 업로드할 수 있게 하기 위해 .의 존재 유무만 판단하였습니다.
        try {
            return fileName.substring(fileName.lastIndexOf("."));
        } catch (StringIndexOutOfBoundsException e) {
            throw new CustomException(ErrorCode.WRONG_TYPE_IMAGE);
        }
    }

    // 이승재 / 마이페이지 이미지 등록
    public String mypageUpdate(MultipartFile multipartFile, UserDetailsImpl userDetails) {
        if (multipartFile == null) {
            return "empty";
        } else {
            String fileName = createFileName(multipartFile.getOriginalFilename());
            String fileFormatName = multipartFile.getContentType().substring(multipartFile.getContentType().lastIndexOf("/")+1);


            // 이미지 리사이즈 작업
            MultipartFile resizedFile = resizeImage(fileName, fileFormatName, multipartFile, 768);

            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(resizedFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            String imgUrl = amazonS3.getUrl(bucket, fileName).toString();

            Long userId = userDetails.getUserId();
            User user = userRepository.findById(userId).orElseThrow(
                    () -> new CustomException(ErrorCode.NOT_FOUND_USER)
            );

            String userImgUrl = user.getProfile();

            Optional<Image> nowImage = imageRepository.findByImgUrl(userImgUrl);
            if (nowImage.isPresent()) {
                String nowFileName = nowImage.get().getFileName();
                Long nowImgId = nowImage.get().getId();
                amazonS3.deleteObject(new DeleteObjectRequest(bucket, nowFileName));
                imageRepository.deleteById(nowImgId);
            }

            try (InputStream inputStream = resizedFile.getInputStream()) {
                amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
            } catch (IOException e) {
                throw new CustomException(ErrorCode.FAILIED_UPLOAD_IMAGE);
            }

            Image image = new Image(fileName, imgUrl);
            imageRepository.save(image);
            return imgUrl;
        }
    }
}
package com.api.dex.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class S3 {
    private final AmazonS3Client amazonS3Client;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${cloud.aws.s3.bucket}")
    public String bucket;  // S3 버킷 이름

    @Value("${cloud.aws.s3.src}")
    public String s3Url;

    @Value("${file.path}")
    public String path;

    public String upload(MultipartFile multipartFile, String dirName, String realTime) throws IOException {
        File uploadFile = convert(multipartFile, realTime)  // 파일 변환할 수 없으면 에러
                .orElseThrow(() -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));

        return upload(uploadFile, dirName);
    }

    // S3로 파일 업로드하기
    private String upload(File uploadFile, String dirName) {
//        String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName();   // S3에 저장된 파일 이름
        String fileName = dirName + "/" + uploadFile.getName();   // S3에 저장된 파일 이름
        String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
        removeNewFile(uploadFile);
        return uploadImageUrl;
    }

    // S3로 업로드
    private String putS3(File uploadFile, String fileName) {
        amazonS3Client.putObject(new PutObjectRequest(bucket, fileName, uploadFile).withCannedAcl(CannedAccessControlList.PublicRead));
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // 로컬에 저장된 이미지 지우기
    private void removeNewFile(File targetFile) {
        if (targetFile.delete()) {
            logger.info("File delete success");
            return;
        }
        logger.info("File delete fail");
    }

    // 로컬에 파일 업로드 하기
    private Optional<File> convert(MultipartFile file, String realTime) throws IOException {
        File convertFile = new File(path + "/" + (realTime+file.getOriginalFilename()));
        if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
            try (FileOutputStream fos = new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }

        return Optional.empty();
    }

    // 버킷 리스트를 가져오는 메서드이다.
    public List<Bucket> getBucketList() {
        return amazonS3Client.listBuckets();
    }
    // 버킷을 생성하는 메서드이다.
    public Bucket createBucket(String bucketName) {
        return amazonS3Client.createBucket(bucketName);
    }

    // 폴더 생성 (폴더는 파일명 뒤에 "/"를 붙여야한다.)
    public void createFolder(String bucketName, String folderName) {
        amazonS3Client.putObject(bucketName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    // 파일 삭제
    public void fileDelete(String folderName, String fileName) {
        logger.info("fileDelete fileName : " + fileName);
        String imgName = folderName;

        if(fileName.length() > 0){
            imgName += "/" + (fileName).replace(File.separatorChar, '/');
        }

        logger.info("imgName : " + imgName);
        amazonS3Client.deleteObject(this.bucket, imgName);
        logger.info("삭제성공");
    }

    // 파일 URL
    public String getFileURL(String folderName, String fileName) {
        logger.info("넘어오는 파일명 : "+fileName);
        fileName = folderName + fileName;
        String imgName = (fileName).replace(File.separatorChar, '/');
        return amazonS3Client.generatePresignedUrl(new GeneratePresignedUrlRequest(bucket, imgName)).toString();
    }

    public String getSrc(String folderName, String fileName){
        return s3Url + folderName + "/" + fileName;
    }

    public void getObject(String folderName, String fileName){
        amazonS3Client.getObject(folderName, fileName);
    }
}

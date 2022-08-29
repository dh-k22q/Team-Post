package com.example.intermediate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.intermediate.controller.response.ResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket; //버킷이름

    @Value("${cloud.aws.s3.dir}")
    private String dir;

    private final AmazonS3Client s3Client;


    public ResponseDto<String> upload(InputStream inputStream, String originFileName, String fileSize) {
        //S3에 저장되는 파일의 이름이 중복되지 않기 위해서 UUID로 생성한 랜덤 값과 파일 이름을 연결하여 S3에 업로드
        String s3FileName = UUID.randomUUID() + "-" + originFileName;
        // Spring Server에서 S3로 파일을 업로드할때 파일 사이즈를 ContentLength로 S3에 알려주기 위해서 ObjectMetadata를 사용
        ObjectMetadata objMeta = new ObjectMetadata();
        objMeta.setContentLength(Long.parseLong(fileSize));
        // S3 API 메소드인 putObject를 이용하여 파일 Stream을 열어서 S3에 파일을 업로드
        s3Client.putObject(bucket, s3FileName, inputStream, objMeta);
        // getUrl 메소드를 통해서 S3에 업로드된 사진 URL을 가져오는 방식
        return ResponseDto.success(s3Client.getUrl(bucket, dir + s3FileName).toString());

    }

}





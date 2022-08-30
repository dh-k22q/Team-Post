package com.example.intermediate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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

    private final TokenProvider tokenProvider;

    private final AmazonS3Client s3Client;

    public ResponseDto<String > upload(MultipartFile multipartFile, HttpServletRequest request) throws IllegalAccessException {
        // 토큰 체크하기
        if (null == request.getHeader("Refresh-Token")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        //S3에 저장되는 파일의 이름이 중복되지 않기 위해서 UUID로 생성한 랜덤 값과 파일 이름을 연결하여 S3에 업로드
        String s3FileName = UUID.randomUUID() + "-" + multipartFile.getOriginalFilename();

        try (InputStream inputStream = multipartFile.getInputStream()) {
            s3Client.putObject(bucket, s3FileName, inputStream.toString());
        } catch(IOException e) {
            throw new IllegalAccessException(String.format("파일 변환에 실패했습니다", multipartFile.getOriginalFilename()));
        }
        return ResponseDto.success(s3Client.getUrl(bucket, dir + s3FileName).toString());
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("Refresh-Token"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}







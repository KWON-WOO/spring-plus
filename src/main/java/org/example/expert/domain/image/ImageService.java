package org.example.expert.domain.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    private final AmazonS3 amazonS3;
    private final ImageRepository repository;

    @Transactional
    public String addImage(Long id, MultipartFile file) {

        if (file.isEmpty()) throw new InvalidRequestException("업로드 할 파일이 없습니다");
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try(InputStream inputStream = file.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e){
            throw new ServerException("파일 업로드에 실패했습니다");
        }
        repository.save(new Image(id, fileName));
        return fileName;
    }

    @Transactional
    public String deleteImage(Long id) {
        Image image = repository.findById(id)
                .orElseThrow(()-> new InvalidRequestException("not found image"));
        String fileName = image.getImage();
        amazonS3.deleteObject((new DeleteObjectRequest(bucket, fileName)));
        repository.delete(image);
        return fileName;
    }
}

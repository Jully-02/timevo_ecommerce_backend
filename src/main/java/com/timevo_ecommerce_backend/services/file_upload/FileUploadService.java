package com.timevo_ecommerce_backend.services.file_upload;

import com.cloudinary.Cloudinary;
import com.timevo_ecommerce_backend.responses.CloudinaryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FileUploadService implements IFileUploadService{

    private final Cloudinary cloudinary;
    @Override
    @Transactional
    public CloudinaryResponse uploadFile(MultipartFile multipartFile, String fileName) throws Exception {
        try {
            Map result = cloudinary.uploader().upload(multipartFile.getBytes(), Map.of("public_id", "timevo/product/" + fileName));
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            return CloudinaryResponse.builder()
                    .publicId(publicId)
                    .url(url)
                    .build();
        } catch (Exception e) {
            throw new Exception("Failed to upload file");
        }
    }
}

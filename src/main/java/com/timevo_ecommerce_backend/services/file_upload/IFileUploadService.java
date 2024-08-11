package com.timevo_ecommerce_backend.services.file_upload;

import com.timevo_ecommerce_backend.responses.CloudinaryResponse;
import org.springframework.web.multipart.MultipartFile;

public interface IFileUploadService {
    CloudinaryResponse uploadFile (MultipartFile multipartFile, String fileName) throws Exception;
}

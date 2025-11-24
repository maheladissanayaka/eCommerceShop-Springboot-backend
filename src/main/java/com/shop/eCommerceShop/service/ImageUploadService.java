package com.shop.eCommerceShop.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String uploadImage(MultipartFile file) throws IOException;
    boolean deleteImage(String publicId) throws IOException;
}


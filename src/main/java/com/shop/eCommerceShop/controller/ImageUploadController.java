package com.shop.eCommerceShop.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.shop.eCommerceShop.responce.ApiResponse;
import com.shop.eCommerceShop.service.ImageUploadService;

@RestController
@RequestMapping("/api/admin/images")
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                ApiResponse response = new ApiResponse();
                response.setMessage("File is empty");
                response.setStatus(false);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            String imageUrl = imageUploadService.uploadImage(file);
            
            ApiResponse response = new ApiResponse();
            response.setMessage(imageUrl);
            response.setStatus(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IOException e) {
            ApiResponse response = new ApiResponse();
            response.setMessage("Failed to upload image: " + e.getMessage());
            response.setStatus(false);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}


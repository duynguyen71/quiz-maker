package com.nkd.quizmaker.helper;

import com.nkd.quizmaker.response.BaseResponse;
import com.nkd.quizmaker.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component("FileHelper")
@RequiredArgsConstructor
public class FileHelper {
    private final FileStorageService fileStorageService;


    public ResponseEntity<?> getImage(String name) {
        Resource resource = fileStorageService.loadFile(name);
        if(resource!=null)
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
        return ResponseEntity.badRequest().body(BaseResponse.badRequest("Could not found image with name: "+name));
    }
}

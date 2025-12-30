package org.example.expert.domain.image;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/images")
public class ImageController {
    private final ImageService service;
    @PostMapping
    public ResponseEntity<String> addImage(@AuthenticationPrincipal AuthUser user, MultipartFile file){
        return ResponseEntity.ok(service.addImage(user.getId(), file));
    }

    @DeleteMapping
    public ResponseEntity<String> deleteImage(@AuthenticationPrincipal AuthUser user) {
        return ResponseEntity.ok(service.deleteImage(user.getId()));
    }

}

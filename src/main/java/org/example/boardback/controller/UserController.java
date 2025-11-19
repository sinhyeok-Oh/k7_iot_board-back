package org.example.boardback.controller;

import lombok.RequiredArgsConstructor;
import org.example.boardback.common.apis.UserApi;
import org.example.boardback.entity.file.FileInfo;
import org.example.boardback.service.impl.ProfileServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(UserApi.ROOT)
@RequiredArgsConstructor
public class UserController {
    private final ProfileServiceImpl profileService;

    @PostMapping(UserApi.ME + "/profile")
    public ResponseEntity<?> uploadProfile(
//            @AuthenticationPrincipal Long userId,
            @RequestParam("file")MultipartFile file
    ) {
        Long userId = 1L;
        FileInfo saved = profileService.updateProfile(userId, file);
        return ResponseEntity.ok(saved);
    }
}
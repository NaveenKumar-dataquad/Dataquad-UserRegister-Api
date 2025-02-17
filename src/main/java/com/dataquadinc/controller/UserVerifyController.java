package com.dataquadinc.controller;

import com.dataquadinc.dto.ForgotResponseDto;
import com.dataquadinc.dto.UserVerifyDto;
import com.dataquadinc.service.UserVerifyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserVerifyController {

    private final UserVerifyService userService;

    public UserVerifyController(UserVerifyService userService) {
        this.userService = userService;
    }

    // Send OTP to Email
    @PostMapping("/send-otp")
    public ResponseEntity<ForgotResponseDto> sendOtp(@RequestParam String email) {
        ForgotResponseDto response = userService.sendOtp(email);
        return ResponseEntity.ok(response);
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<ForgotResponseDto> verifyOtp(@RequestBody UserVerifyDto userDTO) {
        String email = userDTO.getEmail();
        String otp = userDTO.getOtp();
        if (email == null || otp == null || email.isEmpty() || otp.isEmpty()) {
            return ResponseEntity.badRequest().body(new ForgotResponseDto(false, "Email and OTP are required", "Missing fields"));
        }
        ForgotResponseDto response = userService.verifyOtp(userDTO);
        return ResponseEntity.ok(response);
    }
}


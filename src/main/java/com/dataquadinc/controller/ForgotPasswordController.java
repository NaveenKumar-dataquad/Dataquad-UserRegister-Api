package com.dataquadinc.controller;

import com.dataquadinc.dto.ForgotPasswordDto;
import com.dataquadinc.service.ForgotPasswordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://35.188.150.92")
// Allow all origins

@RestController
@RequestMapping("/users")
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    // Forgot Password (Generate OTP)

    @PostMapping("/send-otp")

    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        String email = forgotPasswordDto.getEmail();
        System.out.println("Received email: " + email);

        // Validate email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email cannot be null or empty");
        }

        // Generate OTP
        try {
            String otp = forgotPasswordService.generateOtp(email);
            forgotPasswordDto.setOtp(otp);  // Store the OTP in the DTO
            return ResponseEntity.ok("OTP sent successfully. Please check your email.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating OTP: " + e.getMessage());
        }
    }

    // Verify OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        String email = forgotPasswordDto.getEmail();
        String otp = forgotPasswordDto.getOtp();

        if (email == null || otp == null || email.isEmpty() || otp.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email and OTP are required");
        }

        // Verify OTP
        boolean isOtpValid = forgotPasswordService.verifyOtp(email, otp);
        if (isOtpValid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP. Please try again.");
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        String email = forgotPasswordDto.getEmail();
        String updatePassword = forgotPasswordDto.getUpdatePassword();
        String confirmPassword = forgotPasswordDto.getConfirmPassword();

        // Validate the fields (check if they are not null or empty)
        // Validate Email
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email cannot be null or empty.");
        }

        // Validate Update Password
        if (updatePassword == null || updatePassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Update Password cannot be null or empty.");
        }

        // Validate Confirm Password
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Confirm Password cannot be null or empty.");
        }

        // Check if the password and confirm password match
        if (!updatePassword.equals(confirmPassword)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Password and Confirm Password do not match.");
        }

        try {
            // Update the password in the service
            forgotPasswordService.updatePassword(email, updatePassword);
            return ResponseEntity.ok("Password updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating password: " + e.getMessage());
        }
    }
}

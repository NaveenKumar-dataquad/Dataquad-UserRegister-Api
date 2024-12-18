package com.dataquadinc.controller;

import com.dataquadinc.dto.LoginDTO;
import com.dataquadinc.dto.LoginResponseDTO;
import com.dataquadinc.exceptions.InvalidCredentialsException;
import com.dataquadinc.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            LoginResponseDTO response = loginService.authenticate(loginDTO);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {

            LoginResponseDTO.ErrorDetails errorDetails = new LoginResponseDTO.ErrorDetails(
                    "300",
                    "Invalid credentials"
            );
            LoginResponseDTO errorResponse = new LoginResponseDTO(
                    false,
                    "Unsuccessful",
                    null,
                    errorDetails
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}

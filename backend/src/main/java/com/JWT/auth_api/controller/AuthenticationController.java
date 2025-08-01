package com.JWT.auth_api.controller;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.JWT.auth_api.model.AuthenticationResponse;
import com.JWT.auth_api.model.User;
import com.JWT.auth_api.repository.UserRepository;
import com.JWT.auth_api.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor // Lombok annotation to inject final fields
public class AuthenticationController {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authService;
    private final UserRepository userRepository;

    // ✅ User Registration (with OTP email trigger)
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody User request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // ✅ Login (JWT only if verified)
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }

    // ✅ OTP Verification
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean verified = authService.verifyOtp(email, otp);
        if (verified) {
            return ResponseEntity.ok("OTP verified successfully. You can now log in.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP or email.");
        }
    }

    // ✅ Face Registration: Save face vector for user
    @PostMapping("/face/register")
    public ResponseEntity<String> registerFace(@RequestParam String username, @RequestBody String faceVectorJson) {
        boolean ok = authService.saveFaceVector(username, faceVectorJson);
        logger.info("Entered to face check");
        if (ok) {
            return ResponseEntity.ok("Face vector saved.");
        } else {
            return ResponseEntity.badRequest().body("User not found or error saving face vector.");
        }
    }

    // ✅ Face Verification: Compare face vector for login
    @PostMapping("/face/verify")
    public ResponseEntity<String> verifyFace(
            @RequestParam String username,
            @RequestBody String faceVectorJson) {

        logger.info("Entered face verify");
//        logger.info("Username: {}", username);
//        logger.info("Received JSON: {}", faceVectorJson);

        try {
            // Parse the JSON string into a List<Double>
            String cleaned = faceVectorJson.replaceAll("[\\[\\]\\s]", "");
            String[] parts = cleaned.split(",");
            double[] vector = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                vector[i] = Double.parseDouble(parts[i]);
            }

            boolean match = authService.verifyFace(username, toList(vector));
            if (match) {
                return ResponseEntity.ok("Face verified.");
            } else {
                return ResponseEntity.status(401).body("Face not recognized.");
            }
        } catch (Exception e) {
            logger.error("Error parsing face vector: ", e);
            return ResponseEntity.badRequest().body("Invalid face vector format.");
        }
    }

    // Helper method to convert array to List<Double>
    private List<Double> toList(double[] array) {
        List<Double> list = new java.util.ArrayList<>();
        for (double v : array) {
            list.add(v);
        }
        return list;
    }


    // ✅ Health Check
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    // ✅ Fetch All Users (admin use)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }
}

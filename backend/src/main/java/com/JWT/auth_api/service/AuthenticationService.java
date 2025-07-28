package com.JWT.auth_api.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JWT.auth_api.model.AuthenticationResponse;
import com.JWT.auth_api.model.User;
import com.JWT.auth_api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // ✅ Lombok handles constructor injection
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService; // ✅ Injected properly

    // ✅ User Registration with OTP setup
    @Transactional
    public AuthenticationResponse register(User request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        // ✅ Generate and set OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setVerified(false); // user not verified initially

        user = repository.save(user); // Save user with OTP and unverified flag

        // ✅ Send OTP via email
        emailService.sendOtpEmail(user.getUsername(), otp);

        // ✅ Respond with message instead of JWT
        return new AuthenticationResponse("OTP sent to email. Please verify before logging in.", user.getRole().name());
    }

    // ✅ Authenticate only if OTP verified
    public AuthenticationResponse authenticate(User request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = repository.findByUsername(request.getUsername())
                              .orElseThrow(() -> new RuntimeException("User not found"));

        // ✅ Block login if not verified
        if (!user.isVerified()) {
            throw new RuntimeException("Email not verified. Please verify using OTP.");
        }

        String token = jwtService.generateToken(user);
        return new AuthenticationResponse(token, user.getRole().name());
    }

    // ✅ Verify OTP and activate account
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOpt = repository.findByUsername(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (otp.equals(user.getOtp())) {
                user.setVerified(true);
                user.setOtp(null); // Clear OTP after successful verification
                repository.save(user);
                return true;
            }
        }
        return false;
    }

    // ✅ Utility: Generate 6-digit numeric OTP
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }
}

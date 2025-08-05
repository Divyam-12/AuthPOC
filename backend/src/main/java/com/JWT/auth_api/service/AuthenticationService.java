package com.JWT.auth_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.JWT.auth_api.exception.CustomAuthException;
import com.JWT.auth_api.model.AuthenticationResponse;
import com.JWT.auth_api.model.User;
import com.JWT.auth_api.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 5;

    // ✅ Register with OTP and timestamp
    @Transactional
    public AuthenticationResponse register(User request) {
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedAt(LocalDateTime.now());
        user.setVerified(false);
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);

        repository.save(user);
        emailService.sendOtpEmail(user.getUsername(), otp);

        return new AuthenticationResponse(
            "OTP sent to email. Please verify before logging in.",
            user.getRole().name()
        );
    }

    // ✅ Login with rate limiting + OTP verification + token
    public AuthenticationResponse authenticate(User request) {
        System.out.println("Authenticating: " + request.getUsername());

        User user = repository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomAuthException("User not found"));

        // 🚫 Check if account is locked
        if (user.getLockUntil() != null && user.getLockUntil().isAfter(LocalDateTime.now())) {
            throw new CustomAuthException("Account is temporarily locked. Try again at: " + user.getLockUntil());
        }

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
        } catch (BadCredentialsException e) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setLockUntil(LocalDateTime.now().plusMinutes(LOCK_DURATION_MINUTES));
            }

            repository.save(user);
            throw new CustomAuthException("Invalid username or password");
        }

        // ✅ Check if user is verified
        if (!user.isVerified()) {
            throw new CustomAuthException("Email not verified. Please verify using OTP.");
        }

        // ✅ Reset failed attempt counters
        user.setFailedLoginAttempts(0);
        user.setLockUntil(null);
        repository.save(user);

        String token = jwtService.generateToken(user);
        System.out.println("Token issued: " + token);
        return new AuthenticationResponse(token, user.getRole().name());
    }

    // ✅ OTP verification with expiry
    @Transactional
    public boolean verifyOtp(String email, String otp) {
        Optional<User> userOpt = repository.findByUsername(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (user.getOtpGeneratedAt() == null ||
                user.getOtpGeneratedAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
                return false; // OTP expired
            }

            if (otp.equals(user.getOtp())) {
                user.setVerified(true);
                user.setOtp(null);
                user.setOtpGeneratedAt(null);
                repository.save(user);
                return true;
            }
        }
        return false;
    }

    // ✅ Generate 6-digit OTP
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    // ✅ Save face vector during registration
    @Transactional
    public boolean saveFaceVector(String username, String faceVectorJson) {
        Optional<User> userOpt = repository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setFaceVectorJson(faceVectorJson);
            repository.save(user);
            return true;
        }
        return false;
    }

    // ✅ Verify face during login using cosine similarity
    public boolean verifyFace(String username, List<Double> faceVectorJson) {
        Optional<User> userOpt = repository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String stored = user.getFaceVectorJson();
            if (stored == null || stored.isEmpty()) return false;

            try {
                double[] v1 = parseJsonArray(stored);
                double[] v2 = faceVectorJson.stream().mapToDouble(Double::doubleValue).toArray();
                double similarity = cosineSimilarity(v1, v2);
                return similarity > 0.7; // Adjust threshold as needed
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    // ✅ Convert stored vector string to array
    private double[] parseJsonArray(String json) {
        json = json.replaceAll("[\\[\\]\\s]", "");
        String[] parts = json.split(",");
        double[] arr = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            arr[i] = Double.parseDouble(parts[i]);
        }
        return arr;
    }

    // ✅ Cosine similarity calculation
    private double cosineSimilarity(double[] a, double[] b) {
        if (a.length != b.length) return -1;
        double dot = 0, normA = 0, normB = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

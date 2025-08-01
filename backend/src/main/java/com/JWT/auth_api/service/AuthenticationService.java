	package com.JWT.auth_api.service;
	
	import java.util.List;
	import java.util.Optional;
	import java.util.Random;
	
	import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
	    	System.out.println("Authenticating: " + request.getUsername());
	    	try {
	    	    authenticationManager.authenticate(
	    	        new UsernamePasswordAuthenticationToken(
	    	            request.getUsername(),
	    	            request.getPassword()
	    	        )
	    	    );
	    	} catch (BadCredentialsException e) {
	    		System.out.println("Invalid credentials");
	    	    throw new RuntimeException("Invalid username or password");
	    	}
	
	        User user = repository.findByUsername(request.getUsername())
	                              .orElseThrow(() -> new RuntimeException("User not found"));
	
	        // ✅ Block login if not verified
	        if (!user.isVerified()) {
	        	System.out.println("User not verified: " + request.getUsername());
	            throw new RuntimeException("Email not verified. Please verify using OTP.");
	        }
	
	        String token = jwtService.generateToken(user);
	        System.out.println("Token issued: " + token);
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
	
	    // --- Save face vector for user (registration) ---
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
	
	    // --- Verify face vector for user (login) ---
	    public boolean verifyFace(String username, List<Double> faceVectorJson) {
	        
	        Optional<User> userOpt = repository.findByUsername(username);
	        if (userOpt.isPresent()) {
	            User user = userOpt.get();
	            String stored = user.getFaceVectorJson();
	            if (stored == null || stored.isEmpty()) return false;
	            try {
	                // Parse JSON arrays
	                double[] v1 = parseJsonArray(stored);
	                double[] v2 = faceVectorJson.stream().mapToDouble(Double::doubleValue).toArray();
	                System.out.println("Stored vector: " + java.util.Arrays.toString(v1));
	                System.out.println("Received vector: " + java.util.Arrays.toString(v2));
	                System.out.println("Vector lengths: " + v1.length + " vs " + v2.length);
	                double similarity = cosineSimilarity(v1, v2);
	                System.out.println("Cosine similarity: " + similarity);
	                boolean flag = (similarity > 0.7); // realistic threshold
	                System.out.println("Face match result: " + flag);
	                return flag;
	            } catch (Exception e) {
	                e.printStackTrace();
	                return false;
	            }
	        }
	        return false;
	    }
	
	    // --- Parse JSON array string to double[] ---
	    private double[] parseJsonArray(String json) {
	        json = json.replaceAll("[\\[\\]\\s]", "");
	        String[] parts = json.split(",");
	        double[] arr = new double[parts.length];
	        for (int i = 0; i < parts.length; i++) {
	            arr[i] = Double.parseDouble(parts[i]);
	        }
	        return arr;
	    }
	
	    // --- Cosine similarity ---
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

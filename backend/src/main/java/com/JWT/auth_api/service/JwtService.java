package com.JWT.auth_api.service;

import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.JWT.auth_api.model.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	private final String SECRET_KEY="07525b98fa75f994c7038a53783899e85f39b9bbd55a27e8f4ed2f7a5573a4fd";
	
	public <T> T extractClaim(String token,Function<Claims,T> resolver)
	{
		Claims claims=extractAllClaims(token);
		return resolver.apply(claims);
	}
	
	public boolean isValid(String token, UserDetails user)
	{
		String username=extractUsername(token);
		return username.equals(user.getUsername()) && !isTokenExpired(token);
	}
	
	
	
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}
	
	private Date extractExpiration(String token)
	{
		return extractClaim(token, Claims::getExpiration);
	}

	public String extractUsername(String token)
	{
		return extractClaim(token, Claims::getSubject);
	}
	
	private Claims extractAllClaims(String token)
	{
		return Jwts
				.parser()
				.verifyWith(getSigninKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}
	
	public String generateToken(User user)
	{
		String token=Jwts
				.builder()
				.subject(user.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis()+24*60*60*1000))
				.signWith(getSigninKey())
				.compact();
		return token;
	}
	
	private SecretKey getSigninKey()
	{
		byte[] keyBytes=Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}

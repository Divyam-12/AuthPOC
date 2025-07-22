package com.JWT.auth_api.model;

public class AuthenticationResponse {
	private String token;
	//new
	private String role;
	//new
	
	//old
//	public AuthenticationResponse(String token)
//	{
//		this.token=token;
//	}
	
	//new
	public AuthenticationResponse(String token, String role) {
	    this.token = token;
	    this.role = role;
	}
	//new
	
	public String getToken()
	{
		return token;
	}

	//new
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}
	//new
}

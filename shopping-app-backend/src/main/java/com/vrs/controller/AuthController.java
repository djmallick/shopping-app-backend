package com.vrs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vrs.exception.ResourceNotFoundException;
import com.vrs.security.JwtAuthRequest;
import com.vrs.security.JwtAuthResponse;
import com.vrs.security.JwtTokenHelper;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private JwtTokenHelper jwtTokenHelper;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@PostMapping("/login")
	public ResponseEntity<JwtAuthResponse> createToken(@RequestBody JwtAuthRequest jwtAuthRequest) {
		this.authenticate(jwtAuthRequest.getUsername(), jwtAuthRequest.getPassword());
		UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtAuthRequest.getUsername());
		String generatedToken = jwtTokenHelper.generateToken(userDetails);
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		jwtAuthResponse.setToken(generatedToken);
		return new ResponseEntity<JwtAuthResponse>(jwtAuthResponse, HttpStatus.OK);	
	}

	private void authenticate(String username, String password) {
			UsernamePasswordAuthenticationToken autheticationToken = new UsernamePasswordAuthenticationToken(username, password);
//			try {
			this.authenticationManager.authenticate(autheticationToken);
//			}catch(Exception e) {
//				e.printStackTrace();
//			}
	}
	
}

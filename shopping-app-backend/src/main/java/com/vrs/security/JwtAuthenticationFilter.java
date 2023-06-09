package com.vrs.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtTokenHelper jwtTokenHelper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		1. get token
		String requestToken = request.getHeader("Authorization");
		
//		2. fetch username from token
		String username = null;
		String token = null;
		if(requestToken!=null && requestToken.startsWith("Bearer")) {
			token = requestToken.substring(7);
			try {
				username = jwtTokenHelper.getUsernameFromToken(token);
			} catch(IllegalArgumentException e) {
				System.out.println("Unable to get Jwt token");
			} catch(ExpiredJwtException e) {
				System.out.println("JWT token expired");
			} catch(MalformedJwtException e) {
				System.out.println("Invalid JWT");
			}
		} else {
			System.out.println("JWT token not start with Bearer");			
		}
		
		
//		3. Once we get the token and fetch username from token we need to validate it and if it's validate we will set authentication in context
		
		if(username!= null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			if(jwtTokenHelper.validateToken(token, userDetails)) {
				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//				System.out.println(SecurityContextHolder.getContext().getAuthentication().getName());
				
			} else {
				System.out.println("Invalid jwt token");
			}
		}else {
			System.out.println("User name is null or context is not null");
		}
		
		filterChain.doFilter(request, response);
	}

}

package de.mpg.mpdl.auth.web.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

public class CORSFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://localhost:3000");
		// System.out.println("CORS " + request.getMethod());
		if ("OPTIONS".equals(request.getMethod())) {
			response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET, POST, PUT, DELETE");
			response.addHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "Token");
			response.addHeader(HttpHeaders.ACCESS_CONTROL_MAX_AGE, "1");
		}

		filterChain.doFilter(request, response);
	}
}

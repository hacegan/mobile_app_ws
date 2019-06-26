package com.app.ws.mobileappws.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.app.ws.mobileappws.SpringApplicationContext;
import com.app.ws.mobileappws.service.UserService;
import com.app.ws.mobileappws.shared.dto.UserDto;
import com.app.ws.mobileappws.ui.model.request.UserLoginRequestModel;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {
 private final AuthenticationManager authenticationManager;

 public AuthenticationFilter(AuthenticationManager authenticationManager) {
  this.authenticationManager = authenticationManager;
 }

 @Override
 public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
 throws AuthenticationException {
  try {

   UserLoginRequestModel creds = new ObjectMapper().readValue(req.getInputStream(),
    UserLoginRequestModel.class);

   return authenticationManager.authenticate(
    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(), new ArrayList < > ()));

  } catch (IOException e) {
   throw new RuntimeException(e);
  }
 }

 @Override
 protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
  Authentication auth) throws IOException, ServletException { // giriş başarılıysa

  String userName = ((User) auth.getPrincipal()).getUsername();

  String token = Jwts.builder().setSubject(userName)
   .setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
   .signWith(SignatureAlgorithm.HS512, SecurityConstants.getTokenSecret()).compact();

  UserService userService = (UserService) SpringApplicationContext.getBean("userServiceImpl"); // auth filterı
  // @bean diyip
  // kullanamıyacağımız
  // icin impl
  // yapılacak yerde
  // impl yapılacak
  // olan
  // classı
  // appcontextden
  // getiriyoruz.

  UserDto userDto = userService.getUser(userName);

  res.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX + token);

  res.addHeader("UserId", userDto.getUserId()); // userid üzerinden işlem yapıcagımız icin headera ekliyoruz.
 }

 @Override
 protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
  AuthenticationException failed) { // giriş başarısızsa bunu override etmessek status code 403 forbiden
  // dönücek.
  final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
  LOGGER.severe("giris basarisiz");
 }

}

package com.app.ws.mobileappws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.app.ws.mobileappws.security.AppProperties;

@SpringBootApplication
public class MobileAppWsApplication {

 public static void main(String[] args) {
  SpringApplication.run(MobileAppWsApplication.class, args);
 }

 @Bean
 public BCryptPasswordEncoder bCryptPasswordEncoder() {
  return new BCryptPasswordEncoder();
 }

 @Bean
 public SpringApplicationContext springApplicationContext() {
  return new SpringApplicationContext();
 }

 @Bean // appcontextden get bean diyip alacagımız yapılar icin burada @bean tanımlaması
 // yapmamız gerekiyor.
 public AppProperties getAppProperties() {
  return new AppProperties();
 }

}

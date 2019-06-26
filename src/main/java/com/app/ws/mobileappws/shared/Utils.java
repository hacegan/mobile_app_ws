package com.app.ws.mobileappws.shared;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public class Utils {

	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	public String generateUserId(int length) {// Kaç karakterlik üreteceğimiz bilgisi geliyor.
		return generateRandomString(length);
	}

	public String generateAddressId(int length) {// Kaç karakterlik üreteceğimiz bilgisi geliyor.
		return generateRandomString(length);
	}

	private String generateRandomString(int length) {
		StringBuilder returnValue = new StringBuilder(length);

		for (int i = 0; i < length; i++) {// length uzunlugunda rastgele alfabeden secip id olusturuyoruz.
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}

		return new String(returnValue);
	}

}

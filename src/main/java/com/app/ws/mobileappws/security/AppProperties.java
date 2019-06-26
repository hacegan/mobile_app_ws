package com.app.ws.mobileappws.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

	@Autowired
	Environment env;// app properties dosyasını okumak icin kullanıcaz

	public String getTokenSecret() {
		return env.getProperty("tokenSecret");
	}

}

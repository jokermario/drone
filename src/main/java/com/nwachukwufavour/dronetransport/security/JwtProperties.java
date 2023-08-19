package com.nwachukwufavour.dronetransport.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
@Data
public class JwtProperties {
    private String secretKey = "flzxsqcysyhljtxdfgthitcbvb";

    //validity in milliseconds
    private long validityInMs = 3600000; // this is 1h
}

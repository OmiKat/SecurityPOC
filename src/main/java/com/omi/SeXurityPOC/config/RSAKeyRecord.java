package com.omi.SeXurityPOC.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@ConfigurationProperties(prefix = "jwt")
public record RSAKeyRecord(RSAPrivateKey rsaPrivateKey , RSAPublicKey rsaPublicKey) {
}

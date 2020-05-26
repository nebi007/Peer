package com.example.bloodbank.configs;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;

@Component
public class KeyManager {

    @Value("${key.private}")
    private String privateKeyEncoded;

    @Value("${key.public}")
    public String publicKeyEncoded;

    public PrivateKey getPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(privateKeyEncoded);

        PKCS8EncodedKeySpec formatted_private = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("ECDSA",new BouncyCastleProvider());
        return kf.generatePrivate(formatted_private);
    }

    public PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String str_key=publicKeyEncoded;
        byte[] byte_pubkey  = Base64.getDecoder().decode(str_key);
        KeyFactory factory = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        PublicKey public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        return public_key;
    }

    public static PublicKey decodePubliKey(String pbKeyEncoded) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String str_key=pbKeyEncoded;
        byte[] byte_pubkey  = Base64.getDecoder().decode(str_key);
        KeyFactory factory = KeyFactory.getInstance("ECDSA", new BouncyCastleProvider());
        PublicKey public_key = (ECPublicKey) factory.generatePublic(new X509EncodedKeySpec(byte_pubkey));
        return public_key;
    }

    public static PrivateKey decodePrivateKey(String privateKetEncodedString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decodedKey = Base64.getDecoder().decode(privateKetEncodedString);

        PKCS8EncodedKeySpec formatted_private = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("ECDSA",new BouncyCastleProvider());
        return kf.generatePrivate(formatted_private);
    }


    }




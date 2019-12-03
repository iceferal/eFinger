package com.iceferal.efinger;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class Protection extends AppCompatActivity {

    Cipher cipher;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA512";
    private static final int keySize = 256;
    private static final int pswdIterations = 1024;
    private final String Salt;
    private SecretKey secretKey;
    String initializationVector;

    Protection(String Salt, String password, String initializationVector) throws InvalidKeySpecException, NoSuchAlgorithmException {
        this.Salt = Salt;
        secretKey = generateKey(password);
        this.initializationVector = initializationVector;   }

    private SecretKey generateKey(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] saltByte;
        saltByte = Salt.getBytes(StandardCharsets.UTF_8);
        KeySpec kSpec = new PBEKeySpec(password.toCharArray(), saltByte, pswdIterations, keySize);
        SecretKey tmpSecretKey = SecretKeyFactory.getInstance(secretKeyInstance).generateSecret(kSpec);
        return new SecretKeySpec(tmpSecretKey.getEncoded(), "AES");    }

    public String encrypt(String toEncrypt) throws Exception {
        cipher = Cipher.getInstance(cypherInstance);
        byte[] ivBytes = Arrays.copyOfRange(initializationVector.getBytes(StandardCharsets.UTF_8), 0, 16);
        AlgorithmParameterSpec spec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] cipherText = cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherText);    }

    public String decrypt(String toDecrypt) throws Exception {
        cipher = Cipher.getInstance(cypherInstance);
        byte[] ivBytes = Arrays.copyOfRange(initializationVector.getBytes(StandardCharsets.UTF_8), 0, 16);
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] decryptedBytes = cipher.doFinal(
                Base64.getDecoder().decode(toDecrypt)        );
        return new String(decryptedBytes, StandardCharsets.UTF_8);     }
}
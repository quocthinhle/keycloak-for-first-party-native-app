package com.github.quocthinhle.authorizationchallenge.utils;

import org.jose4j.jwe.ContentEncryptionAlgorithmIdentifiers;
import org.jose4j.jwe.JsonWebEncryption;
import org.jose4j.jwe.KeyManagementAlgorithmIdentifiers;
import org.jose4j.keys.AesKey;
import org.jose4j.lang.JoseException;

import javax.crypto.SecretKey;
import java.security.Key;

public class DeviceSessionParser {
    // Since JWT is the parent of JWE and JWS
    //  JWT: jsonwebtoken
    //      JWS: json web signature, a jwt is signed called jws (ensure data integrity, payload content can be inspected)
    //      JWE: json web encryption, aead with json data structures (use for exchanging data without worrying about sniff)

    /**
     * How JWE works, we generate a random content encryption key, then encrypt the cek with shared symmetric-key / the
     * recipient's public key with some algorithm defined in the jwe header (1), then we encrypt the content with an IV
     * (initialize vector (3)) & the encrypted cek (2), which produces a ciphertext (4). So to ensure the JWE's consistency
     * we use an authentication tag (which is the Authentication Tag output from the encryption operation) (5).
     *
     * In compact form, we have Base64Url(1).Base64Url(2).Base64Url(3).Base64Url(4).Base64Url(5)
     */

    public static String encryptSession(byte[] keyContent, byte[] content) throws JoseException {
        Key key = new AesKey(keyContent);
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(key);
        jwe.setPlaintext(content);

        return jwe.getCompactSerialization();
    }

    public static String encryptSession(SecretKey secretKey, byte[] content) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setKey(secretKey);
        jwe.setPlaintext(content);

        return jwe.getCompactSerialization();
    }

    public static byte[] parseSession(SecretKey secretKey, String compactSerializationContent) throws JoseException {
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setKey(secretKey);
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setCompactSerialization(compactSerializationContent);

        return jwe.getPlaintextBytes();
    }

    public static byte[] parseSession(byte[] keyContent, String compactSerializationContent) throws JoseException {
        Key key = new AesKey(keyContent);
        JsonWebEncryption jwe = new JsonWebEncryption();

        jwe.setKey(key);
        jwe.setAlgorithmHeaderValue(KeyManagementAlgorithmIdentifiers.A128KW);
        jwe.setEncryptionMethodHeaderParameter(ContentEncryptionAlgorithmIdentifiers.AES_128_CBC_HMAC_SHA_256);
        jwe.setCompactSerialization(compactSerializationContent);

        return jwe.getPlaintextBytes();
    }

}

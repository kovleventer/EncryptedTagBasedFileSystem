package com.kovlev.etbf.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**
 * Helper class for encrypting and decrypting blocks
 * Unsafe, but whatever
 */
public class EncryptionUtils {

    private static final String ALGO = "AES";

    /**
     * Encrypts a byte array
     * @param data The data to encrypt
     * @param password The password to encrypt with
     * @return The encrypted byte array
     * @throws ETBFSException
     */
    public static byte[] encrypt(byte[] data, String password) throws ETBFSException {
        Key key = generateKey(password);
        Cipher c = null;
        try {
            c = Cipher.getInstance(ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {}

        try {
            c.init(Cipher.ENCRYPT_MODE, key);
            return c.doFinal(data);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new ETBFSException("Invalid key on encryption");
        }
    }

    /**
     * Decrypts a byte array
     * @param data The data to encrypt
     * @param password The password to decrypt with
     * @return The decrypted byte array
     * @throws ETBFSException
     */
    public static byte[] decrypt(byte[] data, String password) throws ETBFSException {
        Key key = generateKey(password);
        Cipher c = null;
        try {
            c = Cipher.getInstance(ALGO);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ignored) {}

        try {
            c.init(Cipher.DECRYPT_MODE, key);
            return c.doFinal(data);
        } catch (IllegalBlockSizeException | InvalidKeyException | BadPaddingException e) {
            //e.printStackTrace();
            throw new ETBFSException("Invalid key on decryption");
        }
    }

    /**
     * Generates a key from the password
     * UNSAFE AS HELL
     * @param password The password
     * @return
     */
    private static Key generateKey(String password) {
        int keylen = 32;
        byte[] key = new byte[keylen];
        byte[] pswd = password.getBytes();
        System.arraycopy(pswd, 0, key, 0, pswd.length);
        return new SecretKeySpec(key, ALGO);
    }
}

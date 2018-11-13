package com.kovlev.etbf.util;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtils {

    private static final String ALGO = "AES";


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

    private static Key generateKey(String password) {
        int keylen = 32;
        byte[] key = new byte[keylen];
        byte[] pswd = password.getBytes();
        System.arraycopy(pswd, 0, key, 0, pswd.length);
        return new SecretKeySpec(key, ALGO);
    }
}

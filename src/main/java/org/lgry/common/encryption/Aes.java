package org.lgry.common.encryption;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class Aes {
    public static final int IV_LENGTH = 12;   // GCM 권장
    public static final int TAG_LENGTH = 128; // bits

    private static final SecureRandom RANDOM = new SecureRandom();

    public static byte[] encrypt(byte[] plain, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
        return cipher.doFinal(plain);
    }

    public static byte[] decrypt(byte[] cipherText, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_LENGTH, iv));
        return cipher.doFinal(cipherText);
    }

    public static byte[] newIv() {
        byte[] iv = new byte[IV_LENGTH];
        RANDOM.nextBytes(iv);
        return iv;
    }
}

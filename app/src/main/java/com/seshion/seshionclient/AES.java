package com.seshion.seshionclient;

import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AES {

    /* private instance so that it can only be accessed by getInstance() method */
    //private static AES instance;

    /* AES */
    private byte[] aesKey = null;
    private byte[] iv = null;
    SecretKey secretKey;
    private Cipher encryptionCipher;
    private Cipher decryptionCipher;

    /* an empty constructor */
    //private AES() {
    public AES(){

        /* AES symmetric encryption key - uses a strong psuedo-random number generator */
        SecureRandom secureRandom = new SecureRandom();
        aesKey = new byte[16];
        secureRandom.nextBytes(aesKey);

        /* initialize secret key object */
        secretKey = new SecretKeySpec(aesKey, "AES");
        System.out.println("key:" + aesKey);

        /* create initialation vector */
        iv = new byte[12];
        secureRandom.nextBytes(iv);
    }

    /* this constructor takes the key as an argument (only for decryption) */
    private AES(byte[] symmetricKey) {
        this.aesKey = symmetricKey;
    }

    /* synchronized method to control simultaneous access */
//    synchronized public static AES getInstance(){
//        if(instance == null){
//            /* if instance is null initialize, otherwise return instance */
//            instance = new AES();
//        }
//        return instance;
//    }
//
//    /* synchronized method to control simultaneous access with param */
//    synchronized public static AES getInstance(byte[] symmetricKey){
//        if(instance == null){
//            /* if instance is null initialize, otherwise return instance */
//            instance = new AES(symmetricKey);
//        }
//        return instance;
//    }

    public byte[] decrypt(byte[] toBeDecrypted) {
        byte[] originalData = null;

        ByteBuffer byteBuffer = ByteBuffer.wrap(toBeDecrypted);
        int ivLength = byteBuffer.getInt();
        if(ivLength < 12 || ivLength >=16) {
            throw new IllegalArgumentException("invalid iv length");
        }

        /* create initialation vector */
        iv = new byte[ivLength];
        byteBuffer.get(iv);
        byte[] cipherText = new byte[byteBuffer.remaining()];
        byteBuffer.get(cipherText);

        try {
            decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
            decryptionCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesKey, "AES"), new GCMParameterSpec(128, iv));
            originalData = decryptionCipher.doFinal(cipherText);


        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return originalData;
    }

    public byte[] encrypt(byte[] toBeEncrypted) {
        byte[] encryptedData = null;



        /* encrypt the data */
        byte[] encryptedByteArray;
        try {

            /* initialize encryption cipher */
            encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);

            /* this does the encryption itself */
            encryptedByteArray = encryptionCipher.doFinal(toBeEncrypted);

            /* now we concatenate the encrypted bytes, the iv, and the iv length to a single byte array */
            ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + encryptedByteArray.length);
            byteBuffer.putInt(iv.length);
            byteBuffer.put(iv);
            byteBuffer.put(encryptedByteArray);
            encryptedData = byteBuffer.array();

        } catch (IllegalBlockSizeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (BadPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



        return encryptedData;
    }

    /* good practice to wipe the cryptographic key or iv from memory
    as fast as possible */
    //Arrays.fill(key, (byte) 0);

    public void setSymmetricKey(byte[] symmetricKey) {
        this.aesKey = symmetricKey;
    }

    public byte[] getSymmetricKey() {
        return this.aesKey;
    }

}

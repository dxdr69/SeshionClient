package com.seshion.seshionclient;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

public class RSA {

    /* private instance so that it can only be accessed by getInstance() method */
    //private static RSA instance;

    private KeyPairGenerator keyGen;
    private KeyPair pair;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Cipher decryptionCipher;
    private Cipher encryptionCipher;

    /* initialize KeyPairGenerator with RSA and given length */
    /* initialize KeyPairGenerator with RSA and given length */
    //private RSA()  {
    public RSA() {

        try {
            this.keyGen = KeyPairGenerator.getInstance("RSA");

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

//    synchronized public static RSA getInstance(){
//        if (instance == null){
//            instance = new RSA();
//        }
//        return instance;
//    }

    public void generateKeys(int keyLength){

        this.keyGen.initialize(keyLength);

        /* generate the key pair */
        this.pair = this.keyGen.generateKeyPair();
        this.privateKey = pair.getPrivate();
        this.publicKey = pair.getPublic();
    }

    /**
     * decrypt - decrypts using optimal asymmetric encryption padding
     * @param toBeDecrypted - the byte array to be decrypted
     * @return - the decrypted byte array
     */
    public byte[] decrypt(byte[] toBeDecrypted) {
        byte[] decryptedData = null;

        try {

            decryptionCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            OAEPParameterSpec oaepParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            decryptionCipher.init(Cipher.DECRYPT_MODE, this.privateKey, oaepParameterSpec);
            decryptedData = decryptionCipher.doFinal(toBeDecrypted);

            /* log */
            System.out.println("Decrypted symmetric key:" + decryptedData);
            System.out.println("Decrypted symmetric key length:" + decryptedData.length);


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

        return decryptedData;
    }

    public byte[] encrypt(byte[] toBeEncrypted) {
        byte[] encryptedData = null;

        try {

            /* initialize cipher with optimal asymmetric padding */
            encryptionCipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            OAEPParameterSpec oapParameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
            encryptionCipher.init(Cipher.ENCRYPT_MODE, this.publicKey, oapParameterSpec);
            encryptedData = encryptionCipher.doFinal(toBeEncrypted);

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


        return encryptedData;
    }

    public PublicKey decodePublicKey(byte[] encodedKeyBytes) {
        PublicKey rsaPublicKey = null;

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            rsaPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKeyBytes));

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return rsaPublicKey;
    }

    public void setPublicKey(PublicKey pubKey) {
        this.publicKey = pubKey;
    }

    public PrivateKey getPrivateKey() {
        return this.privateKey;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }
}

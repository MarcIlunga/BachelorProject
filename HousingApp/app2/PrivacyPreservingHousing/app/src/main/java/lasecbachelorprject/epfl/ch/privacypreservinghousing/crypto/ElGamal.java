package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

public class ElGamal {


    private  static BigInteger prime,group,generator, privateKey, publicKey;
    private static final int certainty = 300;
    private static final String config = "myElgamalConfig.txt";
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = ONE.add(ONE);
    private static final BigInteger THREE = TWO.add(ONE);
    private static ElGamal cryptoSystem;
    private SecureRandom secureRandom;

    //TODO: fix group, generator. Add method for secret key
    
    private ElGamal(){
        prime = Application.prime;
        group = Application.group;
        generator = Application.generator;
        this.secureRandom = new SecureRandom();
    }

    public static ElGamal getElGamal(){
        if (cryptoSystem == null){
            cryptoSystem = new ElGamal();
        }
        return cryptoSystem;
    }


    public BigInteger[] encrypt(BigInteger message){
        if(message == null) {
            throw new IllegalArgumentException("Message to encrypt is null");
        }
        BigInteger r = new BigInteger(group.bitLength(), secureRandom);
        r = r.mod(group);
        BigInteger gPowMessage = generator.modPow(message,prime);
        BigInteger yPowR = publicKey.modPow(r, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowR).mod(prime);
        cipher[1] = generator.modPow(r, prime);
        return cipher;
    }

    public void setPrivateKey(BigInteger privateKey){
        if(privateKey == null){
            throw  new IllegalArgumentException("Null Argument to Set private key");
        }

        this.privateKey = privateKey;
        publicKey = generator.modPow(privateKey,prime);
    }

    public BigInteger decrypt(BigInteger c0, BigInteger c1){
        if(c0 == null || c1 == null)
            throw new IllegalArgumentException("The couple to decrypt contains a null argument");
        BigInteger c = c1.modPow(privateKey, prime).modInverse(prime);
        BigInteger encryptedBit = c0.multiply(c).mod(prime);
        return encryptedBit.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public BigInteger decrypt(BigInteger[] cypher){
        if(cypher == null || cypher.length != 2|| cypher[0] == null|| cypher[1] == null ){
            throw new IllegalArgumentException("null or bad length cypher");
        }

        BigInteger msg = (cypher[0].divide(cypher[1].modPow(privateKey,prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public BigInteger getPublicKey(){
        return  publicKey;
    }

    public MyElGamalEncrypter getEncrypter() {
        return new MyElGamalEncrypter(prime, generator, publicKey);
    }
    public MyElGamalDecrypter getDecrypter() {
        return new MyElGamalDecrypter(prime, privateKey);
    }

}
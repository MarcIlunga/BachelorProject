package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

public class ElGamal {


    private  static BigInteger prime,group,generator, privateKey, publicKey;
    private static final BigInteger ONE = BigInteger.ONE;
    private static ElGamal cryptoSystem;
    private static SecureRandom secureRandom;

    //TODO: fix group, generator. Add method for secret key
    
    private ElGamal(BigInteger prime, BigInteger group, BigInteger generator){
        this.prime = prime;
        this.group = group;
        this.generator = generator;
        secureRandom = new SecureRandom();
    }

    public static ElGamal getElGamal(BigInteger prime, BigInteger group, BigInteger generator){
        if (cryptoSystem == null){
            cryptoSystem = new ElGamal(prime, group, generator);
        }
        return cryptoSystem;
    }


    public static BigInteger[] encrypt(BigInteger bit){
        if(bit == null) {
            throw new IllegalArgumentException("Message to encrypt is null");
        }
        BigInteger r = new BigInteger(group.bitLength(), secureRandom);
        r = r.mod(group);
        BigInteger gPowMessage = generator.modPow(bit,prime);
        BigInteger yPowR = publicKey.modPow(r, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowR).mod(prime);
        cipher[1] = generator.modPow(r, prime);
        return cipher;
    }

    public static BigInteger[][] encryptMany(BigInteger[] bits){
        BigInteger[][] res = new BigInteger[bits.length][2];
        for (int i = 0; i <bits.length ; i++) {
            res[i] = encrypt(bits[i]);
        }
        return res;
    }

    public void setPrivateKey(BigInteger privateKey){
        if(privateKey == null){
            throw  new IllegalArgumentException("Null Argument to Set private key");
        }

        this.privateKey = privateKey;
        publicKey = generator.modPow(privateKey,prime);
    }


    public BigInteger decrypt(BigInteger[] cypher){
        if(cypher == null || cypher.length != 2|| cypher[0] == null|| cypher[1] == null ){
            throw new IllegalArgumentException("null or bad length cypher");
        }

        BigInteger msg = (cypher[0].multiply(cypher[1].modPow(privateKey.negate(),prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public BigInteger[] decryptMany(BigInteger[][] table){
        BigInteger[] res = new BigInteger[table.length];
        for (int i = 0; i < res.length ; i++) {
            res[i] = decrypt(table[i]);
        }
        return res;
    }

    public BigInteger getPublicKey(){
        return  publicKey;
    }

    public MyElGamalEncrypter getEncrypter() {
        return new MyElGamalEncrypter(prime, generator, publicKey);
    }

    public static BigInteger[] homomorphicEncryption(Collection<BigInteger[]> ciphers){
        BigInteger[] res = new  BigInteger[]{BigInteger.ONE,BigInteger.ONE};
        for (BigInteger[] c: ciphers) {
            res = SecureDotProductParty.vectorsElemMultMod(res,c,prime);
        }
        return res;
    }

    public static BigInteger[] getNegativeciphers(BigInteger[] cypher){
        return SecureDotProductParty.vectorsElemModExpo(cypher,BigInteger.valueOf(Long.parseLong("-1")),prime);
    }

    public static BigInteger[] homomorphicEncryption(BigInteger[] c1, BigInteger[] c2){
        return SecureDotProductParty.vectorsElemMultMod(c1,c2,prime);
    }

    public static BigInteger[] homomorphicEncryption(BigInteger[]... ciphers){
        BigInteger[] res = new BigInteger []{BigInteger.ONE, BigInteger.ONE};
        for(BigInteger[] c : ciphers){
            res = homomorphicEncryption(res,c);
        }
        return res;
    }

    public static BigInteger[] multHomomorphicEncryption(BigInteger[]cipher, BigInteger otherWord){
       return SecureDotProductParty.vectorsElemModExpo(cipher,otherWord,prime);
    }


    public static BigInteger[][] encryptManyWithKey(BigInteger[] bits, BigInteger y) {
        BigInteger[][] res = new BigInteger[bits.length][2];
        for (int i = 0; i <bits.length ; i++) {
            res[i] = encrypt(bits[i]);
        }
        return res;
    }
}
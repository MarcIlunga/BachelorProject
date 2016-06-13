package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

public class ElGamal {


    private static BigInteger prime;
    private static BigInteger group;
    private static BigInteger generator;
    private BigInteger privateKey;
    private BigInteger publicKey;
    private     SecureRandom secureRandom;
    private     BigInteger commonKey;
    private static SecureRandom rnd;


    
    public ElGamal(BigInteger prime, BigInteger group, BigInteger generator, SecureRandom random){
        this.prime = prime;
        this.group = group;
        this.generator = generator;
        secureRandom = random;
        rnd = random;
    }





    public     BigInteger[] encrypt(BigInteger bit){
        if(bit == null) {
            throw new IllegalArgumentException("Message to encrypt is null");
        }
        BigInteger r = new BigInteger(group.bitLength(), secureRandom);
        r = r.mod(group);
        BigInteger gPowMessage = generator.modPow(bit,prime);
        BigInteger yPowR = commonKey.modPow(r, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowR).mod(prime);
        cipher[1] = generator.modPow(r, prime);
        return cipher;
    }

    public     BigInteger[][] encryptMany(BigInteger[] bits){
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

    public void setCommonKey(BigInteger commonKey){
        this.commonKey = commonKey;
    }


    public BigInteger decrypt(BigInteger[] cypher){
        if(cypher == null || cypher.length != 2|| cypher[0] == null|| cypher[1] == null ){
            throw new IllegalArgumentException("null or bad length cypher");
        }

        BigInteger msg = (cypher[0].multiply(cypher[1].modPow(privateKey.negate(),prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }


    public List<BigInteger> decryptMany(List<BigInteger[]> table){
        ArrayList<BigInteger> res = new ArrayList<>(table.size());
        for (BigInteger[] t: table) {
            res.add(decrypt(t));
        }
        return res;
    }

    public     BigInteger getPublicKey(){
        return publicKey;
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

    public BigInteger getPrime(){
        return prime;
    }

    public BigInteger getGenerator(){
        return getGenerator();
    }

    public BigInteger getGroup(){
        return group;
    }

    public BigInteger[] chainedDecription(BigInteger[] cipher){
        checkCipher(cipher);
        BigInteger[] res = new BigInteger[2];
        BigInteger r = new BigInteger(group.bitLength(),secureRandom);
        r = r.mod(group);
        BigInteger ct = cipher[0].multiply(cipher[1].modPow(privateKey,prime).modInverse(prime));
        res[0] = ct.modPow(r,prime);
        res[1] = cipher[1].modPow(r,prime);
        return res;
    }

    private void checkCipher(BigInteger[] cipher) {
        if(cipher.length != 2){
            throw new IllegalArgumentException("Ciphers should have exactely two elements. i.e (c,c')");
        }
        if(cipher[0] == null){
            throw new IllegalArgumentException("First element of cipher is null");
        }
        if(cipher[1] == null){
            throw new IllegalArgumentException("Second element of cipher is null");
        }
    }

    //TODO: REMOVE
    public static BigInteger[] encryptWithKey(BigInteger v, BigInteger y) {
        BigInteger r = new BigInteger(group.bitLength(), rnd);
        r = r.mod(group);
        BigInteger gPowMessage = generator.modPow(v,prime);
        BigInteger yPowR = y.modPow(r, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowR).mod(prime);
        cipher[1] = generator.modPow(r, prime);
        return cipher;
    }

    public static BigInteger decryptWithKey(BigInteger[] cipher, BigInteger privkey) {
        BigInteger msg = (cipher[0].multiply(cipher[1].modPow(privkey.negate(),prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public static BigInteger[] decryptManyWithKey(BigInteger[][] ciphers, BigInteger key) {
        BigInteger[] res = new BigInteger[ciphers.length];
        for (int i = 0; i <ciphers.length; i++) {
            res[i] = decryptWithKey(ciphers[i],key);
        }
        return res;
    }
}
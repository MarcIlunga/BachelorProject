package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collection;

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
    private static SecureRandom secureRandom;

    //TODO: fix group, generator. Add method for secret key
    
    private ElGamal(BigInteger prime, BigInteger group, BigInteger generator){
        this.prime = prime;
        this.group = Application.group;
        this.generator = Application.generator;
        secureRandom = new SecureRandom();
    }

    public static ElGamal getElGamal(BigInteger prime, BigInteger group, BigInteger generator){
        if (cryptoSystem == null){
            cryptoSystem = new ElGamal(prime, group, generator);
        }
        return cryptoSystem;
    }


    public static BigInteger[] encrypt(BigInteger message){
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

    public static BigInteger[] homomorphicEncryption(Collection<BigInteger[]> ciphers){
        BigInteger[] res = new  BigInteger[]{BigInteger.ONE,BigInteger.ONE};
        for (BigInteger[] c: ciphers) {
            res = SecureDotProductParty.vectorsElemMult(res,c);
        }
        return res;
    }

    public static BigInteger[] getNegativeciphers(BigInteger[] cypher){
        return SecureDotProductParty.vectorsElemModExpo(cypher,BigInteger.valueOf(Long.parseLong("-1")),prime);
    }

    public static BigInteger[] homomorphicEncryption(BigInteger[] c1, BigInteger[] c2){
        return SecureDotProductParty.vectorsElemMult(c1,c2);
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


}
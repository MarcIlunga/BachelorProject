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


    private static BigInteger prime,group,generator, privateKey, myPublicKey;
    private static final BigInteger ONE = BigInteger.ONE;
    private static ElGamal cryptoSystem;
    private static SecureRandom secureRandom;
    private static BigInteger commonKey;
    //TODO: Remove after test
    private static Map<Participant, BigInteger> mySecretKey;
    private static Map<Participant, BigInteger> myPukey;

    //TODO: fix group, generator. Add method for secret key
    
    private ElGamal(BigInteger prime, BigInteger group, BigInteger generator){
        mySecretKey = new HashMap<>();
        myPukey= new HashMap<>();
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
        BigInteger yPowR = commonKey.modPow(r, prime);
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

    public static  void setPrivateKey(BigInteger privateKey){
        if(privateKey == null){
            throw  new IllegalArgumentException("Null Argument to Set private key");
        }

        ElGamal.privateKey = privateKey;
        myPublicKey = generator.modPow(privateKey,prime);
    }

    public static void setMySKey(Participant p, BigInteger key){
        mySecretKey.put(p,key);
        myPukey.put(p,generator.modPow(key,prime));

    }

    public static BigInteger getMyPuKey(Participant p){
        return myPukey.get(p);
    }

    public static void setCommonKey(BigInteger commonKey){
        ElGamal.commonKey = commonKey;
    }


    public static BigInteger decrypt(BigInteger[] cypher){
        if(cypher == null || cypher.length != 2|| cypher[0] == null|| cypher[1] == null ){
            throw new IllegalArgumentException("null or bad length cypher");
        }

        BigInteger msg = (cypher[0].multiply(cypher[1].modPow(privateKey.negate(),prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public static BigInteger decryptMe(Participant p, BigInteger[] cypher){
        if(cypher == null || cypher.length != 2|| cypher[0] == null|| cypher[1] == null ){
            throw new IllegalArgumentException("null or bad length cypher");
        }
        setPrivateKey(mySecretKey.get(p));
        BigInteger msg = (cypher[0].multiply(cypher[1].modPow(privateKey.negate(),prime))).mod(prime);

        return msg.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }



    public static List<BigInteger> decryptMany(List<BigInteger[]> table){
        ArrayList<BigInteger> res = new ArrayList<>(table.size());
        for (BigInteger[] t: table) {
            res.add(decrypt(t));
        }
        return res;
    }

    public static Collection<BigInteger> decryptManyME(Participant p,Collection<BigInteger[]> table){
        ArrayList<BigInteger> res = new ArrayList<>(table.size());
        for (BigInteger[] t: table) {
            res.add(decryptMe(p,t));
        }
        return res;
    }

    public static BigInteger getmyPublicKey(){
        return  myPublicKey;
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

    public static BigInteger getPrime(){
        return prime;
    }

    public static BigInteger getGenerator(){
        return getGenerator();
    }

    public static BigInteger getGroup(){
        return group;
    }

}
package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import android.util.Log;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

public class ElGamal {


    private BigInteger prime, generator, privateKey, publicKey, pMinus2;
    private SecureRandom secureRandom;
    private static final int certainty = 300;
    private static final String config = "myElgamalConfig.txt";
    private static final BigInteger ZERO = BigInteger.ZERO;
    private static final BigInteger ONE = BigInteger.ONE;
    private static final BigInteger TWO = ONE.add(ONE);
    private static final BigInteger THREE = TWO.add(ONE);

    //TODO: fix group, generator. Add method for secret key
    public ElGamal(int keySize){
        secureRandom = new SecureRandom();
        PrimeGenerator primeGenerator = new PrimeGenerator(keySize, certainty,secureRandom, true);
        prime = primeGenerator.getPrime();
        pMinus2 = prime.subtract(TWO);
        generator = primeGenerator.getGenerator();
        BigInteger pmt = prime.subtract(THREE);
        privateKey = (new BigInteger(prime.bitLength(), secureRandom)).mod(pmt).add(TWO);
        publicKey = generator.modPow(privateKey, prime);
        saveConfig();
    }

    


    private void saveConfig(){
        try {
            PrintWriter out = new PrintWriter(new FileWriter((config)));
            out.println(prime.toString(16));
            out.println(generator.toString(16));
            out.println(privateKey.toString(16));
            out.close();
        }
         catch (IOException e) {
             Log.d("elgamalConfig", e.getMessage());
        }
    }

    public BigInteger getPrime(){
        return prime;
    }

    public BigInteger getGenerator(){
        return generator;
    }

    public BigInteger getPublicKey(){
        return publicKey;
    }

    public BigInteger[] encrypt(BigInteger message){
        BigInteger k = new BigInteger(prime.bitLength(), secureRandom);
        k = k.mod(pMinus2).add(ONE);
        BigInteger gPowMessage = generator.modPow(message,prime);
        BigInteger yPowK = publicKey.modPow(k, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowK).mod(prime);
        cipher[1] = generator.modPow(k, prime);
        return cipher;
    }

    public BigInteger decrypt(BigInteger c0, BigInteger c1){
        BigInteger c = c1.modPow(privateKey, prime).modInverse(prime);
        BigInteger encryptedBit = c0.multiply(c).mod(prime);
        return encryptedBit.equals(BigInteger.ONE) ? BigInteger.ZERO : BigInteger.ONE;
    }

    public BigInteger[] encryptWithKey(BigInteger message, BigInteger key){
        BigInteger k = new BigInteger(prime.bitLength(), secureRandom);
        k = k.mod(pMinus2).add(ONE);
        BigInteger gPowMessage = generator.modPow(message,prime);
        BigInteger yPowK = key.modPow(k, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowK).mod(prime);
        cipher[1] = generator.modPow(k, prime);
        return cipher;
    }
    public MyElGamalEncrypter getEncrypter() {
        return new MyElGamalEncrypter(prime, generator, publicKey);
    }
    public MyElGamalDecrypter getDecrypter() {
        return new MyElGamalDecrypter(prime, privateKey);
    }

}
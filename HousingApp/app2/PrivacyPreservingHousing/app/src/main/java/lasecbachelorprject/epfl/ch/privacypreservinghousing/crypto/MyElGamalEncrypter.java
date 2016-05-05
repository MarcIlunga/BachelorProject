package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class MyElGamalEncrypter {
    private BigInteger p, g, r, pMinus2;
    private SecureRandom srng;
    private static final BigInteger ONE = BigInteger.ONE, TWO = ONE.add(ONE);
    //Assume p is prime, g is a gen mod p, r = g^a mod p (a = pvt key)
    public MyElGamalEncrypter(BigInteger p, BigInteger g, BigInteger r) {
        srng = new SecureRandom();
        this.p = p; this.g = g; this.r = r;
        pMinus2 = p.subtract(TWO);
        System.out.println("Encryption key:");
        System.out.println("p = " + p.toString(16));
        System.out.println("g = " + g.toString(16));
        System.out.println("r = " + r.toString(16));
    }
    public byte[] transform(byte[] msg) {
        long startTm = System.currentTimeMillis();
        int blkSz = (p.bitLength() - 1)/8;
        byte[][] ba = Transformer.block(Transformer.pad(msg, blkSz), blkSz);
        byte[][] ba2 = new byte[2*ba.length][];
        BigInteger m, c0, c1, k;
        System.err.println("" + ba.length + " blocks");
        for (int i=0; i<ba.length; i++) {
            m = new BigInteger(1, ba[i]); //make +ve BigInt out of current blk
            k = new BigInteger(p.bitLength(), srng);

            k = k.mod(pMinus2).add(ONE); //rndm k, 0 < k < p-1
            c0 = g.modPow(k, p); //compute ElGamal transform
            c1 = r.modPow(k, p).multiply(m).mod(p);
            ba2[2*i] = Transformer.getBytes(c0); // convert to bytes
            ba2[2*i+1] = Transformer.getBytes(c1);
            if (i%10 == 0) System.err.print("\rBlock " + i);
        }
        System.err.println("\rEncryption took " +
                (System.currentTimeMillis()-startTm) + " ms");
        return Transformer.unblock(ba2, blkSz+1);
    }


}

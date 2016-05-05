package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;

public class MyElGamalDecrypter extends Transformer {
    private BigInteger p, a;

    //Assume p is prime
    public MyElGamalDecrypter(BigInteger p, BigInteger a) {
        this.p = p;
        this.a = a;
        System.out.println("Decryption key:");
        System.out.println("p = " + p.toString(16));
        System.out.println("a = " + a.toString(16));
    }

    public byte[] transform(byte[] msg) {
        long startTm = System.currentTimeMillis();
        int blkSz = (p.bitLength() - 1) / 8 + 1;
        byte[][] ba2 = Transformer.block(msg, blkSz);
        byte[][] ba = new byte[ba2.length / 2][];
        BigInteger m, c0, c1, c;
        System.err.println("" + ba.length + " blocks");
        for (int i = 0; i < ba.length; i++) {
            c0 = new BigInteger(1, ba2[2 * i]); //make +ve BigInts out of
            c1 = new BigInteger(1, ba2[2 * i + 1]); //current blocks
            c = c0.modPow(a, p).modInverse(p); //c0^-a mod p
            m = c.multiply(c1).mod(p); //recover plain "text"
            ba[i] = Transformer.getBytes(m); //convert to bytes
            if (i % 10 == 0) System.err.print("\rBlock " + i);
        }
        System.err.println("\nDecryption took " +
                (System.currentTimeMillis() - startTm) + " ms");
        return Transformer.unpad(Transformer.unblock(ba, blkSz - 1), blkSz - 1);
    }
}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ZeroKnowledgeVerifier {
    private BigInteger y;
    private BigInteger h;
    private BigInteger z;
    private BigInteger prime, group, generator, qMinusOne;
    private BigInteger c;
    private SecureRandom secureRandom;

    public ZeroKnowledgeVerifier(BigInteger prime, BigInteger group, BigInteger generator){
        this.prime = prime;
        this.generator = generator;
        this.group = group;
        this.qMinusOne = group.subtract(BigInteger.ONE);
        secureRandom = new SecureRandom();
    }

    public BigInteger sendC(){
        c = new BigInteger(group.bitLength(), secureRandom);
        return  c = c.mod(group);

    }



    public void receiveH(BigInteger h) {
        this.h = h;
    }

    public void receiveZ(BigInteger z) {
        this.z = z;
    }

    public void setY(BigInteger y) {
        this.y = y;
    }


    public boolean verifyWithZ(BigInteger z, BigInteger sharedC) {
        BigInteger gPowZ = generator.modPow(z,prime);
        BigInteger hTimesYPowC = (h.multiply(y.modPow(sharedC, prime))).mod(prime);
        return  gPowZ.equals(hTimesYPowC);
    }
}

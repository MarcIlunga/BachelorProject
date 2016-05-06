package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ZeroKnowledgeVerifier {
    private BigInteger y;
    private BigInteger h;
    private BigInteger z;
    private BigInteger group, generator, pMinusOne;
    private BigInteger c;
    private SecureRandom secureRandom;

    public ZeroKnowledgeVerifier(BigInteger y, BigInteger group, BigInteger generator){
        this.y = y;
        this.generator = generator;
        this.group = group;
        this.pMinusOne = group.subtract(BigInteger.ONE);
        secureRandom = new SecureRandom();
    }

    public BigInteger sendC(){
        c = new BigInteger(group.bitLength(), secureRandom);
        return  c = c.mod(pMinusOne);

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
        BigInteger gPowZ = generator.modPow(z,group);
        BigInteger hTimesYPowC = (h.multiply(y.modPow(sharedC, group))).mod(group);
        return  gPowZ.equals(hTimesYPowC);
    }
}

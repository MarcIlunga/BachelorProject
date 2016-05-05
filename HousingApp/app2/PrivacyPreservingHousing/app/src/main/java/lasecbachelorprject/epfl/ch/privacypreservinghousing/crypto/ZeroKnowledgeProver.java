package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ZeroKnowledgeProver {

    private BigInteger x;
    private BigInteger h;
    private BigInteger z;
    private BigInteger group, generator,pMinusOne;
    private SecureRandom secureRandom;
    public BigInteger r;

    //TODO: Group should be the published key
    public ZeroKnowledgeProver(BigInteger x, BigInteger group, BigInteger generator){
        this.x = x;
        this.group = group;
        this.generator = generator;
        this.pMinusOne = group.subtract(BigInteger.ONE);
        secureRandom = new SecureRandom();
    }

    public void initiateProof(){
        generateH();
    }
    private void generateH(){
        r = new BigInteger(group.bitLength(),secureRandom);
        r = r.mod(pMinusOne);
        h = generator.modPow(r, group);
    }

    public void sendH(ZeroKnowledgeVerifier verifier){
        verifier.receiveH(h);
    }

    private void computeZ(BigInteger c){
        z = (r.add(x.multiply(c))).mod(pMinusOne);
    }
    public void sendZ(ZeroKnowledgeVerifier verifier){
        verifier.receiveZ(z);
    }


    public void receiveC(BigInteger c) {
        computeZ(c);
    }

    public void setX(BigInteger x){
        this.x = x;
    }
}

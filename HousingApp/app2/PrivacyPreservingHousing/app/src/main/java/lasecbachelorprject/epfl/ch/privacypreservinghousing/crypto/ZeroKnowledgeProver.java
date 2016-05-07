package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class ZeroKnowledgeProver {

    private BigInteger x;
    private BigInteger h;
    private BigInteger z;
    private BigInteger prime,group, generator,pMinusOne;
    private SecureRandom secureRandom;
    public BigInteger r;

    //TODO: Group should be the published key
    public ZeroKnowledgeProver(BigInteger prime, BigInteger group, BigInteger generator){
        this.prime = prime;
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
        r = r.mod(group);
        h = generator.modPow(r, prime);
    }

    public void sendH(ZeroKnowledgeVerifier verifier){
        verifier.receiveH(h);
    }

    private void computeZ(BigInteger c){
        z = (r.add(x.multiply(c))).mod(group);
    }

    public BigInteger sendZ(BigInteger c){
        computeZ(c);
        return z;
    }


    public void receiveC(BigInteger c) {
        computeZ(c);
    }

    public void setX(BigInteger x){
        this.x = x;
    }
}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;


import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.Group;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeProver;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeVerifier;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.Group.getGroup;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.copyBigIntArrayToIntArray;

public class Participant extends Person implements User {

    private final BigInteger prime;
    private final BigInteger generator;
    public SecureDotProductParty secureDotProduct;
    public BigInteger[] wPrimeVector;
    private String[] binaryWprime;
    private BigInteger privateKey;
    private BigInteger publicKey;
    public ZeroKnowledgeProver prover;
    public ZeroKnowledgeVerifier verifier;
    private SecureRandom random;
    private Group group;
    int replyEqSize;
    int replyGrSize;
    private BigInteger gain;
    private BigInteger[][] cypher;


    public Participant(BigInteger replyGr[], BigInteger[] replyEq){
        replyEqSize = replyEq.length;
        replyGrSize = replyGr.length;
        wPrimeVector = new BigInteger[replyGrSize+2*replyEqSize];
        generateWPrimeVector(replyGr, replyEq);
        secureDotProduct = new SecureDotProductParty();
        secureDotProduct.setMyvector(wPrimeVector);
        group = getGroup();
        prime  = group.getPrime();
        generator = group.getGenerator();
        random = new SecureRandom();

        prover = new ZeroKnowledgeProver(BigInteger.ZERO,prime,generator);
        verifier = new ZeroKnowledgeVerifier(BigInteger.ZERO, prime, generator);
    }

    private void generateWPrimeVector(BigInteger replyGr[], BigInteger[] replyEq) {
        copyBigIntArrayToIntArray(replyGr,0,wPrimeVector,0,replyGrSize);

        BigInteger[] v = new BigInteger[replyEqSize];

        for (int i = 0; i < replyEqSize; i++) {
            v[i] = replyEq[i].pow(2);
        }
        copyBigIntArrayToIntArray(v,0,wPrimeVector,replyGrSize,replyEqSize);
        copyBigIntArrayToIntArray(replyEq,0,wPrimeVector,replyEqSize+replyEqSize,replyEqSize);
    }

    public BigInteger[] getReplyVector() {
        return wPrimeVector;
    }


    public void convertGain(int l) {
        BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        gain = secureDotProduct.getBeta().add(two.pow(l));
    }

    public void generatePrivateKey(){
        privateKey = (new BigInteger(prime.bitLength(),random)).mod(prime);
        publicKey = generator.modPow(privateKey,prime);
        DataBase.getDataBase().publishElGamalPublicKey(this, publicKey);
        prover.setX(privateKey);
    }


    public void setKeyToVerify(BigInteger key) {
        verifier.setY(key);
    }

    public void createBinaryGain(){

        binaryWprime = (gain.toString(2)).split("");
    }

    public void encryptWithcommonkey(){
        cypher = new BigInteger[binaryWprime.length][2];
        for (int i = 0; i <binaryWprime.length ; i++)
            cypher[i] = encryptMessage(binaryWprime[i]);

    }

    public BigInteger [] encryptMessage(String s){
        BigInteger k = new BigInteger(prime.bitLength(), new SecureRandom());
        k = k.mod(prime);
        BigInteger gPowMessage = generator.modPow(BigInteger.valueOf(Long.parseLong(s)),prime);
        BigInteger yPowK = publicKey.modPow(k, prime);
        BigInteger[] cipher = new BigInteger[2];
        cipher[0] = gPowMessage.multiply(yPowK).mod(prime);
        cipher[1] = generator.modPow(k, prime);
        return cipher;

    }

}

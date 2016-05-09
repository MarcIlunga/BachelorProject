package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.GainComparisonHelper;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeProver;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeVerifier;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;

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
    private BigInteger group;
    int replyEqSize;
    int replyGrSize;
    private BigInteger gain;
    private BigInteger[][] cypher;
    private int myCandidateNumber;
    private List<List<BigInteger[][]>> list;
    private GainComparisonHelper comparisonTool = GainComparisonHelper.getGainComparisonTool();


    public Participant(BigInteger replyGr[], BigInteger[] replyEq){
        replyEqSize = replyEq.length;
        replyGrSize = replyGr.length;
        wPrimeVector = new BigInteger[replyGrSize+2*replyEqSize];
        generateWPrimeVector(replyGr, replyEq);
        secureDotProduct = new SecureDotProductParty();
        secureDotProduct.setMyvector(wPrimeVector);
        group = Application.group;
        prime  = Application.prime;
        generator = Application.generator;
        random = new SecureRandom();

        prover = new ZeroKnowledgeProver(prime, group,generator);
        verifier = new ZeroKnowledgeVerifier(prime,group, generator);
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
            cypher[i] = ElGamal.encrypt(BigInteger.valueOf(Long.parseLong(binaryWprime[i])));

    }


    public void setMyCandidateNumber (int number){
        myCandidateNumber = number;
    }

    public void sendEncryptedComparisonToDB(){
        DataBase.pushComparisonVector(this,comparisonTool.getEncryptedComparisons());
    }

    public void getEpsilonVector(List<List<BigInteger[][]>> list){
        this.list = comparisonTool.processList(list,myCandidateNumber,privateKey,prime);

    }
    public void sendBackDecryptedListToDB(){
        DataBase.pushPartialListDecryption(list);
    }




}

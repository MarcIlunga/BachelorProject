package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeProver;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ZeroKnowledgeVerifier;

import static junit.framework.Assert.assertTrue;

public class Participant extends Person implements User {


    private final BigInteger prime;
    private final BigInteger generator;
    private int k;
    public SecureDotProductParty secureDotProduct;
    public BigInteger[] wPrimeVector;
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
    private int myParticipantNumber;
    private Map<Integer, BigInteger[][]> othersGain;
    private List<List<BigInteger[]>> encryptedComparisonList;
    private int l;
    private EncryptedBinaryComparator comparisonTool;
    private List<List<BigInteger[]>> finalComp;
    private int ranking;
    private int pollNumber;
    private ElGamal elGamal;
    private List<List<BigInteger[][]>> comparisons;
    private boolean selected;
    private BigInteger[] wPrimeBinVector;


    public Participant(BigInteger replyGr[], BigInteger[] replyEq) {
        replyEqSize = replyEq.length;
        replyGrSize = replyGr.length;
        wPrimeVector = new BigInteger[replyGrSize + 2 * replyEqSize];
        generateWPrimeVector(replyGr, replyEq);
        for (int i = 0; i < replyGr.length; i++) {
            assertTrue(wPrimeVector[i].equals(replyGr[i]));
        }
        for (int i = 0; i < replyEq.length; i++) {
            assertTrue(wPrimeVector[i + replyGr.length].equals(replyEq[i].multiply(replyEq[i])));
            assertTrue(wPrimeVector[i + replyEq.length + replyGr.length].equals(replyEq[i]));
        }
        secureDotProduct = new SecureDotProductParty();
        secureDotProduct.setMyvector(wPrimeVector);
        group = Application.group;
        prime = Application.prime;
        generator = Application.generator;
        random = Application.random;
        elGamal = new ElGamal(prime, group, generator, random);
        comparisonTool = new EncryptedBinaryComparator(elGamal);
        prover = new ZeroKnowledgeProver(prime, group, generator);
        verifier = new ZeroKnowledgeVerifier(prime, group, generator);
        othersGain = new HashMap<>();
        encryptedComparisonList = new ArrayList<>();
        selected = false;

    }

    private void generateWPrimeVector(BigInteger replyGr[], BigInteger[] replyEq) {
        System.arraycopy(replyGr, 0, wPrimeVector, 0, replyGr.length);
        BigInteger[] v = SecureDotProductParty.vectorsElemMult(replyEq, replyEq);
        System.arraycopy(v, 0, wPrimeVector, replyEq.length, replyEq.length);
        System.arraycopy(replyEq, 0, wPrimeVector, replyEq.length + replyGr.length, replyEq.length);
    }

    public BigInteger[] getReplyVector() {
        return wPrimeVector;
    }


    public void convertGain(int l) {
        BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        gain = secureDotProduct.getBeta().add(two.pow(l));

    }

    public void generatesBinaryGain() {
        wPrimeBinVector = comparisonTool.createBinaryArrayOfLength(gain, l);
    }

    public void generatePrivateKey() {
        privateKey = (new BigInteger(group.bitLength(), random)).mod(group);
        publicKey = generator.modPow(privateKey, prime);
        elGamal.setPrivateKey(privateKey);
        prover.setKeyToProve(privateKey);
    }


    public void setKeyToVerify(BigInteger key) {
        verifier.setY(key);
    }


    public void encryptWithCommonkey() {
        cypher = elGamal.encryptMany(wPrimeBinVector);
    }


    public void setMyParticipantNumber(int number) {
        myParticipantNumber = number;
    }

    public List<List<BigInteger[]>> sendEncryptedComparisonToDB() {
        return encryptedComparisonList;

    }


    public void compareWithParticipants() {

        for (Integer index: othersGain.keySet()) {
            List<BigInteger[]> comp = comparisonTool.compareNumbers(wPrimeBinVector,cypher,othersGain.get(index));
            encryptedComparisonList.add(comp);
        }
        /*
        //List of encrypted bit by bit comparisons
        encryptedComparisonList = new ArrayList<>();
        //Procedure for Participant I
        for (Integer index : othersGain.keySet()) {

            //Computation of the gama's factors
            List<BigInteger[]> gamas = new ArrayList<>(l);
            //table of beta's
            BigInteger[][] betaI = othersGain.get(index);
            BigInteger[] tmp;
            BigInteger[] tmp2;
            for (int t = 0; t < l; t++) {
                tmp = homomorphicEncryption(betaI[t], cypher[t]);
                tmp2 = multHomomorphicEncryption(betaI[t], wPrimeVector[t].multiply(BigInteger.valueOf(-2)));
                gamas.add(t, homomorphicEncryption(tmp, tmp2));
            }
            /*
             * Optimisation begin with t = l so singe loop
             * Pull the creation outside the loops
             */
        /*
            BigInteger[] val;//(l -t +1 )
            BigInteger[][] sum = new BigInteger[l][2];
            BigInteger[][] negGamaT = new BigInteger[l][2];
            BigInteger[][] omegas = new BigInteger[l][2];
            BigInteger[][] taus = new BigInteger[l][2];
            for (int t = 0; t < l; t++) {
                val = elGamal.encrypt(BigInteger.valueOf(l - t));
                tmp = elGamal.getNegativeciphers(gamas.get(t));
                negGamaT[t] = multHomomorphicEncryption(tmp, BigInteger.valueOf(l - t));
                sum[t] = homomorphicEncryption(gamas.subList(t + 1, l));
                omegas[t] = homomorphicEncryption(val, sum[t], negGamaT[t]);// E( l-t+1 + sum of (gamav - gmai))
                taus[t] = homomorphicEncryption(omegas[t], betaI[t]);
            }
            encryptedComparisonList.add(taus);

        }
    */
    }


    public void receiveFinalComp(List<List<BigInteger[]>> comp) {
        this.finalComp = comp;

    }

    public void setK(int k) {
        this.k = k;
    }

    public void setL(int l) {
        this.l = l;
    }

    public void setPollNumber(int pollNumber) {
        this.pollNumber = pollNumber;
    }


    public int getParticipantNumber() {
        return myParticipantNumber;
    }

    public void initiateGainComputation() {
        secureDotProduct.initiateDotProduct();
    }

    public BigInteger getPubKey() {
        return publicKey;
    }

    public void setCommonKey(BigInteger commonKey) {
        elGamal.setCommonKey(commonKey);
    }


    public BigInteger[] getBinaryGain() {
        return wPrimeBinVector;
    }

    public void receiveOtherGain(int participantNumber, BigInteger[][] gain) {
        othersGain.put(participantNumber, gain);
    }


    public void receiveDataForChained(List<List<BigInteger[][]>> comparisons) {
        this.comparisons = comparisons;
    }

    public void partilaDecryption(Map<Integer,List<List<BigInteger[]>>> comparisons) {

        for (Integer index: comparisons.keySet()) {
            if(index.intValue() != myParticipantNumber){
               List<List<BigInteger[]>> comparisonsOfIndex = comparisons.get(index);
                for (List<BigInteger[]> comp: comparisonsOfIndex) {
                    for (int i = 0; i < comp.size(); i++) {
                        comp.set(i,elGamal.chainedDecription(comp.get(i)));
                    }
                }
            }

        }

    }


    public List<List<BigInteger[][]>> getPartialDecryption() {
        return comparisons;
    }

    public void computeRanking() {
        ranking = comparisonTool.finalDecryption(finalComp);
        if (ranking <= k) {
            selected = true;
        } else {
            selected = false;
        }
    }

    public boolean choosen() {
        return selected;
    }

    public Integer getRankings() {
        return ranking;
    }

    public BigInteger getPrivateKey() {
        return privateKey;
    }

    //TODO: REMOVE
    public BigInteger[][] getCryptedBinaryGain() {
        return cypher;
    }
}
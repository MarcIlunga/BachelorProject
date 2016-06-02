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
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.homomorphicEncryption;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.multHomomorphicEncryption;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.createBinaryArray;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.finalDecryption;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.copyBigIntArrayToIntArray;

public class Participant extends Person implements User {


    private final BigInteger prime;
    private final BigInteger generator;
    private final int k;
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
    private int myCandidateNumber;
    private static Map<Integer, BigInteger[][]> othersGain;
    private static List<BigInteger[][]> encryptedComparisonList;
    private List<List<BigInteger[][]>> list;
    private int l;
    private EncryptedBinaryComparator comparisonTool = EncryptedBinaryComparator.getGainComparisonTool();
    private BigInteger commonKey;
    private List<BigInteger[][]> finalComp;
    private int ranking;


    public Participant(BigInteger replyGr[], BigInteger[] replyEq,int l, int k) {
        replyEqSize = replyEq.length;
        replyGrSize = replyGr.length;
        wPrimeVector = new BigInteger[replyGrSize + 2 * replyEqSize];
        generateWPrimeVector(replyGr, replyEq);
        secureDotProduct = new SecureDotProductParty();
        secureDotProduct.setMyvector(wPrimeVector);
        group = Application.group;
        prime = Application.prime;
        generator = Application.generator;
        random = new SecureRandom();

        prover = new ZeroKnowledgeProver(prime, group, generator);
        verifier = new ZeroKnowledgeVerifier(prime, group, generator);
        othersGain = new HashMap<>();
        encryptedComparisonList = new ArrayList<>();
        this.l = l;
        this.k = k;
    }

    private void generateWPrimeVector(BigInteger replyGr[], BigInteger[] replyEq) {
        copyBigIntArrayToIntArray(replyGr, 0, wPrimeVector, 0, replyGrSize);

        BigInteger[] v = new BigInteger[replyEqSize];

        for (int i = 0; i < replyEqSize; i++) {
            v[i] = replyEq[i].pow(2);
        }
        copyBigIntArrayToIntArray(v, 0, wPrimeVector, replyGrSize, replyEqSize);
        copyBigIntArrayToIntArray(replyEq, 0, wPrimeVector, replyEqSize + replyEqSize, replyEqSize);
    }

    public BigInteger[] getReplyVector() {
        return wPrimeVector;
    }


    public void convertGain(int l) {
        BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
        gain = secureDotProduct.getBeta().add(two.pow(l));
        wPrimeVector = createBinaryArray(gain,l);
    }

    public void generatePrivateKey() {
        privateKey = (new BigInteger(prime.bitLength(), random)).mod(prime);
        publicKey = generator.modPow(privateKey, prime);
        DataBase.getDataBase().publishElGamalPublicKey(this, publicKey);
        prover.setX(privateKey);
    }


    public void setKeyToVerify(BigInteger key) {
        verifier.setY(key);
    }


    public void encryptWithCommonkey() {
        ElGamal.setCommonKey(DataBase.getEncryptionKey());
        cypher = ElGamal.encryptMany(wPrimeVector);

    }


    public void setMyCandidateNumber(int number) {
        myCandidateNumber = number;
    }

    public void sendEncryptedComparisonToDB() {
        DataBase.pushComparisonVector(this, comparisonTool.getEncryptedComparisons());
    }

    public void getEpsilonVector(List<List<BigInteger[][]>> list) {
        this.list = comparisonTool.chainedDecryption(list, myCandidateNumber, privateKey, prime,group);

    }

    public void sendBackDecryptedListToDB() {
        DataBase.pushPartialListDecryption(list);
    }


    public void participantGain(Integer participantIndex, BigInteger[][] gain) {
        othersGain.put(participantIndex, gain);
    }

    public void compareWithParticipants() {
        //List of encrypted bit by bit comparisons
        encryptedComparisonList = new ArrayList<>();
        //Procedure for candidate I
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
            BigInteger[] val;//(l -t +1 )
            BigInteger[][] sum = new BigInteger[l][2];
            BigInteger[][] negGamaT = new BigInteger[l][2];
            BigInteger[][] omegas = new BigInteger[l][2];
            BigInteger[][] taus = new BigInteger[l][2];
            for (int t = 0; t < l; t++) {
                val = ElGamal.encrypt(BigInteger.valueOf(l - t));
                tmp = ElGamal.getNegativeciphers(gamas.get(t));
                negGamaT[t] = multHomomorphicEncryption(tmp, BigInteger.valueOf(l - t));
                sum[t] = homomorphicEncryption(gamas.subList(t + 1, l));
                omegas[t] = homomorphicEncryption(val, sum[t], negGamaT[t]);// E( l-t+1 + sum of (gamav - gmai))
                taus[t] = homomorphicEncryption(omegas[t], betaI[t]);
            }
            encryptedComparisonList.add(taus);
        }
        DataBase.pushComparisonVector(this,encryptedComparisonList);
    }




    public void receiveFinalComp(List<BigInteger[][]> comp) {
        this.finalComp = comp;

    }

    private void computeSelfRanking(){
        ranking = finalDecryption(finalComp);
    }

    private void rankingSubmission(){
        if (ranking <= k){
            DataBase.submitsResults(this,ranking, wPrimeVector);
        }
    }

}
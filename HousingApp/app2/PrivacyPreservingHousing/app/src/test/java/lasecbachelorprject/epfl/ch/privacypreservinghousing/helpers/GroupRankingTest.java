package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Initiator;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Marc Ilunga on 09.06.2016.
 */
public class GroupRankingTest {
    private BigInteger prime;// = Application.prime;
    private BigInteger group;// = Application.group;
    private BigInteger generator;// = Application.generator;
    private Initiator initiator;
    private int eqNb = 5;
    private int grNb = 5;
    private BigInteger[] eqCriterion;
    private BigInteger[] greatCrit;
    private BigInteger[] eqWeight;
    private BigInteger[] grWeight;
    private DataBase dataBase = DataBase.getDataBase();
    private int pollNb;

    private int nbParticipants = 25;
    private int k;
    private List<Participant> participants;
    private SecureRandom random;
    private List<BigInteger> gains;
    private List<Integer> ranking;
    private List<BigInteger> sortedRankings;

    public GroupRankingTest() {
        prime = Application.prime;
        group = Application.group;
        generator = Application.generator;
        random = new SecureRandom();
        eqCriterion = new BigInteger[eqNb];
        initializeVector(eqCriterion);
        greatCrit = new BigInteger[grNb];
        initializeVector(greatCrit);
        eqWeight = new BigInteger[eqNb];
        initializeVector(eqWeight);
        grWeight = new BigInteger[grNb];
        initializeVector(grWeight);
        dataBase.securitySetUp(prime,group,generator);

    }

    private void initializeVectorGrCrit(BigInteger[] vect) {
        for (int i = 0; i <vect.length ; i++) {
            vect[i] = BigInteger.ZERO;
        }
    }

    private void initializeVectorEqCrit(BigInteger[] vect) {
        for (int i = 0; i <vect.length ; i++) {
            vect[i] = BigInteger.ONE;
        }
    }

    private void addParticipantsToPoll(List<Participant> participants) {
        for (int i = 0; i < nbParticipants ; i++) {
            Participant p = participants.get(i);
            int participantNumber = dataBase.addParticipant(p, pollNb);
            p.setMyParticipantNumber(participantNumber);
            p.setPollNumber(this.pollNb);
            p.setK(dataBase.getNbGoodCandidates(this.pollNb));
            p.setL(dataBase.getBitLength(this.pollNb));
        }
    }

    @Test
    public void groupRanking() throws IllegalAccessException {
        //Phase 1 in the constructor and various initializations
        initiator = new Initiator(eqCriterion,greatCrit,eqWeight,grWeight);
        k = 1;
        List<Participant> participants = initializeParticipants();
        pollNb = dataBase.initiateProtocol(initiator,nbParticipants,k,initiator.l);

        for (Participant p: participants) {
            p.setMyParticipantNumber(dataBase.addParticipant(p,pollNb));
            p.setL(dataBase.getBitLength(pollNb));
            p.setK(k);
        }
        //phase 2 to 4: SECURE GAIN COMPUTATION
        for (int i = 0; i <nbParticipants ; i++) {
            Participant p = participants.get(i);
            dataBase.InitiateDotproduct(p,pollNb);
            dataBase.respondToParticipant(initiator,initiator.getCurrParticipant(),pollNb);
            BigInteger expectedGain = SecureDotProductParty.normalDotProduct(p.wPrimeVector,initiator.getMyAttVector());
            BigInteger gain = p.secureDotProduct.getBeta();
            assertTrue(expectedGain.equals(gain));
            p.convertGain(dataBase.getBitLength(pollNb));
        }

        //phase 5: UNLIKEABLE GAIN COMPARISON
        for (int i = 0; i < nbParticipants; i++) {
            Participant p = participants.get(i);
            p.generatePrivateKey();
            dataBase.publishElGamalPublicKey(p,pollNb);
            assertTrue(dataBase.proveKeyToOthers(p,pollNb));
        }

        BigInteger y = dataBase.getCommonKey(pollNb).mod(prime);





        //Encryption and comparison phase
        for (int i = 0; i < nbParticipants; i++) {
            Participant p = participants.get(i);
            p.generatesBinaryGain();
            p.setCommonKey(y);
            p.encryptWithCommonkey();
            dataBase.publishEncryptedGain(p,pollNb);
        }
        //TODO: TEST HERE THAT THE ENCRYPTIONS ARE CORRECTS

        for (int i = 0; i < nbParticipants ; i++) {
            Participant p = participants.get(i);
            p.compareWithParticipants();
            dataBase.pushComparisonVector(p,pollNb);
        }

        for (int i = 0; i < nbParticipants ; i++) {
            Participant p = participants.get(i);
            dataBase.sendDataForChainedDecryption(p,pollNb);
        }
        for (int i = 0; i < nbParticipants; i++) {
            Participant p = participants.get(i);
            dataBase.sendLastCompartison(p,pollNb);
            p.computeRanking();
            dataBase.submitsResults(p,pollNb);
        }





    }

    private List<Participant> initializeParticipants() {
        List<Participant> participants = new ArrayList<>();
        BigInteger[] eqV = new BigInteger[eqNb];
        BigInteger[] grV = new BigInteger[grNb];
        for (int i = 0; i < nbParticipants ; i++) {
            initializeVector(eqV);
            initializeVector(grV);
            Participant p = new Participant(grV, eqV);
            participants.add(p);
        }
        return participants;
    }

    private void initializeWeight(BigInteger[] w) {
        for (int i = 0; i <w.length ; i++) {
            w[i] = new BigInteger(random.nextInt(5)+1,random);
        }
    }

    private void initializeVector(BigInteger[] vect) {
        for (int i = 0; i <vect.length ; i++) {
            vect[i] = new BigInteger(random.nextInt(11)+1,random).mod(group);
        }
    }

    private void testResults(){
        gains = new ArrayList<>(nbParticipants);
        ranking = new ArrayList<>(nbParticipants);
        for (int i = 0; i <nbParticipants ; i++) {
            gains.add(SecureDotProductParty.normalDotProduct(initiator.getMyAttVector(),participants.get(i).getReplyVector()));
        }
        sortedRankings = new ArrayList<>(gains);
        Collections.sort(sortedRankings,Collections.<BigInteger>reverseOrder());

        for (int i = 0; i < nbParticipants; i++) {
            ranking.add(gains.indexOf(sortedRankings.get(i)));
        }
    }



    @Test
    public void testWitihoutCrypto(){
        testResults();
    }


}

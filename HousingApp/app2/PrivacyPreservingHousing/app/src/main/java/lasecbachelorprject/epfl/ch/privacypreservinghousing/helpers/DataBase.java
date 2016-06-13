package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import android.renderscript.RSInvalidStateException;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Initiator;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

public class DataBase {

    public Initiator initiator;
    public static Poll poll;
    private static List<Poll> polls;
    private static List<Participant> participants;
    private static List<List<BigInteger[][]>> V; //Vector off all comparisons
    public static DataBase dataBase;
    private static Map<Participant,BigInteger[]> winnersVector;
    private static Map<Participant, Integer> whinersRanking;
    private static BigInteger commonKey = null;
    private int length;
    private int nbGoodCandidates;
    private BigInteger prime, group, generator;

    private  DataBase(){
        polls = new ArrayList<>();
        participants = new ArrayList<>();
        whinersRanking = new HashMap<>();
        winnersVector = new HashMap<>();
    }

    public static DataBase getDataBase(){
        if(dataBase == null){
            dataBase = new DataBase();
        }
        return dataBase;
    }

    public void securitySetUp(BigInteger prime, BigInteger group, BigInteger generator){
        this.prime = prime;
        this.group = group;
        this.generator = generator;
    }
    public int initiateProtocol(Initiator initiator,int nbParticipants, int nbSeekedCandidates, int bitLength){
        Poll newPoll = new Poll(nbParticipants);
        newPoll.setInitiator(initiator);
        newPoll.setBitLength(bitLength);
        newPoll.setSeekedCandidates(nbSeekedCandidates);
        polls.add(newPoll);
        return polls.indexOf(newPoll);
    }


    public int addParticipant(Participant p,int pollNb){
        return polls.get(pollNb).addParticipants(p);
    }


    public int getBitLength(int pollNb){
        return polls.get(pollNb).getBitLength();
    }

    public int getNbGoodCandidates(int pollNb){
        return polls.get(pollNb).getNbGoodCandidates();
    }

    public List<Participant> getParticipants(){
        return new ArrayList(participants);
    }

    public Poll getPoll(int pollNb){
        return polls.get(pollNb);
    }

    public Initiator getInitiator(){
        return initiator;
    }

    public void computeGainSecurely(Participant participant, Initiator initiator) {
        participant.secureDotProduct.initiateDotProduct();
        participant.sendInitialDataToOwner(initiator);
        initiator.secureDotProductParty.sendAH(participant.secureDotProduct);
        participant.convertGain(initiator.l);
    }

    public void publishElGamalPublicKey(Participant p, int pollNb){
        Poll poll = polls.get(pollNb);
        BigInteger key = p.getPubKey();
        poll.publicshKey(p,key);
    }

    //TODO
    public static boolean proveKeyToOthers(Participant participant, int pollNb) throws IllegalAccessException {
        boolean proof = true;
        BigInteger c = BigInteger.ZERO;
        Poll poll = polls.get(pollNb);
        BigInteger key = poll.getKeyOfParticipant(participant);
        List<Participant> participants = poll.getparticipants();
        if(key == null){
            throw  new IllegalAccessException("The public key of: "+participant.toString() +" is not in the DataBase" );
        }
        participant.prover.initiateProof();
        for (Participant p: participants) {
            if(!participant.equals(p)){
                p.setKeyToVerify(key);
                participant.prover.sendH(p.verifier);
                c = c.add(p.verifier.sendC());
            }
        }
        BigInteger z = participant.prover.sendZ(c);
        for(Participant p: participants){
            if(!participant.equals(p)){
                proof = proof && p.verifier.verifyWithZ(z,c);
            }
        }
        return proof;
    }

    public static BigInteger getEncryptionKey(){
        if(commonKey == null) {
            commonKey = BigInteger.ONE;
            for (BigInteger b : poll.getKeySet()) {
                commonKey = (commonKey.multiply(b)).mod(Application.prime);
            }
        }
        return commonKey;
    }

    public static void pushComparisonVector(Participant p, int idx){
        Poll poll = polls.get(idx);
        poll.addComparison(p.sendEncryptedComparisonToDB(),p.getParticipantNumber());

    }


    public static void pushPartialListDecryption(List<List<BigInteger[][]>> list) {
        V = list;
    }


    /**
     *
     */


    public void setNbGoodCandidates(int nbGoodCandidates) {
        this.nbGoodCandidates = nbGoodCandidates;
    }

    public static void submitsResults(Participant participant, int pollNb){
        if(participant.choosen()){
            Poll p = polls.get(pollNb);
            p.submitRanking(participant);
            p.sumbitAnswers(participant);
        }

    }


    public void InitiateDotproduct(Participant p, int pollNb) {
        p.initiateGainComputation();
        p.sendInitialDataToOwner(polls.get(pollNb).getInitiator());
        polls.get(pollNb).getInitiator().setCurrParticipant(p.getParticipantNumber());
    }

    public void respondToParticipant(Initiator initiator, int currParticipant,int pollNb) {
        Poll p = polls.get(pollNb);
        initiator.secureDotProductParty.sendAH(p.getParticipant(currParticipant).secureDotProduct);
    }

    public BigInteger getCommonKey(int pollNb) {
        return polls.get(pollNb).getPubKey().mod(prime);
    }

    public void setPrime(BigInteger prime) {
        this.prime = prime;
    }

    public void setGroup(BigInteger group) {
        this.group = group;
    }

    public void setGenerator(BigInteger generator) {
        this.generator = generator;
    }

    public void publishEncryptedGain(Participant p, int pollNb) {
        Poll poll = polls.get(pollNb);
        poll.sendEncryptedGainToOthers(p);
    }

    public void sendDataForChainedDecryption(Participant p, int pollNb) {
        Poll poll = polls.get(pollNb);
        p.partilaDecryption(poll.getComparisons());

    }

    public void sendLastCompartison(Participant p, int pollNb) {
        Poll poll = polls.get(pollNb);
        poll.sendResultToParticipants(p);
    }
}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Initiator;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;


public class Poll {
    public Initiator initiator;
    private List<Participant> participantsList;
    public int participantsNumber;
    private int participantCount;
    private Map<Participant, BigInteger> participantPublicKey;
    private Map <Integer,List<List<BigInteger[]>>> comparisions;
    private int bitLength;
    private int seekedCandidates;
    private int nbGoodCandidates;
    private BigInteger pubKey;
    private Map<Integer, Integer> rankings;
    private Map<Integer, BigInteger[]> answers;


    public Poll(int participantsNumber){
        this.participantsNumber = participantsNumber;
    }

    public void setInitiator(Initiator initiator) {
        this.initiator = initiator;
        participantsList = new ArrayList<>(participantsNumber);
        comparisions = new HashMap<>();
        participantPublicKey = new HashMap<>();
        pubKey = BigInteger.ONE;
        rankings = new HashMap<>();
        answers = new HashMap<>();
    }


    public void publicshKey(Participant participant, BigInteger key){
        participantPublicKey.put(participant, key);
        pubKey = pubKey.multiply(key);
    }


    public BigInteger getKeyOfParticipant(Participant participant) {
        return participantPublicKey.get(participant);
    }

    public int addParticipants(Participant p){
        participantsList.add(participantCount++,p);
        return participantCount -1 ;
    }

    public Collection<BigInteger> getKeySet() {
        return participantPublicKey.values();
    }

    public void setBitLength(int bitLength) {
        this.bitLength = bitLength;
    }

    public void setSeekedCandidates(int seekedCandidates) {
        this.seekedCandidates = seekedCandidates;
    }

    public int getBitLength() {
        return bitLength;
    }

    public int getNbGoodCandidates() {
        return nbGoodCandidates;
    }

    public Initiator getInitiator() {
        return initiator;
    }

    public Participant getParticipant(int index) {
        return participantsList.get(index);
    }

    public BigInteger getPubKey() {
        return pubKey;
    }

    public void sendEncryptedGainToOthers(Participant p) {
        BigInteger[][] gain = p.getCryptedBinaryGain();
        for (Participant part: participantsList) {
            if(p.getParticipantNumber()!= part.getParticipantNumber()){
                part.receiveOtherGain(p.getParticipantNumber(),gain);
            }
        }

    }

    public void addComparison(List<List<BigInteger[]>> comp, int participantNumber) {
        comparisions.put(participantNumber, comp);
    }


    //TODO: ENCAPSULATION
    public Map <Integer,List<List<BigInteger[]>>> getComparisons() {
        return comparisions;
    }

    public void setComp(Map<Integer,List<List<BigInteger[]>>> comp) {
        this.comparisions = comp;
    }

    public  void sendResultToParticipants(Participant p) {
        List<List<BigInteger[]>> compForParticipant = comparisions.get(p.getParticipantNumber());
        p.receiveFinalComp(compForParticipant);
    }

    public void submitRanking(Participant p) {
        rankings.put(p.getParticipantNumber(),p.getRankings());
    }

    public void sumbitAnswers(Participant participant) {
        answers.put(participant.getParticipantNumber(), participant.getReplyVector());
    }

    public List<Participant> getparticipants() {
        return new ArrayList<>(participantsList);
    }
}

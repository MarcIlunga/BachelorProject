package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

public class DataBase {

    public Owner owner;
    public static Poll poll;
    private static List<Participant> participants;
    private static List<List<BigInteger[][]>> V; //Vector off all comparisons
    public static DataBase dataBase;
    private int participantsNumber;

    private  DataBase(){
        participants = new ArrayList<>();
    }

    public static DataBase getDataBase(){
        if(dataBase == null){
            dataBase = new DataBase();
        }
        return dataBase;
    }

    public void setOwner (Owner owner){
        this.owner = owner;
    }
    public void setPoll(Poll poll){
        this.poll = poll;
    }
    public void addParticipant(Participant p){
        participants.add(participantsNumber,p);
        p.setMyCandidateNumber(participantsNumber);
        participantsNumber += 1;
    }

    public List<Participant> getParticipants(){
        return new ArrayList(participants);
    }

    public Poll getPoll(){
        return poll;
    }

    public Owner getOwner(){
        return owner;
    }

    public void computeGainSecurely(Participant participant, Owner owner) {
        participant.secureDotProduct.initiateDotProduct();
        participant.sendInitialDataToOwner(owner);
        owner.me.sendAH(participant.secureDotProduct);
        participant.convertGain(owner.l);
    }

    public void publishElGamalPublicKey(Participant p, BigInteger key){
        poll.publicshKey(p,key);
    }

    //TODO
    public static boolean proveKeyToOthers(Participant participant) throws IllegalAccessException {
        boolean proof = true;
        BigInteger c = BigInteger.ZERO;
        BigInteger key = poll.getKeyOfParticipant(participant);
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

    public BigInteger getEncryptionKey(){
        BigInteger key = BigInteger.ONE;
        for (BigInteger b: poll.getKeySet()) {
            key  = (key.multiply(b)).mod(Application.prime);
        }
        return key;
    }

    public static void pushComparisonVector(Participant p, List<BigInteger[][]> epsilon){
        V.add(participants.indexOf(p), epsilon);
    }


    public static void pushPartialListDecryption(List<List<BigInteger[][]>> list) {
        V = list;
    }
}

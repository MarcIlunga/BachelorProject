package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

public class DataBase {

    public Owner owner;
    public Poll poll;
    public List<Participant> participants;
    public static DataBase dataBase;

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
        participants.add(p);
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
    public boolean proveKeyToOthers(Participant participant) throws IllegalAccessException {
        boolean proof = true;
        for (Participant p: participants) {
            BigInteger key = poll.getKeyOfParticipant(participant);
            if(key == null){
                throw  new IllegalAccessException("The public key of: "+participant.toString() +" is not in the DataBase" );
            }
            if(!participant.equals(p)){
                participant.prover.initiateProof();
                p.setKeyToVerify(key);
                participant.prover.sendH(p.verifier);
                p.verifier.sendC(participant.prover);
                participant.prover.sendZ(p.verifier);
                proof = proof && p.verifier.verify();
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



}

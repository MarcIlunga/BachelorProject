package lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers;


import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Person;


public class Poll {
    public Owner owner;
    private List<Participant> participantsList;
    public int participantsNumber;
    private Map<Participant, BigInteger> participantPublicKey;

    public Poll(int participantsNumber){
        this.participantsNumber = participantsNumber;
    }
    public void setOwner(Owner owner) {
        this.owner = owner;
        participantsList = new ArrayList<>();
        participantPublicKey = new HashMap<>();

    }

    public void getParticipants(){

    }
/*
     public void setParticipants(List<Participant> list) {
         if(list != null){
             participantsList = new ArrayList<>(list);
             for (Participant u: list) {
                    SecureDotProduct dotProductInterface = new SecureDotProduct(u.secureDotProduct,owner.secureDotProduct);
                    BigInteger partialGain = new BigInteger(String.valueOf(dotProductInterface.dotProduct()));

                    u.put("Partial Gain", partialGain);

             }
         }

    }
    */

    public void startParticipants() {
        for (Person participant : participantsList) {

        }
    }

    public void publicshKey(Participant participant, BigInteger key){
        participantPublicKey.put(participant, key);
    }


    public BigInteger getKeyOfParticipant(Participant participant) {
        return participantPublicKey.get(participant);
    }

    public Collection<BigInteger> getKeySet() {
        return participantPublicKey.values();
    }
}

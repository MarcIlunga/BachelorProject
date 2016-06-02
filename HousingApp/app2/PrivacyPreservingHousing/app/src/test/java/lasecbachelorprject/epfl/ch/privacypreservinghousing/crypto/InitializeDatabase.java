package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.Poll;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

public class InitializeDatabase {

    public static Owner owner;
    public static Poll poll;
    public static DataBase database;
    public static List<Participant> participants;

    public static void mockDataBase(){
        BigInteger[] ownerEqCriterion = new BigInteger[5];
        BigInteger[] ownerGrCriterion = new BigInteger[5];
        BigInteger[] weightEq = new BigInteger[5];
        BigInteger[] weightGr = new BigInteger[5];
        for (int i = 0; i < 5; i++) {
            ownerEqCriterion[i] = BigInteger.ONE;
            ownerGrCriterion[i] = BigInteger.ONE;
            weightEq[i] = BigInteger.ONE;
            weightGr[i] = BigInteger.ONE;
        }

        owner = new Owner(ownerEqCriterion, ownerGrCriterion, weightEq, weightGr);
        owner.initiatePoll(6);
        poll = owner.myPoll;
        poll.setOwner(owner);

        database = DataBase.getDataBase();
        database.setOwner(owner);
        database.setPoll(poll);
        participants = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            BigInteger[] replyEq = new BigInteger[5];
            BigInteger[] replyGr = new BigInteger[5];
            for (int j = 0; j < 10; j++) {
                if (j < 5) {
                    replyEq[j] = new BigInteger(String.valueOf(i + 1));
                } else {
                    replyGr[j - 5] = BigInteger.ONE;
                }
            }

            //TODO Participant p = new Participant(replyGr, replyEq);
           // participants.add(i, p);
            //database.addParticipant(p);
        }

    }
}

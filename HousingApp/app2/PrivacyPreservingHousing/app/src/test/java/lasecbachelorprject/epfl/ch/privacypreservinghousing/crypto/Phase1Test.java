package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.Poll;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.InitializeDatabase.mockDataBase;
import static org.junit.Assert.assertEquals;

public class Phase1Test {

    public Owner owner;
    public DataBase database;
    public Poll poll;

    @Test
    public void TestPhase1EqualWorks() {
        mockDataBase();
        database = InitializeDatabase.database;
        owner = InitializeDatabase.owner;
        for (Participant p : InitializeDatabase.participants) {
            database.addParticipant(p);
            database.computeGainSecurely(p, owner);
            BigInteger expectedgain = SecureDotProductParty.normalDotProduct(p.getReplyVector(), owner.getMyAttVector()).add(owner.me.getRhoForParticipant());
            assertEquals(expectedgain, p.secureDotProduct.getBeta());
        }
        for (int i = 1; i < 9; i++) {
            assertTrue(InitializeDatabase.participants.get(0).secureDotProduct.getBeta().compareTo(InitializeDatabase.participants.get(i).secureDotProduct.getBeta()) == 1);
            assertTrue(InitializeDatabase.participants.get(i).secureDotProduct.getBeta().compareTo(InitializeDatabase.participants.get(i + 1).secureDotProduct.getBeta()) == 1);

    }

    /*
    @Test
    public void testGreaterWorks(){
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
        List<Participant> participants = new ArrayList<>(6);
        for (int i = 0; i < 6; i++) {
            BigInteger[] replyEq = new BigInteger[5];
            BigInteger[] replyGr = new BigInteger[5];
            for (int j = 0; j < 10; j++) {
                if (j < 5) {
                    replyEq[j] = new BigInteger(String.valueOf(i + 1));
                } else {
                    replyGr[j - 5] = BigInteger.ONE;
                }
            }

            Participant p = new Participant(replyGr, replyEq);
            participants.add(i, p);
        }

        for (Participant p : participants) {
            database.addParticipant(p);
            database.computeGainSecurely(p, owner);
            int expectedgain = SecureDotProductParty.normalDotProduct(p.getReplyVector(), owner.getMyAttVector()) - owner.me.getRhoForParticipant();
            assertEquals(expectedgain, p.secureDotProduct.getBeta());
        }
        for (int i = 1; i < 5; i++) {
            assertTrue(participants.get(0).secureDotProduct.getBeta() >= participants.get(i).secureDotProduct.getBeta());
            assertTrue(participants.get(i).secureDotProduct.getBeta() >= participants.get(i + 1).secureDotProduct.getBeta());
        }

    }

*/



 }
}




package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;


import org.junit.Test;

import java.math.BigInteger;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch.getStopWatch;
import static org.junit.Assert.assertTrue;

public class ZeroKnowledgeTest {

    BigInteger group = Application.group, generator = Application.generator, prime = Application.prime, x, y;
    StopWatch stopWatch = getStopWatch();
    public ZeroKnowledgeTest(){

    }

    @Test
    public void testZeroKnowledge(){
        InitializeDatabase.mockDataBase();
        DataBase dataBase = InitializeDatabase.database;
        List<Participant> participantList = dataBase.getParticipants() ;

        System.out.println("group "+group.toString());
        System.out.println("générator: "+ generator.toString());
        System.out.println("Prime generation time: "+ stopWatch.ellapsedTime() );
        for (BigInteger i = BigInteger.ZERO; i.compareTo(new BigInteger("6")) == -1 ; i = i.add(BigInteger.ONE)) {
            x = i;
            y = generator.modPow(x,prime);
            Participant p = participantList.get(i.intValue());
                p.prover.setX(x);
                dataBase.publishElGamalPublicKey(p,y);
                try {
                    assertTrue(DataBase.proveKeyToOthers(p));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

        }

    }

}
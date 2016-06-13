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


    }

}
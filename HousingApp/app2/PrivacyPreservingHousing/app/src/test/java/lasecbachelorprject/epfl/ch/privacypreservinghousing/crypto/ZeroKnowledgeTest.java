package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;


import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch.getStopWatch;

public class ZeroKnowledgeTest {

    PrimeGenerator primeGenerator;
    BigInteger group, generator, x, y;
    StopWatch stopWatch = getStopWatch();
    public ZeroKnowledgeTest(){
        stopWatch.start();
        stopWatch.stop();
        group = Application.prime;
        generator = Application.generator;
    }

    @Test
    public void testZeroKnowledge(){
        ZeroKnowledgeProver prover;
        ZeroKnowledgeVerifier verifier;
        System.out.println("group "+group.toString());
        System.out.println("générator: "+ generator.toString());
        System.out.println("Prime generation time: "+ stopWatch.ellapsedTime() );
        for (BigInteger i = BigInteger.ONE; i.compareTo(group) == -1 ; i = i.multiply(BigInteger.valueOf(100)).add(BigInteger.valueOf((new SecureRandom()).nextInt(10)))) {
            x = new BigInteger(String.valueOf(i));
            y = generator.modPow(x,group);
            prover = new ZeroKnowledgeProver(x,group,generator);
            verifier = new ZeroKnowledgeVerifier(y,group,generator);
            prover.initiateProof();
            prover.sendH(verifier);
            verifier.sendC(prover);
            prover.sendH(verifier);
            prover.sendZ(verifier);
            assertTrue(verifier.verify());
        }

    }

}
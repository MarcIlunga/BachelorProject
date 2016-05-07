package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch;

import static junit.framework.Assert.assertTrue;

public class PrimeGeneratorTest {

    @Test
    public void testGroupGenerator(){
        StopWatch stopWatch = StopWatch.getStopWatch();
        stopWatch.start();
        GroupGenerator groupGenerator = new GroupGenerator(3, 300,new SecureRandom(),false);
        stopWatch.stop();
        BigInteger prime = groupGenerator.getPrime();
        BigInteger group = groupGenerator.getGroup();
        BigInteger generator = groupGenerator.getGenerator();
        System.out.println("Ellapsed Time: " + stopWatch.ellapsedTime());
        assertTrue(true);
    }
}

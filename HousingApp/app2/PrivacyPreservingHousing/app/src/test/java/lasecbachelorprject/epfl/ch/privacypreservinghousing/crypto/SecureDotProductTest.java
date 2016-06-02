package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.StopWatch;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.normalDotProduct;
import static org.junit.Assert.assertEquals;

public class SecureDotProductTest {

    @Test
    public void testDotProduct() throws Exception {
        StopWatch timer = StopWatch.getStopWatch();
        PrintWriter writer = new PrintWriter("DotProdTime", "UTF-8");

        for (int i = 1; i < 25 ; i++) {

            BigInteger[] alicesVector = new BigInteger[i];
            BigInteger[] bobsVector = new BigInteger[i];
            SecureRandom rd = new SecureRandom();

            timer.start();
            for (int j = 0; j < i; j++) {
                alicesVector[j] = BigInteger.valueOf(rd.nextInt(10));
                bobsVector[j] = BigInteger.valueOf(rd.nextInt(10));
            }
            SecureDotProductParty alice = new SecureDotProductParty(BigInteger.TEN);
            alice.setMyvector(alicesVector);
            SecureDotProductParty bob = new SecureDotProductParty(BigInteger.TEN);
            bob.setMyvector(bobsVector);
            alice.initiateDotProduct();
            alice.sendInitialDataToOtherParty(bob);
            bob.sendAH(alice);
            alice.sendBeta(bob);
            bob.sendAlpha(alice);

            assertEquals(bob.getAlpha(), alice.getAlpha());
            assertEquals(bob.getBeta(), alice.getBeta());
            assertEquals(normalDotProduct(alicesVector, bobsVector), alice.getBeta().subtract(bob.getAlpha()));
            timer.stop();
            writer.println("Size of matrix :" + i + " time: " + timer.ellapsedTime() + " ms");
        }
        writer.close();
    }

    /*
    private BigInteger normalDotProd(BigInteger[] v1, BigInteger[] v2 ){
        BigInteger res = 0;
        for (int i = 0; i < v1.length ; i++) {
            res += v1[i]*v2[i];
        }
        return res;

    }
    */
}
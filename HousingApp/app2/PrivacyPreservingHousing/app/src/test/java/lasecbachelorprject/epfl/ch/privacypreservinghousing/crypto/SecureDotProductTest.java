package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.normalDotProduct;
import static org.junit.Assert.assertEquals;

public class SecureDotProductTest {

    @Test
    public void testDotProduct() throws Exception {
        BigInteger [] alicesVector = new BigInteger[10];
        BigInteger [] bobsVector = new BigInteger[10];
        SecureRandom rd = new SecureRandom();

            for (int j = 0; j < 10; j++) {
                alicesVector[j] =BigInteger.valueOf(rd.nextInt(10));
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

            assertEquals(bob.getAlpha(),alice.getAlpha());
            assertEquals(bob.getBeta(),alice.getBeta());
            assertEquals(normalDotProduct(alicesVector,bobsVector),alice.getBeta().subtract(bob.getAlpha()));


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
package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.createBinaryArray;


/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class EncryptedBinaryComparatorTest {

    ElGamal elGamal;
    EncryptedBinaryComparator toolA;
    private SecureRandom random;

    public EncryptedBinaryComparatorTest(){
        random = new SecureRandom();
        elGamal = new ElGamal(Application.prime,Application.group,Application.generator,random);
        toolA = new EncryptedBinaryComparator(elGamal);

    }

    @Test
    public void testGetGainComparisonTool() throws Exception {
        for (int j = 0; j < 10; j++) {
            elGamal.setPrivateKey(BigInteger.valueOf(random.nextInt()));
            elGamal.setCommonKey(elGamal.getPublicKey());
            BigInteger gainA = new BigInteger(50,random);
            BigInteger[][] cypherA = elGamal.encryptMany(toolA.createBinaryArrayOfLength(gainA,50));
            BigInteger gainB = new BigInteger(50, random);
            gainB = gainB.mod(gainA);
            BigInteger[][] cypherB = elGamal.encryptMany(toolA.createBinaryArrayOfLength(gainB,50));

            toolA.setMyGain(cypherB, toolA.createBinaryArrayOfLength(gainB,50));
            toolA.setOthersGain(0, cypherA);
            toolA.setOthersPlain(toolA.createBinaryArrayOfLength(gainA,50));

            toolA.compareWithParticipants();

            List<BigInteger[][]> comp = toolA.getEncryptedComparisons();
            List<BigInteger> res = elGamal.decryptMany(Arrays.asList(comp.get(0)));
            int expectedIndex = toolA.findDiffindex(gainA, gainB);
            for (int i = 0; i < res.size(); i++) {
                if (i == expectedIndex) {
                    assertTrue(res.get(expectedIndex).equals(BigInteger.ZERO));
                }
                else {
                    assertTrue(res.get(i).equals(BigInteger.ONE));
                }
            }
        }



        
    }
    @Test
    public void testArrayConv(){
        for (int i = 1; i < 10 ; i++) {
            BigInteger nb = new BigInteger(i, random);
            BigInteger[] ar = createBinaryArray(nb, 10);
            BigInteger res = EncryptedBinaryComparator.convertNumberFromArray(ar);
            assertTrue(nb.equals(res));
        }
    }

}
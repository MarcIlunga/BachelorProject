package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.findDiffindex;


/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class EncryptedBinaryComparatorTest {

    GroupGenerator groupGenerator;

    ElGamal elGamal;
    EncryptedBinaryComparator toolA;
    private SecureRandom random;

    public EncryptedBinaryComparatorTest(){
        elGamal = ElGamal.getElGamal(Application.prime,Application.group,Application.generator);
        toolA = EncryptedBinaryComparator.getGainComparisonTool();
        random = new SecureRandom();
    }

    @Test
    public void testGetGainComparisonTool() throws Exception {
        for (int j = 0; j < 10; j++) {
            elGamal.setPrivateKey(BigInteger.valueOf(random.nextInt()));
            elGamal.setCommonKey(elGamal.getmyPublicKey());
            BigInteger gainA = new BigInteger(50,random);
            BigInteger[][] cypherA = elGamal.encryptMany(toolA.createBinaryArray(gainA,50));
            BigInteger gainB = new BigInteger(50, random);
            gainB = gainB.mod(gainA);
            BigInteger[][] cypherB = elGamal.encryptMany(toolA.createBinaryArray(gainB,50));

            toolA.setMyGain(cypherB, toolA.createBinaryArray(gainB,50));
            toolA.setOthersGain(0, cypherA);
            toolA.setOthersPlain(toolA.createBinaryArray(gainA,50));

            toolA.compareWithParticipants();

            List<BigInteger[][]> comp = toolA.getEncryptedComparisons();
            List<BigInteger> res = elGamal.decryptMany(Arrays.asList(comp.get(0)));
            int expectedIndex = findDiffindex(gainA, gainB);
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
    public void testSetMyGain() throws Exception {

    }

    @Test
    public void testSetOthersGain() throws Exception {

    }

    @Test
    public void testCompareWithParticipants() throws Exception {

    }

    @Test
    public void testGetEncryptedCompWithcanditate() throws Exception {

    }

    @Test
    public void testGetEncryptedComparisons() throws Exception {

    }

    @Test
    public void testProcessList() throws Exception {

    }

}
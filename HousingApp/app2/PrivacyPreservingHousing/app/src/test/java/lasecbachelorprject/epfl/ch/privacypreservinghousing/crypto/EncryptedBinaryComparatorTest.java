package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;


/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class EncryptedBinaryComparatorTest {

    GroupGenerator groupGenerator;

    ElGamal elGamal;
    EncryptedBinaryComparator toolA;

    public EncryptedBinaryComparatorTest(){
        groupGenerator = new GroupGenerator(10,50, new SecureRandom(),false);
        elGamal = ElGamal.getElGamal(GroupGenerator.getPrime(),GroupGenerator.getGroup(),GroupGenerator.getGenerator());
        toolA = EncryptedBinaryComparator.getGainComparisonTool();
    }

    @Test
    public void testGetGainComparisonTool() throws Exception {
        elGamal.setPrivateKey(BigInteger.valueOf(8));
        BigInteger gainA = new BigInteger("255");
        BigInteger[][]cypherA = elGamal.encryptMany(toolA.createBinaryArray(gainA));
        BigInteger gainB = new BigInteger("149");
        BigInteger [][] cypherB = elGamal.encryptMany(toolA.createBinaryArray(gainB));

        toolA.setMyGain(cypherB,toolA.createBinaryArray(gainB));
        toolA.setOthersGain(1,cypherA);

        toolA.compareWithParticipants();

        List<BigInteger[][]> comp = toolA.getEncryptedComparisons();
        List<BigInteger[]> c = new ArrayList<>();

        for (BigInteger[][] cipher:comp) {
            c.add(elGamal.decryptMany(cipher));
        }

        c.add(elGamal.decryptMany(cypherA));
        c.add(elGamal.decryptMany(cypherB));
        
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
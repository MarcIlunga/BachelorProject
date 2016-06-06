package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.hamcrest.SelfDescribing;
import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.compareNumbers;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.createBinaryArray;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.findDiffindex;


/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class EncryptedBinaryComparatorTest {

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
    public void testChainedDecryption() throws Exception {
        List<Participant> part = new ArrayList<>(2);
        List<BigInteger> gains = new ArrayList<>(2);
        List<BigInteger> privKeys = new ArrayList<>(2);
        List<BigInteger> pubKeys = new ArrayList<>(2);
        List<BigInteger[]> binGains = new ArrayList<>(2);
        List<BigInteger[][]> encrypVect = new ArrayList<>(2);
        Map<Integer,List<List<BigInteger[]>>> encryptComps = new HashMap<>();
        for (int i = 0; i < 2 ; i++) {
            part.add(new Participant(null,null,50,1));
            BigInteger key = new BigInteger(String.valueOf(random.nextInt()));
            privKeys.add(i,key);
            elGamal.setMySKey(part.get(i),key);
            pubKeys.add(i,elGamal.getMyPuKey(part.get(i)));
            gains.add(i, new BigInteger(50,random).mod(gains.get(0)));
            binGains.add(createBinaryArray(gains.get(i),50));
        }

        elGamal.setCommonKey(pubKeys.get(0).multiply(pubKeys.get(1).multiply(pubKeys.get(2))));

        for (int i = 0; i <2 ; i++) {
            encrypVect.add(i,elGamal.encryptMany(binGains.get(i)));
        }

        for (int i = 0; i < 2 ; i++) {
            //encryptComps.add(i,new ArrayList<BigInteger[]>());
            for (int j = i +1; j != i; j = (j+1) % 2) {
                //encryptComps.add(i,compareNumbers(binGains.get(i),encrypVect.get(i),encrypVect.get(j)));
            }
        }





    }

}
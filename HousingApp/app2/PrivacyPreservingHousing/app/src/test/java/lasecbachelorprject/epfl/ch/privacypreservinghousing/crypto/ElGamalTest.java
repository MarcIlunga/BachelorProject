package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import android.bluetooth.BluetoothGatt;
import android.text.BidiFormatter;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.createBinaryArray;

/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class ElGamalTest {

    private final GroupGenerator groupGenerator;
    private final ElGamal elGamal;
    private SecureRandom random;
    private BigInteger privateKey;


    public ElGamalTest(){
        groupGenerator = new GroupGenerator(512,50, new SecureRandom(),false);
        elGamal = ElGamal.getElGamal(GroupGenerator.getPrime(),GroupGenerator.getGroup(),GroupGenerator.getGenerator());
        privateKey = new BigInteger(9, new SecureRandom());
        elGamal.setPrivateKey(privateKey);
        elGamal.setCommonKey(elGamal.getmyPublicKey());
        random = new SecureRandom();

    }



    @Test
    public void testGetElGamal() throws Exception {

    }

    @Test
    public void testEncrypt() throws Exception {
        for (int i = 0; i < 10; i++) {
            BigInteger c = BigInteger.valueOf(random.nextInt()).mod(groupGenerator.getPrime());

            BigInteger[] cb = createBinaryArray(c);
            BigInteger[][] cipher = elGamal.encryptMany(cb);

            List<BigInteger> res = elGamal.decryptMany(Arrays.asList(cipher));
            for (int j = 0; j < cb.length; j++) {
                assertTrue(res.get(i).equals(cb[i]));
            }
        }
    }

    @Test
    public void testSetPrivateKey() throws Exception {

    }

    @Test
    public void testDecrypt() throws Exception {

    }

    @Test
    public void testDecrypt1() throws Exception {

    }

    @Test
    public void testGetPublicKey() throws Exception {

    }

    @Test
    public void testGetEncrypter() throws Exception {

    }

    @Test
    public void testHomomorphicEncryption() throws Exception {
        BigInteger[] resu;
        for (int i = 0; i < 100 ; i++) {
            BigInteger v1 = BigInteger.valueOf(random.nextInt(2));
            BigInteger v2 = BigInteger.valueOf(random.nextInt(2));
            BigInteger[] c1 = elGamal.encrypt(v1);
            BigInteger[] c2 = elGamal.encrypt(v2);
            resu = elGamal.homomorphicEncryption(c1, c2);
            BigInteger res = elGamal.decrypt(resu);
            assertTrue(v1.equals(elGamal.decrypt(c1)));
            assertTrue(v2.equals(elGamal.decrypt(c2)));
            if((v1.equals(BigInteger.ZERO)  ^ v2.equals(BigInteger.ZERO)) || (v1.equals(BigInteger.ZERO) && v2.equals(BigInteger.ZERO))){
                assertTrue(res.equals(v1.add(v2)));
            }
            else{
                assertTrue(res.equals(BigInteger.ONE));
            }
        }
    }

    @Test
    public void testGetNegativeciphers() throws Exception {

    }

    @Test
    public void testHomomorphicEncryption1() throws Exception {

    }

    @Test
    public void testHomomorphicEncryption2() throws Exception {

    }

    @Test
    public void testMultHomomorphicEncryption() throws Exception {

    }
}
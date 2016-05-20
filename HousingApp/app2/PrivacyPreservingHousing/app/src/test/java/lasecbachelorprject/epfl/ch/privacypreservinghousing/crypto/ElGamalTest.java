package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import android.bluetooth.BluetoothGatt;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static junit.framework.Assert.assertTrue;

/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class ElGamalTest {

    private final GroupGenerator groupGenerator;
    private final ElGamal elGamal;
    private BigInteger privateKey;


    public ElGamalTest(){
        groupGenerator = new GroupGenerator(10,50, new SecureRandom(),false);
        elGamal = ElGamal.getElGamal(GroupGenerator.getPrime(),GroupGenerator.getGroup(),GroupGenerator.getGenerator());
        privateKey = new BigInteger(9, new SecureRandom());
        elGamal.setPrivateKey(privateKey);
    }



    @Test
    public void testGetElGamal() throws Exception {

    }

    @Test
    public void testEncrypt() throws Exception {
        BigInteger k = new BigInteger(String.valueOf(1));

        BigInteger[] cipher = elGamal.encrypt(k);
        BigInteger res = elGamal.decrypt(cipher);
        assertTrue(!k.equals(res));
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
        BigInteger[] c1 = elGamal.encrypt(BigInteger.ONE);
        BigInteger[] c2 = elGamal.encrypt(BigInteger.ZERO);
        c1 = elGamal.homomorphicEncryption(c1,c2);
        BigInteger res = elGamal.decrypt(c1);
        BigInteger g = c1[0].multiply(c1[1].modPow(privateKey.negate(),groupGenerator.getPrime())).mod(groupGenerator.getPrime());
        assertTrue(BigInteger.ONE.equals(elGamal.decrypt(c1)));
        assertTrue(BigInteger.ZERO.equals(elGamal.decrypt(c2)));
        assertTrue(res.equals(BigInteger.ONE) && g.equals(groupGenerator.getGenerator()));
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
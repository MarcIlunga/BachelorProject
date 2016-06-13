package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import org.junit.Test;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static junit.framework.Assert.assertTrue;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.EncryptedBinaryComparator.createBinaryArray;

/**
 * Created by Marc Ilunga on 10.05.2016.
 */
public class ElGamalTest {

    private  BigInteger generator = Application.generator;
    private BigInteger group = Application.group;
    private ElGamal elGamal;
    private BigInteger prime = Application.group;
    private SecureRandom random;
    private BigInteger privateKey;


    public ElGamalTest(){
        random = new SecureRandom();
        elGamal = new ElGamal(prime,group,generator,random);
        privateKey = new BigInteger(513, new SecureRandom());
    }



    @Test
    public void testChainedEncryptionDecryption() throws Exception {

        for (int i = 2; i < 6  ; i++) {
            List<BigInteger> privKeys = new ArrayList<>(i);
            List<ElGamal> elGamals = new ArrayList<>(i);
            BigInteger y = BigInteger.ONE;
            BigInteger expY = BigInteger.ONE;
            BigInteger yj;
            for (int j = 0; j < i; j++) {
                ElGamal elGamal = new ElGamal(prime,group,generator,random);
                BigInteger priK = new BigInteger(group.bitLength() - 1,random);
                elGamal.setPrivateKey(priK);
                elGamals.add(elGamal);
                privKeys.add(priK);
                yj = generator.modPow(priK,prime);
                assertTrue(yj.equals(elGamals.get(j).getPublicKey()));
                y = y.multiply(yj).mod(prime);

            }


            for (ElGamal e:elGamals) {
                e.setCommonKey(y);
            }

            for (int j = 0; j < 10; j++) {
                BigInteger message = new BigInteger(String.valueOf(random.nextInt(2)));
                BigInteger[] cipher = elGamals.get(j%i).encrypt(message);
                BigInteger res[] = new BigInteger[2];
                for (int k = 0; k < i-1 ; k++) {
                   cipher = elGamals.get(k).chainedDecription(cipher);
                }
                BigInteger resu = elGamals.get(i-1).decrypt(cipher);
                assertTrue(resu.equals(message));
            }
        }
    }

    @Test
    public void testEncrypt() throws Exception {
        elGamal.setCommonKey(privateKey);
        elGamal.setCommonKey(elGamal.getPublicKey());
        for (int i = 0; i < 10; i++) {
            BigInteger c = BigInteger.valueOf(random.nextInt(group.bitLength())).mod(group);

            BigInteger[] cb = createBinaryArray(c);
            BigInteger[][] cipher = elGamal.encryptMany(cb);

            List<BigInteger> res = elGamal.decryptMany(Arrays.asList(cipher));
            for (int j = 0; j < cb.length; j++) {
                assertTrue(res.get(j).equals(cb[j]));
            }
        }
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


}
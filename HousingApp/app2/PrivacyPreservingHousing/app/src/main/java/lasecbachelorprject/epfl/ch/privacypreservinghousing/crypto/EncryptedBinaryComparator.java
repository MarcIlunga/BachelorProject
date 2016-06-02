package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Participant;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.decrypt;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.decryptMany;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.homomorphicEncryption;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.ElGamal.multHomomorphicEncryption;


public class EncryptedBinaryComparator {

    private static BigInteger[][] myGain;
    private static Map<Integer, BigInteger[][]> othersGain;
    private static List<BigInteger[][]> encryptedComparisonList;
    private static BigInteger[] plainGain;
    private static BigInteger[] othersPlain;
    private static int l;
    private static EncryptedBinaryComparator gainComparisonTool;
    private static Map<Participant,BigInteger[][]> selfGain;
    private static Map<Participant,Map<Integer, BigInteger[][]>> selfOthersGain;
    private static Map<Participant,List<BigInteger[][]>> selfCryptComp;
    private static Map<Participant,BigInteger[]> selfPlainGain;
    public  static List<BigInteger[]> gamas;
    public static BigInteger[][] betaI;
    public  static BigInteger[] tmp;
    public static BigInteger[] tmp2;//(l -t +1 )
    public static BigInteger[] val;
    public static BigInteger[][] sum;
    public static BigInteger[][] negGamaT;
    public static BigInteger[][] omegas;
    public static BigInteger[][] taus;

    private EncryptedBinaryComparator(){
        selfCryptComp = new HashMap<>();
        selfGain = new HashMap<>();
        selfPlainGain = new HashMap<>();
        selfOthersGain = new HashMap<>();
        othersGain = new HashMap<>();
    }

    public static EncryptedBinaryComparator getGainComparisonTool(){
        if(gainComparisonTool == null){
            gainComparisonTool = new EncryptedBinaryComparator();
        }
        return gainComparisonTool;
    }

    public static void setMyGain(BigInteger [][] encryptedGain, BigInteger[]plainGain){
        if(encryptedGain == null)
            throw new NullPointerException("Null encryptedGain");
        l = encryptedGain.length;
        myGain = encryptedGain.clone();
        EncryptedBinaryComparator.plainGain = plainGain.clone();
    }
    public  static void setL(int l){
        EncryptedBinaryComparator.l = l;
    }
    public static void setMyGainMe(Participant p, BigInteger [][] encryptedGain, BigInteger[]plainGain){
        if(encryptedGain == null)
            throw new NullPointerException("Null encryptedGain");
        selfGain.put(p,encryptedGain);
        selfPlainGain.put(p,plainGain);


    }



    public static void setOthersGain(Integer participantIndex, BigInteger[][] gain){
        othersGain.put(participantIndex,gain);
    }

    public static void setOthersGainMe(Participant p,Integer participantIndex, BigInteger[][] gain){
        if(selfOthersGain.get(p) == null){

        }
        othersGain.put(participantIndex,gain);
    }
    /**
     * Compares own gain with other's
     */
    public static void compareWithParticipants(){
        //List of encrypted bit by bit comparisons
        encryptedComparisonList = new ArrayList<>();
        //Procedure for candidate I
        for (Integer index: othersGain.keySet()) {

            //Computation of the gama's factors
            gamas = new ArrayList<>(l);
            //table of beta's
            betaI = othersGain.get(index).clone();
            for (int t = 0; t < l  ; t++) {
                tmp = homomorphicEncryption(betaI[t],myGain[t]);
                tmp2 = multHomomorphicEncryption(betaI[t],plainGain[t].multiply(BigInteger.valueOf(-2)));
                gamas.add(t,homomorphicEncryption(tmp, tmp2));
                //Un-comment and bring fields back in the method
                BigInteger expe1 = plainGain[t].add(decrypt(betaI[t])).mod(BigInteger.valueOf(2));
                BigInteger expe2 = plainGain[t].add(othersPlain[t]).mod(BigInteger.valueOf(2));
                BigInteger res = decrypt(gamas.get(t));

                if(!(othersPlain[t].equals(decrypt(betaI[t]))))
                    throw new IllegalStateException();
                if(!othersPlain[t].equals(decrypt(othersGain.get(index)[t])))
                    throw new IllegalStateException();
                if(!plainGain[t].equals(decrypt(myGain[t])))
                    throw new IllegalStateException();
                if(!expe1.equals(expe2))
                    throw new IllegalStateException();
                if(!expe2.equals(res))
                    throw new IllegalStateException();

            }
            /*
             * Optimisation begin with t = l so singe loop
             * Pull the creation outside the loops
             */
            sum = new BigInteger[l][2];
            negGamaT = new BigInteger[l][2];
            omegas = new BigInteger[l][2];
            taus = new BigInteger[l][2];
            List<BigInteger> plainGama = decryptMany(gamas);
            for (int t = l-1; t >= 0; t--) {
                val = ElGamal.encrypt(BigInteger.valueOf(l-t));
                tmp = ElGamal.getNegativeciphers(gamas.get(t));
                negGamaT[t] = multHomomorphicEncryption(tmp,BigInteger.valueOf(l-t));
                sum[t] = homomorphicEncryption(gamas.subList(t+1,l));
                omegas[t] = homomorphicEncryption(val, sum[t], negGamaT[t]);// E( l-t+1 + sum of (gamav - gmai) - gamai)
                taus[t] = homomorphicEncryption(omegas[t], myGain[t]);
                BigInteger expSum = BigInteger.valueOf(l-t);
                BigInteger expeNegGama = plainGama.get(t).multiply(expSum).negate();
                BigInteger expGamSum = SecureDotProductParty.vectorElementsSum((BigInteger[])plainGama.subList(t+1,l).toArray(new BigInteger[l-t-1]));
                BigInteger expeOmeg = expSum.add(expeNegGama).add(expGamSum);
                BigInteger expTau = expeOmeg.add(plainGain[t]);
                if(!compEncrypted(plainGama.get(t),(decrypt(gamas.get(t)))))
                    throw new IllegalStateException();
                if(!compEncrypted(expSum,decrypt(val)))
                    throw  new IllegalStateException();
                if(!compEncrypted(expeNegGama,decrypt(negGamaT[t])))
                    throw new IllegalStateException();
                if(!compEncrypted(expGamSum,decrypt(sum[t])))
                    throw new IllegalStateException();
                if(!compEncrypted(expeOmeg,decrypt(omegas[t])))
                    throw new IllegalStateException();
                if(!compEncrypted(expTau,decrypt(taus[t])))
                    throw new IllegalStateException();

            }
            encryptedComparisonList.add(taus);
        }
    }


    /**
     * Method that's compare two ElGamal encrypted numbers.
     * Computes the XOR bit-by-bit and finally.
     * Computation is done so that the first right most bit is set to Zero
     * Since the encryption Scheme is Homomorphic we can compute: M1+M2 also M1*M2
     * M1, M1 in F2
     * @param cipher1
     * @param cipher2
     */
    public static List<BigInteger[][]> compareNumbers(BigInteger[]plain1, BigInteger[][] cipher1, BigInteger[][] cipher2){
        if (cipher1.length != cipher2.length){
            throw new IllegalArgumentException("Ciphers must have the same size");
        }
        checkInput(cipher1);
        checkInput(cipher2);
        int l = cipher1.length;
        //List of encrypted bit by bit comparisons
       List<BigInteger[][]> encryptedComparisonList = new ArrayList<>();


            //Computation of the gama's factors
            List<BigInteger[]> gamas = new ArrayList<>(l);
            //table of beta's
            BigInteger [][] betaI = new BigInteger[l][2];
            BigInteger[] tmp;
            BigInteger [] tmp2;
            for (int t = 0; t <l ; t++) {
                tmp = homomorphicEncryption(cipher1[t],cipher2[t]);
                tmp2 = multHomomorphicEncryption(cipher2[t],plain1[t].multiply(BigInteger.valueOf(-2)));
                gamas.add(t,homomorphicEncryption(tmp,tmp2));
            }
            /*
             * Optimisation begin with t = l so singe loop
             * Pull the creation outside the loops
             */
            BigInteger[] val;//(l -t +1 )
            BigInteger[] [] sum = new BigInteger[l][2];
            BigInteger[][] negGamaT = new BigInteger[l][2];
            BigInteger[][] omegas = new BigInteger[l][2];
            BigInteger[][] taus = new BigInteger[l][2];
            for (int t = 0; t < l; t++) {
                val = ElGamal.encrypt(BigInteger.valueOf(l-t));
                tmp = ElGamal.getNegativeciphers(gamas.get(t));
                negGamaT[t] = multHomomorphicEncryption(tmp,BigInteger.valueOf(l-t));
                sum[t] = homomorphicEncryption(gamas.subList(t+1,l));
                omegas[t] = homomorphicEncryption(val,sum[t],negGamaT[t]);// E( l-t+1 + sum of (gamav - gmai))
                taus[t] = homomorphicEncryption(omegas[t], betaI[t]);
            }
            encryptedComparisonList.add(taus);

            return encryptedComparisonList;
    }


    /**
     * Check the encrypted input
     * Mostly for size
     * @param cipher1
     */
    private static void checkInput(BigInteger[][] cipher1) {
        for (BigInteger[] c: cipher1) {
            if(c.length!= 2 || c[0] == null || c[1] == null){
                throw new IllegalArgumentException("Bad cipher as argument either size or one of cipher compoment is Null");
            }
        }
    }

    public static BigInteger[][] getEncryptedCompWithCandidate(Integer candidateIndex){
        return encryptedComparisonList.get(candidateIndex);
    }

    public static List<BigInteger[][]> getEncryptedComparisons(){
        return new ArrayList<>(encryptedComparisonList);
    }


    /**
     * Method for chained decryption(i.e Phase 8)
     * @param list V vector in the paoer
     * @param myIndex
     * @param privateKey
     * @param prime
     * @return
     */
    public static List<List<BigInteger[][]>> chainedDecryption(List<List<BigInteger[][]>> list, int myIndex, BigInteger privateKey, BigInteger prime, BigInteger group) {
        BigInteger r;
        for (int i = 0; i < list.size() ; i++) {
            if(i != myIndex){
                for (int j = 0; j < list.size() ; j++) {
                    r = new BigInteger(group.bitLength(),new SecureRandom());
                    r = r.mod(group);
                    for (int k = 0; k <l ; k++) {
                        BigInteger ct = list.get(i).get(j)[k][0];
                        BigInteger ct_prime = list.get(i).get(j)[k][1];
                        ct = (ct.multiply((ct_prime.modPow(privateKey,prime)).modInverse(prime))).modPow(r,prime);
                        list.get(i).get(j)[k][0] = ct;
                        list.get(i).get(j)[k][1] = ct_prime.modPow(r,prime);
                    }
                    Collections.shuffle(Arrays.asList(list.get(i).get(j)));
                }
            }
        }
        return list;
    }


    /**
     * Returns the binary representation of a number as an array.
     * i.e if g = abcd with a,b,c,d in {0,1}
     * then the array is [d,c,b,a] and indexOf(d) = 0;
     * @param gain the number to convert
     * @return
     */
    public static BigInteger[] createBinaryArray(BigInteger gain, int length){
        String t = (gain.toString(2));
        int l = t.length();
        BigInteger[] binaryVector = new BigInteger[length];
        for (int i = 0; i < length ; i++) {
            if(i < l) {
                binaryVector[i] = new BigInteger(String.valueOf(t.charAt(l - i - 1)));
            }
            else{
                binaryVector[i] = BigInteger.ZERO;
            }
        }
        return  binaryVector;
    }

    public static int finalDecryption(List<BigInteger[][]> comp){
        ArrayList<BigInteger> res;
        int nb = 0;
        for (BigInteger[][] b: comp) {
            res = (ArrayList)decryptMany(Arrays.asList(b));
            for (BigInteger c: res) {
                    if(c.equals(BigInteger.ZERO)){
                        nb++;
                    }
                }
            }
        return nb + 1 ;
    }


    public static BigInteger[] getOthersPlain() {
        return othersPlain;
    }

    public static void setOthersPlain(BigInteger[] othersPlain) {
        EncryptedBinaryComparator.othersPlain = othersPlain;
    }

    private static boolean compEncrypted(BigInteger expected, BigInteger res){
        return (!expected.equals(BigInteger.ZERO) && res.equals(BigInteger.ONE) ||
                expected.equals(BigInteger.ZERO) && res.equals(BigInteger.ZERO));
    }

    public static int findDiffindex(BigInteger n1, BigInteger n2){
        int length = Math.max(n1.bitLength(),n2.bitLength());
        BigInteger[] v1 = createBinaryArray(n1,length);
        BigInteger[] v2 = createBinaryArray(n2,length);
        int index = v1.length - 1;
        boolean found = false;
        while(!found){
            if(!v1[index].equals(v2[index])){
                found = true;
            }
            else{
                index--;
            }
        }
        return index;
    }
}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.vectorsElemModExpo;
import static lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty.vectorsElemMult;


public class GainComparisonHelper {

    private static BigInteger[][] myGain;
    private static Map<Integer, BigInteger[][]> othersGain;
    private static List<BigInteger[][]> encryptedComparisonList;
    private String myPlainGain;
    private static int l;
    private static GainComparisonHelper gainComparisonTool;
    private int participantNumber;

    private GainComparisonHelper(){

    }

    public static GainComparisonHelper getGainComparisonTool(){
        if(gainComparisonTool == null){
            gainComparisonTool = new GainComparisonHelper();
        }
        return gainComparisonTool;
    }

    public static void setMyGain(BigInteger [][] gain){
        if(gain == null)
            throw new NullPointerException("Null gain");
        l = gain.length;
        myGain = gain.clone();
    }

    public static void setOthersGain(Integer participantIndex, BigInteger[][] gain){
        othersGain.put(participantIndex,gain);
    }

    public void compareWithParticipants(){
        encryptedComparisonList = new ArrayList<>();
        for (Integer index: othersGain.keySet()) {
            //Computation of the gama's factors
            List<BigInteger[]> gamas = new ArrayList<>(l);
            BigInteger [] betaT;
            int myBetaPlainT;
            for (int t = 0; t <l ; t++) {
                betaT = othersGain.get(index)[t];
                myBetaPlainT = Integer.valueOf(myPlainGain.charAt(t));
                gamas.add(t,SecureDotProductParty.vectorsElemMult(myGain[t],betaT));
                //TODO: Change so to hide the computation
                betaT = ElGamal.multHomomorphicEncryption(betaT, BigInteger.valueOf(-(2*myBetaPlainT)));
                gamas.set(t,vectorsElemMult(gamas.get(t), betaT));
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
                val = ElGamal.encrypt(BigInteger.valueOf(l-t+1));
                negGamaT[t] = ElGamal.getNegativeciphers(gamas.get(t));;
                for (int v = t +1; v < 1 ; v++) {
                    sum[t] = ElGamal.homomorphicEncryption(gamas.get(v),sum[t]);
                }
                omegas[t] = ElGamal.homomorphicEncryption(val,sum[t],negGamaT[t]);// E( l-t+1 + sum of (gamav - gmai))
                taus[t] = ElGamal.homomorphicEncryption(omegas[t], myGain[t]);
            }
            encryptedComparisonList.add(taus);
        }
    }

    public BigInteger[][] getEncryptedCompWithcanditate(Integer i){
        return encryptedComparisonList.get(i);
    }

    public static List<BigInteger[][]> getEncryptedComparisons(){
        return new ArrayList<>(encryptedComparisonList);
    }


    public List<List<BigInteger[][]>> processList(List<List<BigInteger[][]>> list, int myIndex, BigInteger privateKey, BigInteger prime) {
        BigInteger r;
        for (int i = 0; i < list.size() ; i++) {
            if(i != myIndex){
                for (int j = 0; j < list.size() ; j++) {
                    r = new BigInteger(Application.group.bitLength()-1,new SecureRandom());
                    for (int k = 0; k <l ; k++) {
                        BigInteger ct = list.get(i).get(j)[k][0];
                        BigInteger ct_prime = list.get(i).get(j)[k][1];
                        ct = (ct.divide(ct_prime.modPow(privateKey,prime))).modPow(r,prime);
                        list.get(i).get(j)[k][0] = ct;
                        list.get(i).get(j)[k][1] = ct_prime.modPow(r,prime);
                    }
                    Collections.shuffle(Arrays.asList(list.get(i).get(j)));
                }
            }
        }
        return list;
    }

}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.Poll;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.System.arraycopy;


public class Owner {
    //TODO: Normal users with inheritance
    public SecureDotProductParty me;
    private BigInteger rho;
    private int h;
    private BigInteger[] criterionVector;
    public Poll myPoll;



    private BigInteger[] myAttVector; // 10 for test
    private BigInteger[] myWeightVector;
    private BigInteger[] wPrimeVector;
    public int l;
    private int k = 2 ; //for now
    private int t = 5; // for now
    private SecureRandom random;
    private final int greaterLength;
    private final int equalLength;

    public Owner(BigInteger criterionEqualVector [],BigInteger[] criterionGreaterVector, BigInteger[] weightEqualVector, BigInteger[] weightGreaterVector){
        greaterLength = criterionGreaterVector.length;
        equalLength = criterionEqualVector.length;

        l = (int) ceil(log(equalLength + criterionGreaterVector.length)) +
                criterionEqualVector[0].bitLength() +
                2*weightEqualVector[0].bitLength() + 2 ;
        random = new SecureRandom();

        do{
            h = random.nextInt() % 20;
        }while (h <= 0);
        //TODO: Decomment following line
        rho = (new BigInteger(h, random)).abs();

        generateOwnerVprime(criterionEqualVector,weightGreaterVector,weightEqualVector);

        me = new SecureDotProductParty(rho);
        me.setMyvector(wPrimeVector);



    }

    private void generateOwnerVprime(BigInteger[] criterionEqVector, BigInteger[] weightEqualVector, BigInteger[] weightGreaterVector) {
        /*Vector of size gr + 2 *eq*/
        wPrimeVector = new BigInteger[greaterLength + 2*equalLength];

        /*[vgT,..]*/
        arraycopy(weightGreaterVector,0, wPrimeVector,0,greaterLength);

        BigInteger v[] = new BigInteger[equalLength];
        /* [vgT,(ve*ve)T,..]*/
        for (int i = 0; i < equalLength ; i++) {
            v[i] = weightEqualVector[i].negate();
        }
        arraycopy(v,0,wPrimeVector,greaterLength,equalLength);

        for (int i = 0; i <equalLength; i++) {
            v[i] = weightEqualVector[i].multiply(criterionEqVector[i]);
            v[i] = v[i].add(v[i]);
        }
        arraycopy(v,0,wPrimeVector,greaterLength+equalLength,equalLength);
        for (int i = 0; i < wPrimeVector.length ; i++) {
            wPrimeVector[i] = wPrimeVector[i].multiply(rho);
        }

        myAttVector = new BigInteger[wPrimeVector.length];
        for (int i = 0; i <myAttVector.length; i++) {
            myAttVector[i] = wPrimeVector[i];
        }
    }

    public void initiatePoll(int Participants){


        myPoll = new Poll(Participants);

    }


    public BigInteger[] getMyAttVector() {
        return myAttVector.clone();
    }




}

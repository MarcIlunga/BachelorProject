package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;

import java.math.BigInteger;
import java.security.SecureRandom;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto.SecureDotProductParty;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.Poll;

import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.System.arraycopy;
import static junit.framework.Assert.assertTrue;


public class Initiator {
    //TODO: Normal users with inheritance
    public SecureDotProductParty secureDotProductParty;
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
    private int currParticipant;
    private BigInteger prime;
    private BigInteger group;
    private BigInteger generator;

    public Initiator(BigInteger criterionEqualVector [], BigInteger[] criterionGreaterVector, BigInteger[] weightEqualVector, BigInteger[] weightGreaterVector){
        greaterLength = criterionGreaterVector.length;
        equalLength = criterionEqualVector.length;


        random = Application.random;

        do{
            h = 15;
        }while (h <= 0);

        l =  (h +(int) ceil(log(equalLength + greaterLength)) +
                criterionEqualVector[0].bitLength() +
                2*weightEqualVector[0].bitLength() + 3) ;

        //TODO: Decomment following line
        rho = BigInteger.ONE; // (new BigInteger(h, random)).abs();

        generateOwnerVprime(weightGreaterVector,weightEqualVector,criterionEqualVector);

        secureDotProductParty = new SecureDotProductParty(random);
        secureDotProductParty.setMyvector(wPrimeVector);
        secureDotProductParty.setRho(rho);
        //TODO: REMOVE AFTER TEST
        for (int i = 0; i < greaterLength  ; i++) {
            assertTrue(weightGreaterVector[i].equals(wPrimeVector[i]));
        }

        for (int i = 0; i < equalLength; i++) {
            assertTrue(wPrimeVector[i+greaterLength].equals(rho.negate().multiply(weightEqualVector[i])));
            BigInteger res = wPrimeVector[i+greaterLength+equalLength];
            BigInteger exp = rho.multiply(weightEqualVector[i]).
                    multiply(criterionEqualVector[i]).
                                    multiply(BigInteger.valueOf(2));

            assertTrue(res.equals(exp));
        }




    }

    private void generateOwnerVprime(BigInteger[] weightGreaterVector, BigInteger[] weightEqualVector, BigInteger[] criterionEqVector ) {
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


    public void setCurrParticipant(int currParticipant) {
        this.currParticipant = currParticipant;
    }

    public int getCurrParticipant() {
        return currParticipant;
    }

    public void sendAH(Participant participant) {
        secureDotProductParty.sendAH(participant.secureDotProduct);
    }
}

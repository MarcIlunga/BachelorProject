package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GroupGenerator {

    private int minBitLength;

    int certainty;

    private static BigInteger ONE = BigInteger.ONE;
    private static BigInteger TWO = ONE.add(ONE);
    private static BigInteger generator, prime, group;
    private SecureRandom secureRandom;
    private static GroupGenerator groupGenerator;

    public GroupGenerator(int minBitLength, int certainty, SecureRandom secureRandom, boolean lengthCheck){
        if(minBitLength < 512 && lengthCheck )
                throw  new IllegalArgumentException("Prime should have at least 512 bits");
        this.minBitLength = minBitLength;
        this.certainty = certainty;
        this.secureRandom = secureRandom;
        getSafePrime();

    }

    private GroupGenerator(){

    }



    private void getSafePrime(){

        BigInteger a;
        do {
            a = new BigInteger(minBitLength,secureRandom);

            a = a.add(ONE);

            group = new BigInteger(minBitLength,certainty,secureRandom);

            prime = a.multiply(group).add(ONE);
        }
        while(!prime.isProbablePrime(certainty));



        boolean isGen;
        do{
            isGen = true;
            generator = new BigInteger(prime.bitLength(),secureRandom);
            generator = generator.mod(prime.subtract(BigInteger.ONE)).add(BigInteger.ONE);
            generator = generator.modPow(a, prime);
            if(generator.equals(ONE)){
                isGen = false;
            }
        }while (!(isGen && !generator.equals(BigInteger.ZERO)));
    }


    public BigInteger getPrime(){
        if(prime == null)
        {
            getSafePrime();
        }
        return prime;
    }
    public  BigInteger getGenerator(){
        return  generator;
    }

    public BigInteger getGroup(){
        return group;
    }

/*    public boolean isGenerator(BigInteger p, BigInteger g, int certainty ){
        if(!p.isProbablePrime(certainty)){
            Log.d("<<<<", p.toString() + "is not prime ");
            return false
        }
        if(g.mod(p).equals(ZERO)){
            Log.d("<<<<", p.toString() + " divides " + g.toString());
            return false
        }

        BigInteger p
    }
*/

}

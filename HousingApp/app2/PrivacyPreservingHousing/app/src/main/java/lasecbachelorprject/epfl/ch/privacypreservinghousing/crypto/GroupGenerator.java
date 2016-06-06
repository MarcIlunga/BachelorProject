package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;
import java.security.SecureRandom;

public class GroupGenerator {

    private static int minBitLength;

    private static int certainty;

    private static BigInteger ONE = BigInteger.ONE;
    private static BigInteger generator, prime, group;
    private static SecureRandom secureRandom;

    public GroupGenerator(int minBitLength, int certainty, SecureRandom secureRandom, boolean lengthCheck){
        if(minBitLength < 1024 && lengthCheck )
                throw  new IllegalArgumentException("Prime should have at least 512 bits");
        this.minBitLength = minBitLength;
        this.certainty = certainty;
        this.secureRandom = secureRandom;
        getSafePrime();

    }



    private static void getSafePrime(){

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


    public static BigInteger getPrime(){
        if(prime == null)
        {
            getSafePrime();
        }
        return prime;
    }
    public  static BigInteger getGenerator(){
        return  generator;
    }

    public  static BigInteger getGroup(){
        return group;
    }



}

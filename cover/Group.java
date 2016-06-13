package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

import java.math.BigInteger;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities.Application;

public class Group {
    private BigInteger prime;
    private BigInteger generator;
    private static Group group;

    private Group(BigInteger prime, BigInteger generator){
        this.prime = prime;
        this.generator = generator;
    }

    public static Group getGroup(){
        if(group == null){
            group = new Group(Application.prime, Application.generator);
        }
        return  group;
    }

    public BigInteger getPrime(){
       return prime;
    }

    public BigInteger getGenerator(){
        return generator;
    }
}

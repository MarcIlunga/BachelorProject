package lasecbachelorprject.epfl.ch.privacypreservinghousing.crypto;

public class SecureDotProduct  {
    public SecureDotProductParty alice;
    public SecureDotProductParty bob;

    public SecureDotProduct(SecureDotProductParty bob, SecureDotProductParty alice) {
        if(alice == null || bob == null ){
            throw new IllegalArgumentException("Can't compute with a null entity");
        }

        this.bob = bob;
        this.alice = alice;

    }




}

package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;

public class Person  {



    public Person(){
        super();
    }


    public void setVectorValues(double[] v) {

    }

    public void sendInitialDataToOwner(Initiator initiator) {
        ((Participant)this).secureDotProduct.sendInitialDataToOtherParty(initiator.secureDotProductParty);
    }


}

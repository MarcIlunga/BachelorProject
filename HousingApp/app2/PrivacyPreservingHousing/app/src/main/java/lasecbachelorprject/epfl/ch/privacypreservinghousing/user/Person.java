package lasecbachelorprject.epfl.ch.privacypreservinghousing.user;

public class Person  {



    public Person(){
        super();
    }


    public void setVectorValues(double[] v) {

    }

    public void sendInitialDataToOwner(Owner owner) {
        ((Participant)this).secureDotProduct.sendInitialDataToOtherParty(owner.me);
    }


}

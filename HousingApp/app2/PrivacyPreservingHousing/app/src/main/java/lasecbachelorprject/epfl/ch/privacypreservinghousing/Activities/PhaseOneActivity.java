package lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.R;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Initiator;

public class PhaseOneActivity extends AppCompatActivity {

    DataBase dataBase = DataBase.getDataBase();
    Initiator initiator = dataBase.getInitiator();
    //int replies = dataBase.getPoll(pollNb).participantsNumber;
    int replyCount = 0;

    TextView instruction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phase_one);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        instruction = (TextView) findViewById(R.id.phase1instruction);
    /*
        instruction.setText("Welcome to Phase 1"
                            + "/n"
                            + "There is " + replies+ "candidates"
                            +"Press the button so that each candidate " +
                            "can compute securely their masked Gain. "
                            );
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(replyCount <= replies){
                    SecureDotProductParty part = dataBase.participants.get(replyCount).secureDotProduct;
                    part.initiateDotProduct();
                    part.sendInitialDataToOtherParty(initiator.secureDotProductParty);
                    initiator.secureDotProductParty.sendAH(part);
                    replyCount++;
                    if(replyCount < replies ) {
                        instruction.setText("There are still" + (replies - replyCount) +
                                "candidates, keep going...");
                    }
                    else{
                        instruction.setText("Phase 1 finished"+
                                "/n" + "Press the button ");
                    }
                }
                else {
                    //Start phase 2
                }
            }
        });
        */
    }

}

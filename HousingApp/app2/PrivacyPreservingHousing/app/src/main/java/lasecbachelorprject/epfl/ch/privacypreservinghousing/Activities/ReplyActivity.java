package lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.R;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.DataBase;

public class ReplyActivity extends AppCompatActivity {

    private TextInputEditText location, from, to, distance, travelers, gender,
            privateRoom, sharedRoom, smoking,kidsAtHome, children;
    private FloatingActionButton saveSurvey;
    int replies =0;// DataBase.getDataBase().getPoll(pollNb).participantsNumber;
    int replyCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.survey_creation_activity);
        location = (TextInputEditText) findViewById(R.id.surveyLocation);
        from = (TextInputEditText) findViewById(R.id.surveyFrom);
        to = (TextInputEditText) findViewById(R.id.surveyTo);
        distance = (TextInputEditText) findViewById(R.id.surveyDistance);
        travelers = (TextInputEditText) findViewById(R.id.surveyTravelers);
        gender = (TextInputEditText) findViewById(R.id.surrveyGender);
        privateRoom = (TextInputEditText) findViewById(R.id.surveyPrivateRoom);
        sharedRoom = (TextInputEditText) findViewById(R.id.surveySharedRoom);
        smoking = (TextInputEditText) findViewById(R.id.surveySmoking);
        kidsAtHome = (TextInputEditText)findViewById(R.id.surveyKidsAtHome);
       // children = (TextInputEditText) findViewById(R.id.surveyChildren);
        saveSurvey = (FloatingActionButton) findViewById(R.id.saveSurveyButton);
        saveSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReply();
                if(replyCount == replies) { //Start Phase 1
                    Intent intent = new Intent(ReplyActivity.this, PhaseOneActivity.class);

                }
                else {
                    Intent intent = new Intent(ReplyActivity.this,ReplyActivity.class);
                }
            }
        });
    }

    private void submitReply() {
        /*BigInteger[] reply = new BigInteger[10];
        reply [0] = new BigInteger(location.getText().toString());
        reply[1] = new BigInteger(from.getText().toString());
        reply[2] = new BigInteger(to.getText().toString());
        reply[3] = new BigInteger(distance.getText().toString());
        reply[4] = new BigInteger(travelers.getText().toString());
        reply[5] = new BigInteger(gender.getText().toString());
        reply[6] = new BigInteger(privateRoom.getText().toString());
        reply[7] = new BigInteger(sharedRoom.getText().toString());
        reply[8] = new BigInteger(smoking.getText().toString());
        reply[9] = new BigInteger(kidsAtHome.getText().toString());
        Participant p = new Participant(reply);
        DataBase.getDataBase().addParticipant(p);
        replyCount++;*/
    }


}

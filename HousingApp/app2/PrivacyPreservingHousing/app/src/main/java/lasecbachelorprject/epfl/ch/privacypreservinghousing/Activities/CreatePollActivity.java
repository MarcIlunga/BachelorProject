package lasecbachelorprject.epfl.ch.privacypreservinghousing.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lasecbachelorprject.epfl.ch.privacypreservinghousing.R;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.helpers.Poll;
import lasecbachelorprject.epfl.ch.privacypreservinghousing.user.Owner;

public class CreatePollActivity extends AppCompatActivity {

    public static Owner owner;
    public static Poll poll;
     private TextInputEditText location, from, to, distance, travelers, gender,
                                privateRoom, sharedRoom, smoking,kidsAtHome, children;
    private FloatingActionButton saveSurvey;

    @Override
    public void onCreate(Bundle savedInstanceState){
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
        //children = (TextInputEditText) findViewById(R.id.surveyChildren);
        saveSurvey = (FloatingActionButton) findViewById(R.id.saveSurveyButton);
        saveSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitSurvey();
                Intent intent = new Intent(CreatePollActivity.this,ReplyActivity.class);
                startActivity(intent);
            }
        });
    }

    public void submitSurvey(){/*
        BigInteger [] criterion = new BigInteger[10];
        criterion [0] = new BigInteger(location.getText().toString());
        criterion[1] = new BigInteger(from.getText().toString());
        criterion[2] = new BigInteger(to.getText().toString());
        criterion[3] = new BigInteger(distance.getText().toString());
        criterion[4] = new BigInteger(travelers.getText().toString());
        criterion[5] = new BigInteger(gender.getText().toString());
        criterion[6] = new BigInteger(privateRoom.getText().toString());
        criterion[7] = new BigInteger(sharedRoom.getText().toString());
        criterion[8] = new BigInteger(smoking.getText().toString());
        criterion[9] = new BigInteger(kidsAtHome.getText().toString());
        owner  = new Owner(criterion, new BigInteger[]{BigInteger.ONE});
        owner.initiatePoll(5);
        DataBase.getDataBase().setOwner(owner);
        DataBase.getDataBase().setPoll(owner.myPoll);
    */
    }
}

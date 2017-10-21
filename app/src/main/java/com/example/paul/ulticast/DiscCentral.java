package com.example.paul.ulticast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class DiscCentral extends AppCompatActivity{

    //get authorization reference
    private FirebaseAuth mAuth;

    //declare variables for views
    Button sign_out, practiceButton, addUser_button, findUser;
    TextView username, practice, throwing;
    EditText newPracticeTime;
    String coach, user_UID;
    DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference();//get reference for root of database

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disc_central);//set layout

        //declare authorization and views
        mAuth = FirebaseAuth.getInstance();
        sign_out = (Button)findViewById(R.id.signout_button);
        practiceButton = (Button)findViewById(R.id.changePracticeButton);
        addUser_button = (Button)findViewById(R.id.addUser_button);
        username = (TextView) findViewById(R.id.tvName);
        practice = (TextView) findViewById(R.id.tvPractice);
        throwing = (TextView) findViewById(R.id.tvThrowing);
        newPracticeTime = (EditText) findViewById(R.id.next_practice);
        findUser = (Button)findViewById(R.id.findUser_button);


        //check if user is already logged in
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));//if not logged in, go back to login screen
        } else {
            //get current user and set display message
            FirebaseUser user = mAuth.getCurrentUser();
            user_UID = user.getUid();
            username.setText("Welcome, " + user.getDisplayName() + "!");
            myRootRef.child("Coaches").child("1").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    coach = snapshot.getValue(String.class);
                    checkAuthorized(coach);
                }
                @Override
                public void onCancelled (DatabaseError error) {
                    Log.d("","Failed to read coaches value", error.toException());
                }
            });
        }

        //listener for sign out button, if clicked, sign out of account and go back to login screen
        sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });

        //listener for change practice time button, if clicked change the value of practice on db
        practiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRootRef.child("practice").setValue(newPracticeTime.getText().toString().trim());
            }
        });

        //listener for add user button, if clicked go to add user screen
        addUser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddUser.class));
            }
        });

        //listener for find user button
        findUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                startActivity(new Intent(getApplicationContext(), FindUser.class));
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        myRootRef.child("practice").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);//grab data
                //showNotification(practice.getText().toString().trim(),text); //take out when not debugging this feature
                practice.setText(text);//set text view to grabbed data
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        myRootRef.child("throwing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String text = dataSnapshot.getValue(String.class);//grab
                //showNotification(practice.getText().toString().trim(),text);
                throwing.setText(text);//set text view to grabbed data
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("","Failed to read value", databaseError.toException());
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        //mAuth.signOut();
    }

    //function to check if authorized to add user
    private void checkAuthorized(String coach_ID) {
        if(coach_ID.equalsIgnoreCase(user_UID)) {
            addUser_button.setVisibility(View.VISIBLE);
        } else {
            addUser_button.setVisibility(View.GONE);
        }
    }

    //function to generate notification when practice has changed  *NOT BEING USED RIGHT NOW
    public void showNotification(String old_text, String new_text) {
        //PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this, DiscCentral.class), 0); //sets up a pending intent,
        //use when you want to start new activity for when the notification is called
        Resources r = getResources();
        Notification notification = new NotificationCompat.Builder(this)
                .setTicker(r.getString(R.string.notification_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(r.getString(R.string.notification_title))
                .setContentText("Change. From: " + old_text + " To: " + new_text)
                //.setContentIntent(pi)
                .setAutoCancel(true)
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }
}
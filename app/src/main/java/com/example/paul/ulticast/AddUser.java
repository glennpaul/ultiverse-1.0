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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddUser extends AppCompatActivity {

    //authorization reference
    FirebaseAuth mAuth;

    //declare variable to have local storage of user count
    int user_count;

    //database reference initialization
    DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference();//get reference for root of database
    DatabaseReference userCountRef = myRootRef.child("users").child("user_count");

    //views
    Button addUser_button;
    EditText etName, etTeam;


    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user_page);//choose layout

        //set authorization instance
        mAuth = FirebaseAuth.getInstance();

        //initialize views to variables
        addUser_button = (Button)findViewById(R.id.addUser_button);
        etName = (EditText) findViewById(R.id.name_input);
        etTeam = (EditText)findViewById(R.id.team_input);

        //check if user is already logged in, go back to login activity if not yet logged in
        if (mAuth.getCurrentUser() == null) {
            //finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }

        //add listener to add user button, if pressed, called function to create new user in db
        addUser_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser(
                        String.valueOf(user_count+1),
                        etName.getText().toString().trim(),
                        etTeam.getText().toString().trim());
            }
        });

        //add value listener for user count in database
        userCountRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_count = Integer.parseInt(dataSnapshot.getValue(String.class));//set local value whenever db value changes
                //finish();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("","Failed to read user count value", databaseError.toException());
            }
        });
    }

    //function to create a new user
    private void createNewUser(String userID, String name, String team) {
        myRootRef.child("users").child(userID).child("Name").setValue(name);
        myRootRef.child("users").child(userID).child("Team").setValue(team);
        myRootRef.child("users").child(userID).child("UserID").setValue(userID);
        myRootRef.child("users").child("user_count").setValue(String.valueOf(Integer.parseInt(userID)));
    }

}

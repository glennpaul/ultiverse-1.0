package com.example.paul.ulticast;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShowProfile extends AppCompatActivity {
    TextView namePrompt, teamPrompt, nameShow, teamShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_profile);//chooses layout
        final String profile = getIntent().getStringExtra("profile");



        namePrompt = (TextView) findViewById(R.id.tvNamePrompt);
        teamPrompt = (TextView) findViewById(R.id.tvTeamPrompt);
        nameShow = (TextView) findViewById(R.id.tvNameDisplay);
        teamShow = (TextView) findViewById(R.id.tvTeamDisplay);

        DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference();//get reference for root of database

        Query query = myRootRef.child("users").orderByChild("Name").equalTo(profile);query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    if (postSnapshot.getKey().equals("Name")) {
                        nameShow.setText(postSnapshot.getValue().toString());
                    } else if (postSnapshot.getKey().equals("Team")) {
                        teamShow.setText(postSnapshot.getValue().toString());
                    }
                }
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
//
//
//
//                    Toast.makeText(ShowProfile.this,postSnapshot.getValue().toString(), Toast.LENGTH_SHORT).show();
//                    if (postSnapshot.getKey().equals(profile)) {
//                        nameShow.setText(postSnapshot.getValue().toString());
//                    } else if (postSnapshot.getKey().equals("Team")) {
//                        teamShow.setText(postSnapshot.getValue().toString());
//                    }
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });

    }

}

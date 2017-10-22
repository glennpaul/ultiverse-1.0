package com.example.paul.ulticast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FindUser extends AppCompatActivity {


    ArrayList<String> userList;
    ArrayAdapter<CharSequence> search_adaptor;
    ArrayAdapter<CharSequence> search_adaptor2, search_adaptor3;
    ArrayAdapter<String> adaptor;

    DatabaseReference myRootRef = FirebaseDatabase.getInstance().getReference();//get reference for root of database
    EditText teamName;
    ListView list;
    Button findButton;
    Spinner find_spinner, find_spinner2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_user);
        list = (ListView)findViewById(R.id.sampleListView);
        teamName = (EditText) findViewById(R.id.etTeam);
        findButton = (Button) findViewById(R.id.findButton);
        find_spinner = (Spinner) findViewById(R.id.search_spinner);
        find_spinner2 = (Spinner) findViewById(R.id.search_spinner2);



        //declare Array list, adaptor and set adaptor for drop down list and search results
        search_adaptor = ArrayAdapter.createFromResource(this, R.array.search_array,R.layout.support_simple_spinner_dropdown_item);
        search_adaptor.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        search_adaptor2 = ArrayAdapter.createFromResource(this, R.array.search_array_skills,R.layout.support_simple_spinner_dropdown_item);
        search_adaptor2.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        search_adaptor3 = ArrayAdapter.createFromResource(this, R.array.search_array_tournament,R.layout.support_simple_spinner_dropdown_item);
        search_adaptor3.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        find_spinner.setAdapter(search_adaptor);
        find_spinner2.setAdapter(search_adaptor2);
    }

    @Override
    protected void onStart() {
        super.onStart();
        userList = new ArrayList<>();//empty search results
        adaptor = new ArrayAdapter<>(this,R.layout.list_item,userList);//drop down list
        list.setAdapter(adaptor);
        find_spinner2.setVisibility(View.GONE);

        find_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString().equals("Skills")) {
                    find_spinner2.setVisibility(View.VISIBLE);
                    find_spinner2.setAdapter(search_adaptor2);
                    search_adaptor3.notifyDataSetChanged();
                } else if (parent.getItemAtPosition(position).toString().equals("Tournaments")) {
                    find_spinner2.setVisibility(View.VISIBLE);
                    find_spinner2.setAdapter(search_adaptor3);
                    search_adaptor3.notifyDataSetChanged();
                } else {
                    find_spinner2.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FindUser.this, ShowProfile.class);
                Toast.makeText(FindUser.this,parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
                intent.putExtra("profile",parent.getItemAtPosition(position).toString());
                startActivity(intent);

            }
        });


        //listener for add user button, if clicked go to add user screen
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userList.clear();
                adaptor.notifyDataSetChanged();
                //showNotification(find_spinner.getSelectedItem().toString(),"hi"); //used for debugging
                find(teamName.getText().toString().trim(), find_spinner.getSelectedItem().toString(), find_spinner2.getSelectedItem().toString());
            }
        });
    }

    //function to check if authorized to add user
    private void find(final String item, String category, final String extra) {
        if (category.equals("Skills")) {
            Query query = myRootRef.child(category).orderByChild(extra).startAt(Integer.parseInt(item));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        addUserToList(postSnapshot.child("Name").getValue().toString() + " - " + postSnapshot.child(extra).getValue().toString());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else if (category.equals("Tournaments")) {
            if (extra.equals("Search Players")) {
                Query query = myRootRef.child(category).orderByChild("Name").equalTo("Tournament:" + item);
                query.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            if (!postSnapshot.getValue().toString().equals("Tournament:" + item) && !postSnapshot.getValue().toString().contains("Date")) {
                                addUserToList(postSnapshot.getValue().toString());
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
            } else {
                Query query = myRootRef.child(category);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, Locale.CANADA);//declare date format
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            try {
                                //store string dates as formats
                                Date tourney_date = format.parse(postSnapshot.child("Start").getValue().toString().substring(6));
                                Date sel_date = format.parse(item);
                                if (tourney_date.before(sel_date)) {//check if before the selected date
                                        addUserToList(postSnapshot.child("Name").getValue().toString().substring(11));
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        } else {
            Query query = myRootRef.child("users").orderByChild(category).equalTo(item);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        addUserToList(postSnapshot.child("Name").getValue().toString());
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    //function to add user to Array List
    public void addUserToList (String name) {
        userList.add(name);
        adaptor.notifyDataSetChanged();
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

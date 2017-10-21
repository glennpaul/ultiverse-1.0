package com.example.paul.ulticast;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText etusername, etemail, etpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        final Button login, register;//declares button views

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);//chooses layout

        mAuth = FirebaseAuth.getInstance();//gets instance of the authorization for firebase

        //assigns variables to views
        login = (Button)findViewById(R.id.login_button);
        register = (Button)findViewById(R.id.register_button);
        etusername = (EditText) findViewById(R.id.username_input);
        etemail = (EditText) findViewById(R.id.email_input);
        etpassword = (EditText) findViewById(R.id.password_input);

        //check if user is already logged in, then go to home screen if already logged in
        if (mAuth.getCurrentUser() != null) {
            finish();
            startActivity(new Intent(getApplicationContext(), DiscCentral.class));
        }

        //listener for login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String getEmail = etemail.getText().toString().trim();//grab text from edit text for username
                String getPassword = etpassword.getText().toString().trim();//grab text from edit text for password
                callLogin(getEmail,getPassword);//call function to login with grabbed username and login
            }
        });

        //listener for register button
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String getEmail = etemail.getText().toString().trim();//grab text from edit text for username
                String getPassword = etpassword.getText().toString().trim();//grab text from edit text for password
                callRegister(getEmail,getPassword);//call function to register with grabbed username and password
            }
        });
    }

    //Function to register a new account
    private void callRegister(String email,String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "createUserWithEmail:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) { //if fail, display fail message to user
                            Toast.makeText(LoginActivity.this,"Registration Failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else { //if successful
                            UserProfile();//call function to create profile in database
                            Toast.makeText(LoginActivity.this,"Registration Complete.", Toast.LENGTH_SHORT).show();//notify user successful
                            Log.d("TESTING","Created Account");
                        }
                    }
                });
    }

    //Create Account
    private void UserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(etusername.getText().toString().trim())//set display name for user
                    .build();
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {//log successful creation of user
                                Log.d("TESTING", "User Profile updated.");
                            }
                        }
                    });
        }
    }

    //Start sign in process
    private void callLogin(String email,String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("TESTING", "Sign In Successful" + task.isSuccessful());
                        if (!task.isSuccessful()) {//if failed login, notify user
                            Log.v("TESTING", "signInWIthEmail:failed",task.getException());
                            Toast.makeText(LoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                        }
                        else {//if succesful sign in, go to home home screen
                            Intent intent = new Intent(LoginActivity.this, DiscCentral.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                });

    }

}

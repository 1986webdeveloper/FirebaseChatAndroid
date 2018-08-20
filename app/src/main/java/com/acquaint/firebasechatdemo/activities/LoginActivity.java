package com.acquaint.firebasechatdemo.activities;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acquaint.firebasechatdemo.GlobalData;

import com.acquaint.firebasechatdemo.R;
import com.acquaint.firebasechatdemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;
    SharedPreferences sPref;
    User user;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String regId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    //    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    FirebaseMessaging.getInstance().subscribeToTopic("user_"+"fab");


                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };
*/
        // set the view now

        //Get Firebase auth instance

        getLastLoginDatafromSPrefs();


        if(!GlobalData.isNetworkAvailable(LoginActivity.this)){
        Toast.makeText(LoginActivity.this,"Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
    }
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();


        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                GlobalData.hideKeyboard(LoginActivity.this);
                final String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }


                if(!GlobalData.isNetworkAvailable(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this,"Please Check Your Internet Connection", Toast.LENGTH_LONG).show();
                }
                else {

                    progressBar.setVisibility(View.VISIBLE);
                    //authenticate user
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.
                                    progressBar.setVisibility(View.GONE);
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                    } else {

                                        String userId = task.getResult().getUser().getUid();
                                        getProfileDatafromDb(userId);
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                }
            }
        });
    }

  /*  private void displayFirebaseRegId(String fname, String lname) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);

        final String uname1=fname+" "+lname;
        FirebaseDatabase.getInstance()
                .getReference("tokens").child(uname1)
                .setValue(new ChatMessage(uname1,
                        regId)
                );




         Log.e(TAG, "Firebase reg id: " + regId);



    }*/


    private void getProfileDatafromDb(final String userId) {
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'properties' node
        mFirebaseDatabase = mFirebaseInstance.getReference("user");

        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);
                String fname=user.getFname();
                String lname=user.getLname();
                String email=user.getEmail();
                String gender = user.getGender();
                String phone=user.getPhone();
             /*   displayFirebaseRegId(fname,lname);*/


                setDataToSharedPreference(userId,fname,lname,email,gender,phone);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Database Error",""+databaseError);
            }
        });
    }

    private void setDataToSharedPreference(String userId, String fname, String lname, String email, String gender, String phone) {
        SharedPreferences sharedPreferences= getSharedPreferences("my prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("id", userId);
        editor.putString("email",email);
        editor.putString("fname",fname);
        editor.putString("lname",lname);
        editor.putString("gender",gender);
        editor.putString("phone",phone);
        editor.commit();
    }

    private void getLastLoginDatafromSPrefs() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            SharedPreferences sharedPreferences= getSharedPreferences("my prefs", Context.MODE_PRIVATE);
            if(sharedPreferences!=null){
                String userid = sharedPreferences.getString("id",null);
                String email = sharedPreferences.getString("email",null);
                if(userid!=null && email!=null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }

            }

        }
    }
}

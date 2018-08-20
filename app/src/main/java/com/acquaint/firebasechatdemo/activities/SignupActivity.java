package com.acquaint.firebasechatdemo.activities;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.acquaint.firebasechatdemo.GlobalData;
import com.acquaint.firebasechatdemo.R;
import com.acquaint.firebasechatdemo.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

     EditText et_email, et_password, et_fname,et_lname,et_phone;
     Button btnSignIn, btnSignUp;
     RadioButton rb_male,rb_female;
     RadioGroup rg_gender;
     ProgressBar progressBar;
     FirebaseAuth auth;
     String userId;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        initWidgets();



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GlobalData.hideKeyboard(SignupActivity.this);
                if (validation()) {

                final String fname = et_fname.getText().toString().trim();
                final String lname = et_lname.getText().toString().trim();
                final String gender;
                if(rb_female.isChecked()==true){
                    gender="Female";
                }
                else {
                    gender="Male";
                }
                final String email = et_email.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                final String phone = et_phone.getText().toString().trim();



                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                           //     Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.

                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_LONG).show();
                                    Log.e("SignUp Error","Exception"+task.getException());

                                } else {


                                    userId=task.getResult().getUser().getUid();
                                    savetoSharedPrefs(userId,email,fname,lname,gender,phone);
                                    saveDataToDb(fname,lname,email,gender,phone);
                                    startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
                }
            }
        });
    }

    private void savetoSharedPrefs(String userId, String email, String fname, String lname, String gender, String phone) {
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

    private void initWidgets() {
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mFirebaseInstance = FirebaseDatabase.getInstance();
        // get reference to 'properties' node
        mFirebaseDatabase = mFirebaseInstance.getReference("user");

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        et_fname=(EditText) findViewById(R.id.et_fname);
        et_lname=(EditText)findViewById(R.id.et_lname);
        et_phone=(EditText)findViewById(R.id.et_phone);
        rb_female=(RadioButton)findViewById(R.id.rb_female);
        rb_male=(RadioButton)findViewById(R.id.rb_male);
        rg_gender=(RadioGroup)findViewById(R.id.rg_gender);

    }

    private void saveDataToDb(String fname, String lname, String email, String gender, String phone) {

    Toast.makeText(SignupActivity.this,"Data Saved to Db Successfully", Toast.LENGTH_LONG).show();

        if (TextUtils.isEmpty(userId)) {

        }


        User user =new User(fname,lname,email,gender,phone);

        mFirebaseDatabase.child(userId).setValue(user);
        reset();
    //    Toast.makeText(SignupActivity.this,"Data Posted Successfully",Toast.LENGTH_LONG).show();

    }

    private void reset() {
        et_fname.setText("");
        et_lname.setText("");
        et_email.setText("");
        et_password.setText("");
        et_phone.setText("");
        rb_female.setChecked(false);
        rb_male.setChecked(false);
    }

    private boolean validation() {
        if(et_fname.getText().toString().equalsIgnoreCase("")){
            et_fname.setError(getString(R.string.er_fname));
            return false;
        }
        else if(et_lname.getText().toString().equalsIgnoreCase("")){
            et_lname.setError(getString(R.string.er_lname));
            return false;
        }
        else if(et_email.getText().toString().equalsIgnoreCase("")){
            et_email.setError(getString(R.string.et_email));
            return false;
        }
        else if(et_password.getText().toString().equalsIgnoreCase("")){
            et_password.setError("Please Enter Password");
            return false;
        }
        else if(rb_male.isChecked()==false && rb_female.isChecked()==false){
            rb_female.setError(getString(R.string.er_gender));
        }
        else if(et_phone.getText().toString().equalsIgnoreCase("")){
            et_phone.setError(getString(R.string.er_phonenull));
            return false;
        }
        else if(et_phone.getText().toString().length()!=10){
            et_phone.setError(getString(R.string.er_phonelength));
            return false;
        }
        else {
            return true;
        }

        return true;

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
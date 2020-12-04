package com.javalearning.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Delayed;

public class MainActivity3 extends Activity {
    private EditText SignUp_Name;
    private EditText SignUp_Username;
    private EditText SignUp_Password;
    private Button signUp_Btn;
    private TextView already_member;
    private FirebaseAuth firebase;
    private ProgressDialog progressDialog;
    private boolean isInit = false;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        init();
        progressDialog = new ProgressDialog(this);
        firebase = FirebaseAuth.getInstance();
        signUp_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = SignUp_Name.getText().toString();
                String user = SignUp_Username.getText().toString().trim();
                String pass = SignUp_Password.getText().toString().trim();
                if (user.isEmpty() || pass.isEmpty() || name.isEmpty()){
                    Toast.makeText(MainActivity3.this,"Please enter your full information !!!",Toast.LENGTH_SHORT).show();
                }else{
                    if(user.contains("@gmail.com")) {
                        register(user,pass);
                    }else{
                        Toast.makeText(MainActivity3.this,"Your email isn't accepted. Must end with '@schedule.com' !!!",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        already_member.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
                startActivity(new Intent(MainActivity3.this,MainActivity.class));
            }
        });
    }

    public void register(String username,String password){
        progressDialog.setMessage("Registing...");
        progressDialog.show();
        firebase.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    sendEmailVerification();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity3.this,"Register Failed !!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void sendEmailVerification(){
        FirebaseUser firebaseUser = firebase.getCurrentUser();
        if(firebaseUser != null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(MainActivity3.this,"Successfully Registered. An email has been sent to your email !",Toast.LENGTH_SHORT).show();
                        firebase.signOut();
                        finish();
                        startActivity(new Intent(MainActivity3.this,MainActivity.class));
                    }else{
                        Toast.makeText(MainActivity3.this,"Verification mail hasn't been sent !",Toast.LENGTH_SHORT);
                    }
                }
            });
        }
    }

    public void init(){
        if(!isInit) {
            SignUp_Name = (EditText)findViewById(R.id.SignUp_nameInput);
            SignUp_Username = (EditText) findViewById(R.id.SignUp_userInput);
            SignUp_Password = (EditText) findViewById(R.id.SignUp_passInput);
            signUp_Btn = (Button) findViewById(R.id.SignUp_Btn);
            already_member = (TextView)findViewById(R.id.Already_Member_Btn);
            isInit = true;
        }
    }

    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 1500);
    }
}
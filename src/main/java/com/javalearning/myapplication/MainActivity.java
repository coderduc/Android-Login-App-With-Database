package com.javalearning.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
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

public class MainActivity extends AppCompatActivity {

    private EditText Username;
    private EditText Password;
    private Button loginBtn;
    private TextView btn_forgotPwd;
    private TextView btn_SignUp;
    private FirebaseAuth firebase;
    private String email2sent;
    private ProgressDialog progressDialog;
    private boolean isInit = false;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        progressDialog = new ProgressDialog(this);
        firebase = FirebaseAuth.getInstance();
        FirebaseUser curUser = firebase.getCurrentUser();
        if(curUser != null){
            finish();
            startActivity(new Intent(MainActivity.this,MainActivity2.class));
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = Username.getText().toString().trim();
                String pass = Password.getText().toString().trim();
                if (user.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please fill username and password !!!",Toast.LENGTH_LONG).show();
                }else{
                    if(user.contains("@gmail.com")) {
                        validate(user, pass);
                    }else{
                        Toast.makeText(MainActivity.this,"Your email isn't accepted !!!",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        btn_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish();
                startActivity(new Intent(MainActivity.this,MainActivity3.class));
            }
        });

        btn_forgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotDialog(MainActivity.this);
            }
        });
    }

    private void showForgotDialog(Context c) {
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setMessage("Enter your email: ")
                .setView(taskEditText)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        email2sent = String.valueOf(taskEditText.getText());
                        firebase.sendPasswordResetEmail(email2sent).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this,"An email has been sent to your email !!!",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(MainActivity.this,"An error while sending email !!!",Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .create();
        dialog.show();
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

    public void validate(String username,String password){
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        firebase.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    progressDialog.dismiss();
                    isEmailVerified();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this,"Username or password is incorrect !!!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void isEmailVerified(){
        FirebaseUser firebaseUser = firebase.getInstance().getCurrentUser();
        Boolean isVerified = firebaseUser.isEmailVerified();
        if(isVerified){
            finish();
            Toast.makeText(MainActivity.this,"Login Successful !!!",Toast.LENGTH_LONG).show();
            startActivity(new Intent(MainActivity.this,MainActivity2.class));
        }else{
            Toast.makeText(MainActivity.this,"Please verify your email !!!",Toast.LENGTH_LONG).show();
            firebase.signOut();
        }
    }

    public void init(){
        if(!isInit) {
            Username = (EditText) findViewById(R.id.userInput);
            Password = (EditText) findViewById(R.id.passwordInput);
            loginBtn = (Button) findViewById(R.id.btn_Login);
            btn_SignUp = (TextView)findViewById(R.id.btn_SignUp);
            btn_forgotPwd =  (TextView)findViewById(R.id.btnForgotPassword);
            isInit = true;
        }
    }
}

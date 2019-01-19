package com.rikin.jain.whatsappclone;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtEmail, edtUsername, edtPassword;
    private Button btnSignUp, btnLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Sign Up");
        ParseInstallation.getCurrentInstallation().saveInBackground();
        edtEmail = findViewById(R.id.edtEmail);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    onClick(btnSignUp);
                }
                return false;
            }
        });
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnLogIn.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSignUp:
                if( edtEmail.getText().toString().equals("") || edtUsername.getText().toString().equals("") || edtPassword.getText().toString().equals("")){
                    FancyToast.makeText(this, "Email, Username or password cannot be empty", FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();

                }
                else{
                 ParseUser whatsappUser = new ParseUser();
                whatsappUser.setEmail(edtEmail.getText().toString());
                whatsappUser.setUsername(edtUsername.getText().toString());
                whatsappUser.setPassword(edtPassword.getText().toString());
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Signing Up");
                progressDialog.show();
                whatsappUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            FancyToast.makeText(MainActivity.this,ParseUser.getCurrentUser().getUsername() +" is signed up successfully",
                                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            progressDialog.dismiss();
                            transitionToUsersActivity();
                        } else{
                            progressDialog.dismiss();
                            FancyToast.makeText(MainActivity.this,e.getMessage(),
                                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }

                    }
                });

                }
                break;
            case  R.id.btnLogIn:
                Intent intent = new Intent(MainActivity.this, LogIn.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    public  void transitionToUsersActivity(){
        Intent intent = new Intent(this, Users.class);
        startActivity(intent);
        finish();
    }
    public void rootLayoutIsTapped( View view){
       try {
           InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
           inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
       } catch (Exception e){
           e.printStackTrace();
       }
    }
}

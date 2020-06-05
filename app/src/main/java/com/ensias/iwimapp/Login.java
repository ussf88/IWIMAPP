package com.ensias.iwimapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends Activity {
    private EditText emailText;
    private EditText passwordText;
    private Button loginButton;
    private Button SignButton;
    private FirebaseAuth mAuth;
    @Override

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUIHome(currentUser);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        emailText =(EditText) findViewById(R.id.email);
        passwordText =(EditText) findViewById(R.id.password);
        loginButton =(Button) findViewById(R.id.Loginbutton);
        SignButton =(Button) findViewById(R.id.Signbutton);
        mAuth = FirebaseAuth.getInstance();
        SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Sign.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailText.getText().toString();
                String password=passwordText.getText().toString();
                Signinapp(email,password);



            }
        });

    }
    private void Signinapp(String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(Login.this,"please fill both fields",Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Login.this,"login secssesful",Toast.LENGTH_LONG).show();
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUIHome(user);

                            } else {
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }

                            // ...
                        }
                    });
        }
    }
    private void updateUIHome(FirebaseUser user){
        if (user !=null ){
            startActivity(new Intent(Login.this,Home.class));
        }
    }
}

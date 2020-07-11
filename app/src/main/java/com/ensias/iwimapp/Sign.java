package com.ensias.iwimapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

public class Sign extends Activity {
    private EditText emailText;
    private EditText passwordText;
    private EditText FirstName;
    private EditText LastName;
    private Button SignButton;
    private Button loginButton;
    private RadioGroup radiogroup;
    private RadioButton radioButton;
    private FirebaseAuth mAuth;
    String UserID;
    FirebaseFirestore db ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign);
        emailText =(EditText) findViewById(R.id.Semail);
        passwordText =(EditText) findViewById(R.id.Spassword);
        SignButton =(Button) findViewById(R.id.sign);
        loginButton =(Button) findViewById(R.id.login);
        FirstName =(EditText) findViewById(R.id.Sfirstname);
        LastName =(EditText) findViewById(R.id.Slastname);
        radiogroup = findViewById(R.id.Stype);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Sign.this,Login.class));
            }
        });
        SignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailText.getText().toString();
                String password=passwordText.getText().toString();
                CreateAccount(email,password);



            }
        });
    }
    private void CreateAccount(String email, String password){
        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(Sign.this,"please fill both fields",Toast.LENGTH_LONG).show();
        }
        else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("we are in 1");
                            UserID=mAuth.getCurrentUser().getUid();
                            DocumentReference UserDoc= db.collection("Users").document(UserID);
                            radioButton=findViewById(radiogroup.getCheckedRadioButtonId());
                            Map<String, Object> user = new HashMap<>();
                            user.put("FirstName",FirstName.getText().toString());
                            user.put("LastName", LastName.getText().toString());
                            user.put("Type", radioButton.getText().toString());
                            UserDoc.set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(Sign.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Sign.this,"Error Registration Failed1",Toast.LENGTH_LONG).show();
                                        }
                                    });

                            startActivity(new Intent(Sign.this,Home.class));
                        } else {
                            System.out.println("we are in 2");
                            Toast.makeText(Sign.this, "Error Registration Failed2",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        }
    }
}


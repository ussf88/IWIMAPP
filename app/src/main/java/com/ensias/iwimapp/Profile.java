package com.ensias.iwimapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Profile extends Activity implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    NavigationView nav;
    Toolbar navIcon;
    androidx.appcompat.widget.Toolbar t;
    ListView listView;
    FirebaseFirestore db ;
    String UserID;
    EditText pname;
    EditText plastname;
    EditText pemail;
    EditText ptype;
    Button btnChange;
    FirebaseUser userE ;
    ArrayList<EditText> fiels= new ArrayList<EditText>();


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        userE = FirebaseAuth.getInstance().getCurrentUser();
        pname= findViewById(R.id.Pname);
        plastname= findViewById(R.id.Plastname);
        pemail= findViewById(R.id.Pemail);
        ptype= findViewById(R.id.Ptype);
        db = FirebaseFirestore.getInstance();
        drawer= findViewById(R.id.drawer);
        nav= findViewById(R.id.nav);
        btnChange=findViewById(R.id.btnChange);
        btnChange.setVisibility(View.INVISIBLE);
        navIcon= findViewById(R.id.toolbar);
        nav.bringToFront();
        setActionBar(navIcon);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,R.string.open_nav,R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.setCheckedItem(R.id.nav_profile);
        UserID=mAuth.getCurrentUser().getUid();
        final DocumentReference UserDoc= db.collection("Users").document(UserID);
        UserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user=document.getData();
                        String type=(String)user.get("Type");
                        Log.e(TAG, "mystring "+type);
                        if (type.equals("Student")) {
                            nav.getMenu().findItem(R.id.nav_add_cours).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_add_timetable).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_chatd).setVisible(false);
                        } else if (type.equals("Teacher")) {
                            nav.getMenu().findItem(R.id.nav_add_timetable).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_chatd).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_chat).setVisible(false);
                        }else if (type.equals("head of sector")) {
                            nav.getMenu().findItem(R.id.nav_chat).setVisible(false);
                        }else if (type.equals("Delegate")) {
                            nav.getMenu().findItem(R.id.nav_add_cours).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_add_timetable).setVisible(false);
                        }
                        pname.setText((String)user.get("FirstName"));
                        plastname.setText((String)user.get("LastName"));
                        ptype.setText((String)user.get("Type"));
                        pemail.setText(userE.getEmail());
                        fiels.add(pname);
                        fiels.add(plastname);
                        fiels.add(ptype);
                        fiels.add(pemail);
                        for(int i=0; i<fiels.size();i++){
                            fiels.get(i).addTextChangedListener(new TextWatcher() {

                                public void afterTextChanged(Editable s) {
                                    btnChange.setVisibility(View.VISIBLE);
                                }

                                public void beforeTextChanged(CharSequence s, int start,
                                                              int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start,
                                                          int before, int count) {

                                }
                            });
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        btnChange.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                UserDoc
                        .update("FirstName",pname.getText().toString() )
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully updated!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error updating document", e);
                            }
                        });
                finish();
                startActivity(getIntent());

            }
        });

    }
    protected  void onResume(){
        super.onResume();
        nav= findViewById(R.id.nav);
        nav.setCheckedItem(R.id.nav_profile);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_course:
                Intent intent= new Intent(getApplicationContext(),Courses.class);
                startActivity(intent);
                break;
            case R.id.nav_home:
                Intent intent5 =new Intent(getApplicationContext(),Home.class);
                startActivity(intent5);
                break;

            case R.id.nav_add_cours:
                Intent intent3= new Intent(getApplicationContext(),addCourse.class);
                startActivity(intent3);
                break;
            case R.id.nav_profile:
                break;
            case R.id.nav_timetable:
                Intent intent4 =new Intent(getApplicationContext(),Timetable.class);
                startActivity(intent4);
                break;
            case R.id.nav_add_timetable:
                Intent intent2= new Intent(getApplicationContext(),addTimetable.class);
                startActivity(intent2);
                break;
            case R.id.nav_chat:
                Intent intent6 =new Intent(getApplicationContext(),Messaging.class);
                startActivity(intent6);
                break;
            case R.id.nav_chatd:
                Intent intent7 =new Intent(getApplicationContext(),Messaging_delegate.class);
                startActivity(intent7);
                break;
            case R.id.nav_logout:
                mAuth.getInstance().signOut();
                finish();
                startActivity( new Intent(getApplicationContext(),Sign.class));

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

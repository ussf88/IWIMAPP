package com.ensias.iwimapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class addCourse extends Activity implements View.OnClickListener , NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    NavigationView nav;
    Toolbar navIcon;
    androidx.appcompat.widget.Toolbar t;
    DateFormat formatter;
    Date dateObject;

    //this is the pic pdf code used in file chooser
    final static int PICK_PDF_CODE = 2342;

    //these are the views
    TextView textViewStatus;
    EditText editTextSubject;
    EditText editTextCourse;
    ProgressBar progressBar;
    FirebaseFirestore db ;
    String UserID;
    String prof = new String();

    //the firebase objects for storage and database
    StorageReference mStorageReference;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        formatter = new SimpleDateFormat("dd/MM/yyyy");
        dateObject=new Date();
        Log.e("mydate",formatter.format(dateObject));
        drawer= findViewById(R.id.drawer);
        nav= findViewById(R.id.nav);
        navIcon= findViewById(R.id.toolbar);
        nav.bringToFront();
        setActionBar(navIcon);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,R.string.open_nav,R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.setCheckedItem(R.id.nav_add_cours);
        //getting firebase objects
        mStorageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        //getting the views
        textViewStatus = (TextView) findViewById(R.id.textViewStatus);
        editTextSubject = (EditText) findViewById(R.id.ACsubject);
        editTextCourse = (EditText) findViewById(R.id.ACcourse);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();

        //attaching listeners to views
        findViewById(R.id.buttonUploadFile).setOnClickListener(this);





        UserID=mAuth.getCurrentUser().getUid();
        DocumentReference UserDoc= db.collection("Users").document(UserID);
        UserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user=document.getData();
                        String type=(String)user.get("Type");
                        if (type.equals("Student")){
                            nav.getMenu().findItem(R.id.nav_add_cours).setVisible(false);
                            nav.getMenu().findItem(R.id.nav_add_timetable).setVisible(false);
                        }
                        else if(type.equals("Teacher")){
                            nav.getMenu().findItem(R.id.nav_add_timetable).setVisible(false);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });







    }
    @Override
    protected  void onResume(){
        super.onResume();
        nav= findViewById(R.id.nav);
        nav.setCheckedItem(R.id.nav_add_cours);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_course:
                Intent intent= new Intent(getApplicationContext(),Courses.class);
                startActivity(intent);
                break;
            case R.id.nav_home:
                Intent intent2= new Intent(getApplicationContext(),Home.class);
                startActivity(intent2);
                break;
            case R.id.nav_add_cours:
                break;
            case R.id.nav_profile:
                Intent intent3= new Intent(getApplicationContext(),Profile.class);
                startActivity(intent3);
                break;
            case R.id.nav_timetable:
                Intent intent4 =new Intent(getApplicationContext(),Timetable.class);
                startActivity(intent4);
                break;
            case R.id.nav_add_timetable:
                Intent intent5 =new Intent(getApplicationContext(),addTimetable.class);
                startActivity(intent5);
                break;
            case R.id.nav_logout:
                mAuth.getInstance().signOut();
                finish();
                startActivity( new Intent(getApplicationContext(),Login.class));

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //this function will get the pdf from the storage
    private void getPDF() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Course"), PICK_PDF_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadFile(data.getData());
            }else{
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }


    //this method is uploading the file
    //the code is same as the previous tutorial
    //so we are not explaining it
    private void uploadFile(Uri data) {
        progressBar.setVisibility(View.VISIBLE);
        final StorageReference sRef = mStorageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
        sRef.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressBar.setVisibility(View.GONE);
                        textViewStatus.setText("File Uploaded Successfully");

                        sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(final Uri uri) {
                                UserID=mAuth.getCurrentUser().getUid();
                                DocumentReference UserDoc= db.collection("Users").document(UserID);
                                UserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> user = new HashMap<>();
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                user=document.getData();
                                                prof =user.get("FirstName")+" "+user.get("LastName");
                                                final Uri downloadUrl = uri;
                                                Map<String, Object> cours = new HashMap<>();
                                                String subject=editTextSubject.getText().toString();
                                                String course=editTextCourse.getText().toString();
                                                cours.put(" Date",dateObject);
                                                cours.put("Matiere",subject);
                                                Log.e(TAG, "tlata " + prof);
                                                cours.put("Prof",prof);
                                                cours.put("nom",course);
                                                cours.put("url",downloadUrl.toString());
                                                db.collection("Courses").add(cours).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Toast.makeText(getApplicationContext(), "Success",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getApplicationContext(), "Failed",
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }
                            });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @SuppressWarnings("VisibleForTests")
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        textViewStatus.setText((int) progress + "% Uploading...");
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.buttonUploadFile:
                getPDF();
                break;
        }
    }
}
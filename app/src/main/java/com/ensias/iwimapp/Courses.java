package com.ensias.iwimapp;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Courses extends Activity implements NavigationView.OnNavigationItemSelectedListener{
    private FirebaseAuth mAuth;
    DrawerLayout drawer;
    NavigationView nav;
    Toolbar navIcon;
    androidx.appcompat.widget.Toolbar t;
    String UserID;
    //the listview
    ListView listView;

    FirebaseFirestore db ;

    //list to store uploads data
    List<Map> Courses;
    List<RowItem> rowItems;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        mAuth = FirebaseAuth.getInstance();
        rowItems = new ArrayList<RowItem>();
        db = FirebaseFirestore.getInstance();
        Courses = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        drawer= findViewById(R.id.drawer);
        nav= findViewById(R.id.nav);
        navIcon= findViewById(R.id.toolbar);
        nav.bringToFront();
        setActionBar(navIcon);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,R.string.open_nav,R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);





        UserID = mAuth.getCurrentUser().getUid();
        DocumentReference UserDoc = db.collection("Users").document(UserID);
        UserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> user = new HashMap<>();
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        user = document.getData();
                        String type = (String) user.get("Type");
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
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });















        //adding a clicklistener on listview
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                Map cours1 = Courses.get(i);

                //Opening the upload file in browser using the upload url
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String)cours1.get("url")));
                startActivity(browserIntent);
            }
        });

        db.collection("Courses").orderBy("Date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Map<String, Object> cours = new HashMap<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                cours=new HashMap<>();
                                cours=document.getData();
                                Courses.add(cours);

                            }
                            Log.e("cour1", "1 " + Courses.toString());
                            for (int i = 0; i < Courses.size(); i++) {
                                RowItem item = new RowItem((String) Courses.get(i).get("nom"),(String) Courses.get(i).get("Prof"),(String) Courses.get(i).get("Matiere"), R.drawable.book);
                                rowItems.add(item);
                            }

                            CourseAdapter adapter = new CourseAdapter(Courses.this,R.layout.item, rowItems);
                            listView.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    @Override
    protected  void onResume(){
        super.onResume();
        nav= findViewById(R.id.nav);
        nav.setCheckedItem(R.id.nav_course);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_course:
                break;
            case R.id.nav_home:
                Intent intent5 =new Intent(getApplicationContext(),Home.class);
                startActivity(intent5);
                break;

            case R.id.nav_add_cours:
                Intent intent= new Intent(getApplicationContext(),addCourse.class);
                startActivity(intent);
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
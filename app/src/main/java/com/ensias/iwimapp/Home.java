package com.ensias.iwimapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Home extends Activity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    NavigationView nav;
    Toolbar navIcon;
    String UserID;
    FirebaseFirestore db ;
    androidx.appcompat.widget.Toolbar t;
    ListView listView;
    //list to store uploads data
    List<Map> Courses;
    List<RowItem> rowItems;
    List<Map> Timetables;
    Typeface iconFont;
    private FirebaseAuth mAuth;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        iconFont = FontAwesome.getTypeface(getApplicationContext(), FontAwesome.FONTAWESOME);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        drawer = findViewById(R.id.drawer);
        rowItems = new ArrayList<RowItem>();
        nav = findViewById(R.id.nav);
        navIcon = findViewById(R.id.toolbar);
        Courses = new ArrayList<>();
        Timetables = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        nav.bringToFront();
        setActionBar(navIcon);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, R.string.open_nav, R.string.close_nav);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav.setNavigationItemSelectedListener(this);
        nav.setCheckedItem(R.id.nav_home);


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
                        } else if (type.equals("Teacher")) {
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting the upload
                Map cours1 = Courses.get(i);

                //Opening the upload file in browser using the upload url
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse((String) cours1.get("url")));
                startActivity(browserIntent);
            }
        });

        db.collection("Courses").orderBy("Date", Query.Direction.DESCENDING).limit(2)
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
                            db.collection("TimeTables").orderBy("Week Date", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            Map<String, Object> timetable = new HashMap<>();
                                            if (task.isSuccessful()) {

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    timetable=new HashMap<>();
                                                    timetable=document.getData();
                                                    Timetables.add(timetable);

                                                }
                                                Log.e("cour1", "1 " + Courses.toString());
                                                Log.e("cour1", "1 " + Timetables.toString());
                                                String date;
                                                String p;
                                                String s;
                                                String w;
                                                for (int i = 0; i < 3; i++) {
                                                    if(i<2) {
                                                        RowItem item = new RowItem((String) Courses.get(i).get("nom"), (String) Courses.get(i).get("Prof"), (String) Courses.get(i).get("Matiere"), R.drawable.book);
                                                        rowItems.add(item);
                                                    }
                                                    else{
                                                        w=String.valueOf(Timetables.get(i).get("Week Number"));
                                                        date=String.valueOf(Timetables.get(i).get("Week Date"));
                                                        date=date.substring(0, 10);
                                                        p=String.valueOf(Timetables.get(i).get("Period"));
                                                        s=String.valueOf(Timetables.get(i).get("Semester"));
                                                        RowItem item = new RowItem("Week "+w,"Semester :"+s+" Period: "+p,"Date :"+date, R.drawable.calendar);
                                                        rowItems.add(item);
                                                    }
                                                }

                                                CourseAdapter adapter = new CourseAdapter(Home.this,R.layout.item, rowItems);
                                                listView.setAdapter(adapter);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });
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
        nav.setCheckedItem(R.id.nav_home);
   }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_course:
                Intent intent= new Intent(getApplicationContext(),Courses.class);
                startActivity(intent);
                break;
            case R.id.nav_home:
                break;

            case R.id.nav_add_cours:
                Intent intent2= new Intent(getApplicationContext(),addCourse.class);
                startActivity(intent2);
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
            case R.id.nav_chat:
                Intent intent6 =new Intent(getApplicationContext(),Messaging.class);
                startActivity(intent6);
                break;
            case R.id.nav_logout:
                mAuth.getInstance().signOut();
                finish();
                startActivity( new Intent(getApplicationContext(),Login.class));

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

package com.example.impetrosysdev.Activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.impetrosysdev.Adapter.MyAdapter;
import com.example.impetrosysdev.R;
import com.example.impetrosysdev.model.Userdata;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class UsersList extends AppCompatActivity {

    RecyclerView recyclerView;
    DatabaseReference database;
    MyAdapter myAdapter;
    ArrayList<Userdata> list;
    FirebaseFirestore db;
    // List to store User objects
    ArrayList<Userdata> userList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userslist);

        recyclerView = findViewById(R.id.userList);
        database = FirebaseDatabase.getInstance().getReference("user");
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        // Check for static data
        //userList.add(new Userdata("kom", "jik", "kom12@gmail.com", "45465464"));
        getdata( );


    }

    private void getdata() {
        db = FirebaseFirestore.getInstance();
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            // Iterate through the documents
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Convert each document to a User object
                                Log.d("__data", "___user " + document.getData());
                                userList.add(new Userdata(document.getString("firstname"),
                                        document.getString("lastname"),
                                        document.getString("email"),
                                        document.getString("mobile")));
                            }
                            Toast.makeText(UsersList.this, "Users " + userList.size(), Toast.LENGTH_LONG).show();
                            myAdapter = new MyAdapter(userList, UsersList.this);
                            recyclerView.setAdapter(myAdapter);
                            // Now, userList contains all User objects from the collection
                            for (Userdata user : userList) {
                                Log.d(TAG, "User: " + user.toString());
                            }
                        } else {
                            // Handle errors
                            Log.e(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }
}
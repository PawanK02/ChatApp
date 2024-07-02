package com.example.chatapp;

import static android.Manifest.permission.CALL_PHONE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionScene;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat extends AppCompatActivity {
    ImageView back;
    TextView phtxt;
    TextView chattxt;
    ImageButton call;
    EditText edtchat;
    RecyclerView rvchat;
    FloatingActionButton fab;
    String userName,othername,phoneno;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    MsgAdapter adp;
    List<Model> list;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        back = (ImageView) findViewById(R.id.back);
        chattxt = (TextView) findViewById(R.id.txtchat);
        phtxt = (TextView) findViewById(R.id.phtxt);
        edtchat = (EditText)findViewById(R.id.chatedt);
        rvchat = (RecyclerView) findViewById(R.id.rvchat);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        call = (ImageButton)findViewById(R.id.call);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = database.getReference();
        phoneno = getIntent().getStringExtra("phoneno");
        userName = getIntent().getStringExtra("userName");
        othername = getIntent().getStringExtra("othername");

        chattxt.setText(othername);
        rvchat.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chat.this,MainActivity.class);
                startActivity(intent);
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                      if(ContextCompat.checkSelfPermission(Chat.this,android.Manifest.permission.CALL_PHONE) !=
                      PackageManager.PERMISSION_GRANTED){
                          ActivityCompat.requestPermissions(Chat.this,new String[]{android.Manifest.permission.CALL_PHONE},100);
                      }
                      else{
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + phoneno));
                            startActivity(callIntent);
                        }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = edtchat.getText().toString();
                if(!msg.equals("")){
                    sendmessage(msg);
                    edtchat.setText("");
                }
            }
        });
        getMessage();
    }
    public void sendmessage(String msg){
        String key = reference.child("Messages").child(userName).child(othername).push().getKey();
        Map<String,Object> messageMap = new HashMap<>();
        messageMap.put("message",msg);
        messageMap.put("from",userName);
        reference.child("Messages").child(userName).child(othername).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(othername).child(userName).child(key).setValue(messageMap);
                }
            }
        });
    }
    public void getMessage(){
        reference.child("Messages").child(userName).child(othername).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Model modelClass = snapshot.getValue(Model.class);
                list.add(modelClass);
                adp.notifyDataSetChanged();
                rvchat.scrollToPosition(list.size()-1);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adp = new MsgAdapter(list,userName);
        rvchat.setAdapter(adp);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Chat.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
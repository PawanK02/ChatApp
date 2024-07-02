package com.example.chatapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class Image2 extends AppCompatActivity {
    ImageView dp2;
    ImageButton close2;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth auth;
    DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image2);
        dp2 = (ImageView) findViewById(R.id.dp2);
        close2 = (ImageButton) findViewById(R.id.close2);
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        reference = database.getReference();
        user = auth.getCurrentUser();
        String image = getIntent().getStringExtra("imagedp");
        Picasso.get().load(image).into(dp2);
        close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Image2.this,ProfileAct.class);
                startActivity(intent);

            }
        });

    }
    public void onBackPressed() {
        Intent intent = new Intent(Image2.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
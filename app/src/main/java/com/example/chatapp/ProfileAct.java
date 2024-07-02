package com.example.chatapp;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileAct extends AppCompatActivity {
    private CircleImageView img;
    private EditText edt;
    Button btn,prof;
    FirebaseAuth auth;
    TextView txt2;
    FirebaseDatabase db;
    DatabaseReference reference;
    FirebaseUser user;
    FirebaseStorage storage;
    StorageReference stref;
    String image;
    boolean imagectrl= false;
    Uri imageUri;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        img = (CircleImageView) findViewById(R.id.img1);
        txt2 = (TextView) findViewById(R.id.txt2);
        edt = (EditText) findViewById(R.id.name1);
        btn = (Button) findViewById(R.id.update);
        db = FirebaseDatabase.getInstance();
        prof = (Button) findViewById(R.id.prof);
        reference = db.getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        stref = storage.getReference();
        getuserinfo();
        String imagedp;

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagechooser();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateprofile();
            }
        });
    }

    public void getuserinfo(){
        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("Username").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                txt2.setText(name);
                edt.setText(name);
                if (image.equals("null")) {
                    img.setImageResource(R.drawable.images);
                } else {
                    Picasso.get().load(image).into(img);
                }
                prof.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(ProfileAct.this,Image2.class);
                        intent.putExtra("imagedp",image);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null){
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(img);
            imagectrl=true;
        }
        else{
            imagectrl=false;
        }
    }
    public void updateprofile() {
        String username = edt.getText().toString();
        reference.child("Users").child(user.getUid()).child("Username").setValue(username);
        if(imagectrl){
            UUID randomid = UUID.randomUUID();
            String imagename  = "images/"+randomid+".jpg";
            stref.child(imagename).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    StorageReference mystr = storage.getReference(imagename);
                    mystr.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String filepath = uri.toString();
                            reference.child("Users").child(auth.getUid()).child("image").setValue(filepath).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileAct.this, "Write to database is successful", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ProfileAct.this, "Write to database is not successful", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
            });

        }
        else {
            reference.child("Users").child(auth.getUid()).child("image").setValue(image);
        }

        Intent intent = new Intent(ProfileAct.this,MainActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);

    }

    public void imagechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ProfileAct.this,MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }
}
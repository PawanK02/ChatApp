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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class signup extends AppCompatActivity {
    Button signin;
    TextView txt;
    EditText name,mail,password;
    CircleImageView img;
    FirebaseAuth auth;
    boolean imagectrl = false;
    FirebaseDatabase db;
    FirebaseStorage storage;
    StorageReference stref;
    Uri imageUri;
    EditText phno;
    DatabaseReference reference;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        name = (EditText) findViewById(R.id.name1);
        password = (EditText) findViewById(R.id.password);
        mail = (EditText) findViewById(R.id.email);
        signin = (Button) findViewById(R.id.signin);
        img = (CircleImageView)findViewById(R.id.img1);
        auth = FirebaseAuth.getInstance();
        phno = (EditText)findViewById(R.id.phno);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference();
        storage = FirebaseStorage.getInstance();
        stref = storage.getReference();
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagechooser();
            }
        });
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = name.getText().toString();
                String email = mail.getText().toString();
                String pass = password.getText().toString();
                String phone = phno.getText().toString();
                if(!username.equals("") && !email.equals("") && !pass.equals("") && !phone.equals("")) {
                    SignUp(username, email, pass,phone);
                }
            }
        });
    }
    public void imagechooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
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
    public void SignUp(final String Username,String email,String pass,String phone){
        auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    reference.child("Users").child(auth.getUid()).child("Username").setValue(Username);
                  reference.child("Users").child(auth.getUid()).child("Phone").setValue(phone);
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
                                                Toast.makeText(signup.this, "Write to database is successful", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(signup.this, "Write to database is not successful", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });

                    }
                    else {
                        reference.child("Users").child(auth.getUid()).child("image").setValue("null");
                    }
                    Intent intent = new Intent(signup.this,MainActivity.class);
                    startActivity(intent);

                }
                else{
                    Toast.makeText(signup.this,"Some error occurred",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



}
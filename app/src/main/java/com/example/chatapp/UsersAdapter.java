package com.example.chatapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewHolder>{
    List<String> userList;
    String userName;
    Context mcontext;
    FirebaseDatabase database;
    DatabaseReference reference;

    public UsersAdapter(List<String> userList, String userName, Context mcontext) {
        this.userList = userList;
        this.userName = userName;
        this.mcontext = mcontext;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view,parent,false);
                 return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {

        reference.child("Users").child(userList.get(position)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String othername = snapshot.child("Username").getValue().toString();
                String phoneno =   snapshot.child("Phone").getValue().toString();

                String imageURL = snapshot.child("image").getValue().toString();
                holder.nameuser.setText(othername);
                if(imageURL.equals("null")){
                    holder.nameimage.setImageResource(R.drawable.images);
                }
                else{
                    Picasso.get().load(imageURL).into(holder.nameimage);
                }
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mcontext,Chat.class);
                        intent.putExtra("othername",othername);
                        intent.putExtra("userName",userName);
                        intent.putExtra("phoneno",phoneno);
                        mcontext.startActivity(intent);
                    }
                });
                holder.nameimage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mcontext,Image.class);
                        intent.putExtra("dp",imageURL);
                        mcontext.startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return userList.size();
    }
    public class viewHolder extends RecyclerView.ViewHolder {
      TextView nameuser;
         CircleImageView nameimage;
   CardView cardView;

        @SuppressLint("WrongViewCast")
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            nameuser = (TextView)itemView.findViewById(R.id.username);
            nameimage = (CircleImageView) itemView.findViewById(R.id.userimage);
            cardView = (CardView)itemView.findViewById(R.id.cardview);
        }
    }
}

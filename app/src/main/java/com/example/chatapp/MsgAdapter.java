package com.example.chatapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MsgAdapter  extends RecyclerView.Adapter<MsgAdapter.MsgViewHolder>{
    List<Model> list;
    String username;
boolean status;
int send;
int receive;
    public MsgAdapter(List<Model> list, String username) {
        this.list = list;
        this.username = username;
        status = false;
        send = 1;
        receive = 2;
    }

    @NonNull
    @Override
    public MsgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
                if(viewType==send){
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
                }
                else{
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_recieved,parent,false);
                }
        return new MsgViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MsgViewHolder holder, int position) {
holder.txtx.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MsgViewHolder extends RecyclerView.ViewHolder{
TextView txtx;
        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            if(status){
                txtx = (TextView)itemView.findViewById(R.id.txtsend);
            }
            else{
                txtx = (TextView)itemView.findViewById(R.id.txtreceive);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getFrom().equals(username))
        {
            status = true;
            return send;
        }
        else {
            status = false;
            return receive;
        }
    }
}

package com.example.dibuca;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder>{
    private ArrayList<user> list;
    private iOnClickItemUser iOnClickItemUser;
    private String id;
    public UserAdapter(ArrayList<user> list,iOnClickItemUser iOnClickItemUser,String id){
        this.list = list;
        this.iOnClickItemUser = iOnClickItemUser;
        this.id = id;
    }
    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign,parent,false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.UserViewHolder holder, int position) {
        user user = list.get(position);
        if(user == null) return;
        holder.nametxt.setText(user.getTen());
        holder.jobtxt.setText(user.getTen());
        holder.item_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOnClickItemUser.onClickItemUser(user.getId());
            }
        });
        holder.btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               iOnClickItemUser.onClickCall(user.getSdt());
            }
        });
        holder.btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOnClickItemUser.onClickDelUser(id,user.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (list != null){
            return list.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView nametxt, jobtxt;
        private RelativeLayout item_user;
        private FloatingActionButton btndel, btncall;
        public UserViewHolder(@NonNull View itemView){
            super(itemView);
            nametxt = itemView.findViewById(R.id.ten);
            jobtxt = itemView.findViewById(R.id.cty_cv);
            item_user = itemView.findViewById(R.id.item_user);
            btncall = itemView.findViewById(R.id.btncall);
            btndel = itemView.findViewById(R.id.btndel);
        }
    }
}

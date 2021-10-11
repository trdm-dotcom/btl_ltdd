package com.example.dibuca;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private String id = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        id = getArguments().getString("ID");
        TextView viewTen = view.findViewById(R.id.ten);
        TextView viewCty = view.findViewById(R.id.cty);
        TextView viewCv = view.findViewById(R.id.cv);
        TextView viewDt = view.findViewById(R.id.dienthoai);
        TextView viewEmail = view.findViewById(R.id.email);
        Button btnEdit = view.findViewById(R.id.btnedit);
        Button btnChange = view.findViewById(R.id.btnchangemail);
        btnEdit.setOnClickListener(this);
        btnChange.setOnClickListener(this);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user userProfile = task.getResult().getValue(user.class);
                if(userProfile != null){
                    viewTen.setText(userProfile.getTen());
                    if(userProfile.getCongty() != null){
                        viewCty.setText(userProfile.getCongty());
                    }
                    if(userProfile.getCongviec() != null){
                        viewCv.setText(userProfile.getCongviec());
                    }
                    viewDt.setText(userProfile.getSdt());
                    viewEmail.setText(userProfile.getEmail());
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnedit:{
                Fragment fragment = new EditProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ID",id);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,fragment).commit();
                break;
            }
            case R.id.btnchangemail:{
                Fragment fragment = new UpdateEmailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ID",id);
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container,fragment).commit();
                break;
            }
            default: break;
        }
    }
}
package com.example.dibuca;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileScanFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_scan, container, false);
        String id = getArguments().getString("ID");
        String uid = getArguments().getString("UID");
        TextView viewTen = view.findViewById(R.id.ten);
        TextView viewCty = view.findViewById(R.id.cty);
        TextView viewCv = view.findViewById(R.id.cv);
        TextView viewDt = view.findViewById(R.id.dienthoai);
        TextView viewEmail = view.findViewById(R.id.email);
        Button btnSave = view.findViewById(R.id.btnesave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String>storageid=new HashMap<String, String>();
                storageid.put(id,id);
                FirebaseDatabase.getInstance().getReference().child("contact").child(uid).setValue(storageid)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()){
                            ListUserFragment fragment = new ListUserFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("ID",uid);
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container,fragment).commit();
                        }
                        else{
                            Toast.makeText(getContext(),"Lưu thất bại! Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
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
                else{
                    Toast.makeText(getContext(),"Không tìm thấy! Vui lòng quét lại",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
}
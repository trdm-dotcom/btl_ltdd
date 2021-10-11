package com.example.dibuca;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileFragment extends Fragment {
    private EditText edtTen, edtCty, edtCv, edtSdt;
    private user userProfile;
    private DatabaseReference databaseReference;
    private ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        String id = getArguments().getString("ID");
        edtTen = view.findViewById(R.id.hoten);
        edtCty = view.findViewById(R.id.congty);
        edtCv = view.findViewById(R.id.congviec);
        edtSdt = view.findViewById(R.id.dienthoai);
        Button btnCapnhap = view.findViewById(R.id.capnhap);
        btnCapnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capnhap(id);
            }
        });
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                userProfile = snapshot.getValue(user.class);
                if(userProfile != null){
                    edtTen.setText(userProfile.getTen());
                    if(userProfile.getCongty() != null){
                        edtCty.setText(userProfile.getCongty());
                    }
                    if(userProfile.getCongviec() != null){
                        edtCv.setText(userProfile.getCongviec());
                    }
                    edtSdt.setText(userProfile.getSdt());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("errorfirebase", "loadPost:onCancelled", error.toException());
            }
        });
        return view;
    }
    private void capnhap(String id){
        String hoten = edtTen.getText().toString();
        String cty = edtCty.getText().toString();
        String cv = edtCv.getText().toString();
        String sdt = edtSdt.getText().toString();
        if(hoten.isEmpty() || cty.isEmpty() || cv.isEmpty() || sdt.isEmpty()){
            edtTen.setText(userProfile.getTen());
            edtCty.setText(userProfile.getCongty());
            edtCv.setText(userProfile.getCongviec());
            edtSdt.setText(userProfile.getSdt());
            Toast.makeText(getActivity(),"Vui lòng nhật đủ các trường thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        pd = new ProgressDialog(getActivity());
        pd.show();
        HashMap userupdate = new HashMap();
        userupdate.put("congty",cty);
        userupdate.put("congviec",cv);
        userupdate.put("sdt",sdt);
        userupdate.put("ten",hoten);
        databaseReference.child(id).updateChildren(userupdate).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(Task task) {
                if(task.isSuccessful()){
                    pd.dismiss();
                    userProfile.setSdt(sdt);
                    userProfile.setCongviec(cv);
                    userProfile.setCongty(cty);
                    userProfile.setTen(hoten);
                    Toast.makeText(getActivity(),"Cập nhập thành công", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
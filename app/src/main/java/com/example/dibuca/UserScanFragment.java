package com.example.dibuca;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserScanFragment extends Fragment implements View.OnClickListener {
    private String email, sdt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_scan, container, false);
        String id = getArguments().getString("ID");
        TextView viewTen = view.findViewById(R.id.ten);
        TextView viewCty = view.findViewById(R.id.cty);
        TextView viewCv = view.findViewById(R.id.cv);
        TextView viewDt = view.findViewById(R.id.dienthoai);
        TextView viewEmail = view.findViewById(R.id.email);
        view.findViewById(R.id.btnemail).setOnClickListener(this);
        view.findViewById(R.id.btnsms).setOnClickListener(this);
        view.findViewById(R.id.btnphone).setOnClickListener(this);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user userProfile = task.getResult().getValue(user.class);
                if(userProfile != null){
                    email = userProfile.getEmail();
                    sdt = userProfile.getSdt();
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
                    Toast.makeText(getContext(),"Không tìm thấy",Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnemail:{
                email();
                break;
            }
            case R.id.btnsms:{
                sms();
                break;
            }
            case R.id.btnphone:{
                phone();
                break;
            }
            default:{
                break;
            }
        }
    }
    private void email(){
        //su dung intent khong tuong minh de thuc hien gui email
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.setData(Uri.parse("mailto:"));
        //start activity de thuc hien gui email
        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
            getContext().startActivity(intent);
        }
        else if(email.isEmpty()){
            Toast.makeText(getContext(),"Email không có",Toast.LENGTH_LONG).show();
            return;
        }
        else {
            Toast.makeText(getContext(), "Không ứng dụng hỗ trợ gửi mail",
                    Toast.LENGTH_SHORT).show();
        }

    }
    private void sms(){
        if(sdt.isEmpty()){
            Toast.makeText(getContext(),"Số điện thoại không có",Toast.LENGTH_LONG).show();
            return;
        }
        //su dung intent khong tuong minh de thuc hien nhan tin
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + sdt));
        //start activity de thuc hien nhan tin
        getContext().startActivity(intent);
    }
    private void phone(){
        if(sdt.isEmpty()){
            Toast.makeText(getContext(),"Số điện thoại không có",Toast.LENGTH_LONG).show();
            return;
        }
        //su dung intent khong tuong minh de thuc hien cuoc goi
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + sdt));
        //start activity de thuc hien cuoc goi
        getContext().startActivity(intent);
    }
}
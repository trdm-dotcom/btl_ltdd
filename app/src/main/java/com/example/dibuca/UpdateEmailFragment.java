package com.example.dibuca;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateEmailFragment extends Fragment {
    private EditText edtMail;
    private boolean check;
    private ProgressDialog pd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_email, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments().getString("ID");
        edtMail = view.findViewById(R.id.email);
        Button btnCapnhap = view.findViewById(R.id.capnhap);
        btnCapnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capnhap(id);
            }
        });
    }
    private boolean checkAlreadyExists(String email){
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0){
                    check = true;
                }else {
                    check = false;
                }
            }
        });
        return check;
    }
    private void capnhap(String id){
        String mail = edtMail.getText().toString();
        if(mail.isEmpty()){
            edtMail.setError("Vui lòng trường thông tin");
            edtMail.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(mail).matches()){
            edtMail.setError("Email không hợp lệ");
            edtMail.requestFocus();
            return;
        }
        else if(!checkAlreadyExists(mail)){
            edtMail.setError("Email đã được sử dụng");
            edtMail.requestFocus();
            return;
        }
        else{
            pd = new ProgressDialog(getActivity());
            pd.show();
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                FirebaseAuth.getInstance().getCurrentUser().updateEmail(mail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> taskfa) {
                        if (taskfa.isSuccessful()) {
                            HashMap userupdate = new HashMap();
                            userupdate.put("email",mail);
                            FirebaseDatabase.getInstance().getReference("users").child(id).updateChildren(userupdate).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task taskfb) {
                                    pd.dismiss();
                                    if(taskfb.isSuccessful()){
                                        Toast.makeText(getContext(),"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(getContext(),"Cập nhật thất bại",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else{
                HashMap userupdate = new HashMap();
                userupdate.put("email",mail);
                FirebaseDatabase.getInstance().getReference("users").child(id).updateChildren(userupdate).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task taskfb) {
                        pd.dismiss();
                        if(taskfb.isSuccessful()){
                            Toast.makeText(getContext(),"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
package com.example.dibuca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText edtTen, edtEmail, edtSdt, edtMk;
    private FirebaseAuth mAuth;
    private String stoEmail, stoMk;
    private boolean check;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findViewById(R.id.btndk).setOnClickListener(this);
        findViewById(R.id.btndn).setOnClickListener(this);
        getView();
        mAuth = FirebaseAuth.getInstance();
    }
    private void getView(){
        edtTen      = findViewById(R.id.ten);
        edtEmail    = findViewById(R.id.email);
        edtSdt      = findViewById(R.id.dienthoai);
        edtMk       = findViewById(R.id.matkhau);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndk:{
                register();
                break;
            }
            case R.id.btndn:{
                Intent intent = new Intent(this,LoginActivity.class);
                intent.putExtra("email",stoEmail);
                startActivity(intent);
                finish();
                break;
            }
            default: break;
        }
    }
    private boolean checkAlreadyExistss(String email){
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0){
                    check = true;
                }else {
                    check = false;
                }
            }
        });
        return check;
    }
    private void register(){
        String ten      = edtTen.getText().toString().trim();
        String email    = edtEmail.getText().toString().trim();
        String sdt      = edtSdt.getText().toString().trim();
        String matkhau  = edtMk.getText().toString().trim();
        if(ten.isEmpty()){
            edtTen.setError("Nhập họ tên");
            edtTen.requestFocus();
            return;
        }
        if(email.isEmpty()){
            edtEmail.setError("Nhập email");
            edtEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if(sdt.isEmpty()){
            edtSdt.setError("Nhập số điện thoại");
            edtSdt.requestFocus();
            return;
        }
        if(matkhau.isEmpty()){
            edtMk.setError("Nhập mật khẩu");
            edtMk.requestFocus();
            return;
        }
        if(matkhau.length()<6){
            edtMk.setError("Mật khẩu tối thiểu 6 ký tự");
            edtMk.requestFocus();
            return;
        }
        if(!checkAlreadyExistss(email)){
            edtEmail.setError("Email đã được sử dụng");
            edtEmail.requestFocus();
            return;
        }
        pd = new ProgressDialog(RegisterActivity.this);
        pd.show();
        mAuth.createUserWithEmailAndPassword(email,matkhau).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                pd.dismiss();
                if (task.isSuccessful()){
                    String id = FirebaseAuth.getInstance().getUid();
                    user user = new user(ten,email,sdt,null,null,id);
                    FirebaseDatabase.getInstance().getReference("users")
                            .child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if(task.isSuccessful()){
                                stoEmail = email;
                                stoMk = matkhau;
                                Toast.makeText(RegisterActivity.this,"Đăng ký thành công",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(RegisterActivity.this,"Đăng ký thất bại! Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(RegisterActivity.this,"Email đã được sử dụng! Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Đăng ký thất bại! Vui lòng thử lại",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
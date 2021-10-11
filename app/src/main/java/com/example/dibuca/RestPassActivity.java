package com.example.dibuca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class RestPassActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText edtMail;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rest_pass);
        edtMail = findViewById(R.id.emailresetpass);
        findViewById(R.id.btnnext).setOnClickListener(this);
        findViewById(R.id.btnback).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnback:{
                Intent intent = new Intent(RestPassActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btnnext:{
                String email = edtMail.getText().toString().trim();
                pd = new ProgressDialog(RestPassActivity.this);
                pd.show();
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pd.dismiss();
                        if (task.isSuccessful()){
                            setContentView(R.layout.success_layout);
                        }
                        else{
                            Toast.makeText(RestPassActivity.this,"Email không tồn tại!",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            }
            default: break;
        }
    }
}
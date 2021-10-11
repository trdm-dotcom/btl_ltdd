package com.example.dibuca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

import java.util.Date;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private AccountAuthService mAuthManager;
    private AccountAuthParams mAuthParam;
    private FirebaseAuth mAuth;
    private EditText edtEmail, edtMk;
    private CheckBox cbSave;
    private static final String TAG = "LoginActivity";
    private ProgressDialog pd;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.btndn).setOnClickListener(this);
        findViewById(R.id.btndk).setOnClickListener(this);
        findViewById(R.id.huaweiID).setOnClickListener(this);
        findViewById(R.id.btnpass).setOnClickListener(this);
        edtEmail = findViewById(R.id.email);
        edtMk = findViewById(R.id.matkhau);
        edtEmail.setText(getIntent().getStringExtra("email"));
        cbSave = findViewById(R.id.save);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btndk:{
                Intent intent = new Intent(this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            case R.id.btndn:{
                login();
                break;
            }
            case  R.id.huaweiID:{
                silentSignIn();
                break;
            }
            case R.id.btnpass:{
                Intent intent = new Intent(this, RestPassActivity.class);
                startActivity(intent);
                finish();
                break;
            }
            default: break;
        }
    }
    private void login(){
        String email = edtEmail.getText().toString().trim();
        String matkhau = edtMk.getText().toString().trim();
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
        if(matkhau.isEmpty()){
            edtMk.setError("Nhập mật khẩu");
            edtMk.requestFocus();
            return;
        }
        pd = new ProgressDialog(LoginActivity.this);
        pd.show();
        mAuth.signInWithEmailAndPassword(email,matkhau).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(com.google.android.gms.tasks.Task<AuthResult> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    // duy tri dang nhap
                    if(cbSave.isChecked()){
                        SharedPreferences sharedPreferences = getSharedPreferences("member",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("eUser",email);
                        editor.putString("pUser",matkhau);
                        editor.apply();
                    }
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("ID",user.getUid());
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(LoginActivity.this,"Email hoặc mặt khẩu không đúng",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void huaweiId() {
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setIdToken()
                .setAccessToken()
                .createParams();
        mAuthManager = AccountAuthManager.getService(this, mAuthParam);
        startActivityForResult(mAuthManager.getSignInIntent(), 8888);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 8888) {
            //get user message by parseAuthResultFromIntent
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                AuthAccount authAccount = authAccountTask.getResult();
                String id  = authAccount.getOpenId();
                //select * from users where id  = ?
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.getValue() == null){
                            Log.i(TAG,"id: " +id);
                            user user = new user(authAccount.getDisplayName(),authAccount.getEmail(),null,null,null, id);
                            FirebaseDatabase.getInstance().getReference("users").child(id).setValue(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ID",id);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ID",id);
                            startActivity(intent);
                            finish();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.w("errorfirebase", "loadPost:onCancelled", error.toException());
                    }
                });
            } else {
                Log.i(TAG, "signIn failed: " + ((ApiException) authAccountTask.getException()).getStatusCode());
            }
        }
    }
    private void silentSignIn() {
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
        mAuthManager = AccountAuthManager.getService(LoginActivity.this, mAuthParam);
        Task<AuthAccount> task = mAuthManager.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                String id  = authAccount.getOpenId();
                //select * from users where id  = ?
                databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
                databaseReference.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.getValue()==null){
                            user user = new user(authAccount.getDisplayName(),authAccount.getEmail(),null,null,null, id);
                            FirebaseDatabase.getInstance().getReference("users").child(id).setValue(user);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ID",id);
                            startActivity(intent);
                            finish();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("ID",id);
                            startActivity(intent);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                //if Failed use getSignInIntent
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    huaweiId();
                }
            }
        });
    }
}
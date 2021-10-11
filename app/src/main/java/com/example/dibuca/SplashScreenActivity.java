package com.example.dibuca;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.WindowManager;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;

public class SplashScreenActivity extends AppCompatActivity {
    private Animation topanim,bottomanim;
    private TextView textView;
    private ImageView logo;
    private static final String TAG = "SplashScreenActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splashscreen);
        // animation
        topanim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomanim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        textView = findViewById(R.id.textView);
        logo = findViewById(R.id.logo);
        logo.setAnimation(topanim);
        textView.setAnimation(bottomanim);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AccountAuthParams mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM).createParams();
                AccountAuthService mAuthManager = AccountAuthManager.getService(SplashScreenActivity.this, mAuthParam);
                Task<AuthAccount> task = mAuthManager.silentSignIn();
                task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
                    @Override
                    public void onSuccess(AuthAccount authAccount) {
                        Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                        intent.putExtra("ID",authAccount.getOpenId());
                        startActivity(intent);
                    }
                });
                task.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        //if Failed use getSignInIntent
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            SharedPreferences sharedPreferences = getSharedPreferences("member",MODE_PRIVATE);
                            String eUser = sharedPreferences.getString("eUser","");
                            String pUser = sharedPreferences.getString("pUser","");
                            if(!eUser.isEmpty() && !pUser.isEmpty()){
                                FirebaseAuth.getInstance().signInWithEmailAndPassword(eUser,pUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                            intent.putExtra("ID",user.getUid());
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            }
                            else{
                                Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                });
            }
        }, 3000);
    }
}
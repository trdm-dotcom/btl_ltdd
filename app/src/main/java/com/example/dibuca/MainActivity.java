package com.example.dibuca;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        id = getIntent().getStringExtra("ID");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnav);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Fragment fragmentDefault = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID",id);
        fragmentDefault.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,fragmentDefault).commit();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment fragment=null;
            switch (item.getItemId()){
                case R.id.profile:{
                    fragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID",id);
                    fragment.setArguments(bundle);
                    break;
                }
                case R.id.qr:{
                    fragment = new QRFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID",id);
                    fragment.setArguments(bundle);
                    break;
                }
                case R.id.list:{
                    fragment = new ListUserFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID",id);
                    fragment.setArguments(bundle);
                    break;
                }
                case R.id.logout:{
                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                    alert.setTitle("Xác nhận")
                    .setMessage("Bạn muốn đăng xuất ?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences sharedPreferences = getSharedPreferences("member",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove("eUser");
                                    editor.remove("pUser");
                                    editor.apply();
                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            }).show();
                    return true;
                }
                default:{
                    fragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("ID",id);
                    fragment.setArguments(bundle);
                    break;
                }
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            return true;
        }
    };
}
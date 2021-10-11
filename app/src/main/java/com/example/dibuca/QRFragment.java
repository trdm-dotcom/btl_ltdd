package com.example.dibuca;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.huawei.hms.ml.scan.HmsScan;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QRFragment extends Fragment {
    TextView viewTen, viewCty_cv;
    ImageView viewQr;
    Button btnScan;
    private String uid;
    public static final int DEFINED_CODE = 222;
    private static final int REQUEST_CODE_SCAN = 0X01;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_q_r, container, false);
        uid = getArguments().getString("ID");
        viewTen = view.findViewById(R.id.ten);
        viewCty_cv = view.findViewById(R.id.cty_cv);
        viewQr = view.findViewById(R.id.qr_image);
        btnScan = view.findViewById(R.id.btnscan);
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(uid, BarcodeFormat.QR_CODE,450,450);
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.createBitmap(bitMatrix);
            viewQr.setImageBitmap(bitmap);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                user userProfile = task.getResult().getValue(user.class);
                viewTen.setText(userProfile.getTen());
                viewCty_cv.setText(userProfile.getCongviec()+" tại "+userProfile.getCongty());
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrOperation();
            }
        });
        return view;
    }
    private void qrOperation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    DEFINED_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        else if(requestCode == DEFINED_CODE){
//            saveID("ZfUVskB4wVerVkUz37djO5Xoagj1",uid);
            startActivityForResult(new Intent(getActivity(), ScanActivity.class), REQUEST_CODE_SCAN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != -1 || data == null){
            return;
        }
        if (requestCode == REQUEST_CODE_SCAN){
            HmsScan hmsScan = data.getParcelableExtra(ScanActivity.SCAN_RESULT);
            if (hmsScan != null && !TextUtils.isEmpty(hmsScan.getOriginalValue())) {
                saveID(String.valueOf(hmsScan.getOriginalValue()),uid);
            }
        }
    }
    private void saveID(String id,String uid){
        Fragment fragment = new ProfileScanFragment();
        Bundle bundle = new Bundle();
        bundle.putString("ID",id);
        bundle.putString("UID",uid);
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container,fragment).commit();
    }
}
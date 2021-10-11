package com.example.dibuca;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class ListUserFragment extends Fragment {
    private RecyclerView recview;
    private UserAdapter adapter;
    private ArrayList<user> list = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String id = getArguments().getString("ID");
        recview=(RecyclerView)view.findViewById(R.id.listuser);
        recview.setLayoutManager(new LinearLayoutManager(getContext()));
        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_animation_left_right);
        recview.setLayoutAnimation(layoutAnimationController);
        FirebaseDatabase.getInstance().getReference("contact").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    FirebaseDatabase.getInstance().getReference("users").child(dataSnapshot.getValue(String.class)).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            user user = task.getResult().getValue(user.class);
                            list.add(user);
                            adapter = new UserAdapter(list, new iOnClickItemUser() {
                                @Override
                                public void onClickItemUser(String id) {
                                    Fragment fragment = new UserScanFragment();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("ID",id);
                                    fragment.setArguments(bundle);
                                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                    transaction.replace(R.id.fragment_container,fragment).commit();
                                }
                                public void onClickDelUser(String id, String idDel){
                                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                    alert.setTitle("Xác nhận")
                                            .setMessage("Bạn có muốn xóa không")
                                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    ProgressDialog pd = new ProgressDialog(getActivity());
                                                    pd.show();
                                                    FirebaseDatabase.getInstance().getReference().child("contact").child(id).child(idDel).removeValue();
                                                    pd.dismiss();
                                                }
                                            }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    }).show();
                                }
                                public void onClickCall(String sdt){
                                    if(sdt.isEmpty()){
                                        Toast.makeText(getContext(),"Số điện thoại không có",Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    Intent intent = new Intent(Intent.ACTION_DIAL);
                                    intent.setData(Uri.parse("tel:" + sdt));
                                    //start activity de thuc hien cuoc goi
                                    getContext().startActivity(intent);
                                }
                            },id);
                            recview.setAdapter(adapter);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("errorfirebase", "loadPost:onCancelled", error.toException());
            }
        });
    }
}
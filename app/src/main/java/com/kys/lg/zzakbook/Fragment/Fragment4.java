package com.kys.lg.zzakbook.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kys.lg.zzakbook.LoginActivity;
import com.kys.lg.zzakbook.MainActivity;
import com.kys.lg.zzakbook.R;
import com.kys.lg.zzakbook.databinding.FragmentFragment4Binding;
import com.kys.lg.zzakbook.model.AlarmDTO;
import com.kys.lg.zzakbook.model.ContentDTO;
import com.kys.lg.zzakbook.model.FollowDTO;

import java.util.ArrayList;


public class Fragment4 extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FragmentFragment4Binding binding;
    private final int PICK_PROFILE_FROM_ALBUM = 1;


    private String uid;
    private String currentuserid;

    private MainActivity mainActivity;
    Fragment4 fragment4;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity) {
            mainActivity = (MainActivity) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fragment4, container, false);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        currentuserid = auth.getCurrentUser().getUid();


        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(mainActivity, "로그아웃에 성공하였습니다.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(mainActivity, LoginActivity.class);
                    mainActivity.startActivity(intent);
                    mainActivity.finish();

                }
            }
        };

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding = FragmentFragment4Binding.bind(getView());

        if (getArguments() != null) {
            uid = getArguments().getString("destinationUid");

            //본인 계정인 경우
            if (uid != null && uid.equals(currentuserid)) {

                binding.profileSet.setText(getString(R.string.signout));
                binding.profileSet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signout();
                    }
                });
            }
        }
        //본인 계정 이 아닌 경우
        else {
            binding.profileSet.setText(getString(R.string.follow));
            binding.profileSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestFollow();
                }
            });
        }

        binding.profileAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_PROFILE_FROM_ALBUM);
            }
        });

        getProgileImage();
        getFollower();
        getFollowing();

        binding.accountRecyclerview.setLayoutManager(new GridLayoutManager(mainActivity, 3));
        binding.accountRecyclerview.setAdapter(new Fragment4RecyclerViewAdapter());


    }

    private class Fragment4RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private ArrayList<ContentDTO> contentDTOS;

        public Fragment4RecyclerViewAdapter() {
            contentDTOS = new ArrayList<>();
            databaseReference.child("images").orderByChild("uid").equalTo(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contentDTOS.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ContentDTO content = snapshot.getValue(ContentDTO.class);
                        contentDTOS.add(snapshot.getValue(ContentDTO.class));
                    }

                    binding.boardcnt.setText(String.valueOf(contentDTOS.size()));
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            int width = getResources().getDisplayMetrics().widthPixels / 3;

            ImageView imageView = new ImageView(viewGroup.getContext());
            imageView.setLayoutParams(new LinearLayoutCompat.LayoutParams(width, width));

            return new CustomViewHolder(imageView);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

            Glide.with(viewHolder.itemView.getContext())
                    .load(contentDTOS.get(i).imageUrl)
                    .apply(new RequestOptions().centerCrop())
                    .into(((CustomViewHolder) viewHolder).imageView);
        }

        @Override
        public int getItemCount() {
            return contentDTOS.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;

            public CustomViewHolder(ImageView imageView) {
                super(imageView);
                this.imageView = imageView;
            }
        }
    }

    void getProgileImage() {


        Bundle bundle= getArguments();

        uid = bundle.getString("destinationUid");

        databaseReference.child("profileImages").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String url = dataSnapshot.getValue().toString();
                            Glide.with(mainActivity)
                                    .load(url)
                                    .apply(new RequestOptions().circleCrop())
                                    .into(binding.profileAdd);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void getFollower() {

        databaseReference.child("users").child("uid")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);
                        try {

                            binding.followercnt.setText(String.valueOf(followDTO.followerCount));
                            //팔로우를 했으면
                            if (followDTO.followers.containsKey(currentuserid)) {

                                binding.profileSet.setText(getString(R.string.follow_cancel));
                                binding.profileSet.getBackground().setColorFilter(null);

                            }
                            //팔로우 안 되어 있으면
                            else {
                                if (!uid.equals(currentuserid)) {
                                    binding.profileSet.setText(getString(R.string.follow));
                                    binding.profileSet
                                            .getBackground().setColorFilter(null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    void getFollowing() {
        databaseReference.child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        FollowDTO followDTO = dataSnapshot.getValue(FollowDTO.class);
                        try {
                            binding.followingcnt.setText(String.valueOf(followDTO.followingCount));

                        } catch (Exception e) {

                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    //팔로우 받기
    public void requestFollow() {

        databaseReference.child("users").child(currentuserid)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        FollowDTO followDTO = mutableData.getValue(FollowDTO.class);

                        if (followDTO == null) {
                            followDTO = new FollowDTO();
                            followDTO.followingCount = 1;
                            followDTO.followings.put(uid, true);
                            mutableData.setValue(followDTO);

                            return Transaction.success(mutableData);
                        }

                        if (followDTO.followings.containsKey(uid)) {

                            followDTO.followingCount = followDTO.followingCount - 1;
                            followDTO.followings.remove(uid);
                        } else {


                            followDTO.followingCount = followDTO.followingCount + 1;
                            followDTO.followings.put(uid, true);
                            followerAlarm(uid);
                        }

                        mutableData.setValue(followDTO);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

        databaseReference.child("users").child(uid)
                .runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {

                        FollowDTO followDTO = mutableData.getValue(FollowDTO.class);

                        if (followDTO == null) {

                            followDTO = new FollowDTO();
                            followDTO.followerCount = 1;
                            followDTO.followers.put(currentuserid, true);
                            mutableData.setValue(followDTO);

                            return Transaction.success(mutableData);
                        }
                        if (followDTO.followers.containsKey(currentuserid)) {

                            followDTO.followerCount = followDTO.followerCount - 1;
                            followDTO.followers.remove(currentuserid);

                        } else {

                            followDTO.followerCount = followDTO.followerCount + 1;
                            followDTO.followers.put(currentuserid, true);
                        }
                        mutableData.setValue(followDTO);

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
    }

    private void followerAlarm(String destinationUid) {

        AlarmDTO alarmDTO = new AlarmDTO();

        alarmDTO.destinationUid = destinationUid;
        alarmDTO.userId = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        alarmDTO.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        alarmDTO.kind = 2;

        databaseReference.child("alarms").push().setValue(alarmDTO);
    }

    private  void signout(){

        auth.signOut();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_PROFILE_FROM_ALBUM ){


        }
    }
}


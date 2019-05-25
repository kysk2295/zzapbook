package com.kys.lg.zzakbook.Fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.kys.lg.zzakbook.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Fragment3 extends Fragment {

    private String part;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Fragment4 fragment4= new Fragment4();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = new Bundle();
        bundle.putString("destinationUid", uid);
        fragment4.setArguments(bundle);


        return inflater.inflate(R.layout.fragment_fragment3, container, false);




    }


}

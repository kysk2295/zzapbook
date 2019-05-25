package com.kys.lg.zzakbook;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.kys.lg.zzakbook.Adapter.PagerAdapter;
import com.kys.lg.zzakbook.Fragment.Fragment1;
import com.kys.lg.zzakbook.Fragment.Fragment2;
import com.kys.lg.zzakbook.Fragment.Fragment3;
import com.kys.lg.zzakbook.Fragment.Fragment4;
import com.kys.lg.zzakbook.Fragment.Fragment5;


import java.util.List;
import java.util.Timer;


public class MainActivity extends AppCompatActivity {

    private PagerAdapter pagerAdapter;
    private ViewPager vp;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private FrameLayout frameLayout;
    private ImageView camera;
    int num=3;

    public static final String FRAGMENT_ARG ="ARG_NO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        vp=findViewById(R.id.vp);
        camera=findViewById(R.id.camera);
        frameLayout=findViewById(R.id.fragment_layout);
        BottomNavigationViewEx bottomNavigationViewEx=(BottomNavigationViewEx) findViewById(R.id.bottom_navigationview);

        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);


        pagerAdapter= new PagerAdapter(getSupportFragmentManager());
        vp.setAdapter(pagerAdapter);

        bottomNavigationViewEx.setupWithViewPager(vp);
        bottomNavigationViewEx.enableShiftingMode(0,true);


        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){

            setPermission();
            return;
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            setPermission();

            return;
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET)!=PackageManager.PERMISSION_GRANTED){
            setPermission();

            return;
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            setPermission();

            return;
        }
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            setPermission();
            return;

        }

        Fragment4 fragment4= new Fragment4();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Bundle bundle = new Bundle();
        bundle.putString("destinationUid", uid);
        fragment4.setArguments(bundle);



        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent i = new Intent(MainActivity.this,WritingActivity.class);
              startActivity(i);
            }
        });

        bottomNavigationViewEx.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {



            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){

                    case R.id.home:
                        vp.setCurrentItem(0);
                        return true;

                    case R.id.friends:
                        vp.setCurrentItem(1);
                        return true;

                    case R.id.tv:
                        vp.setCurrentItem(2);
                        return true;

                    case R.id.account:
                        Fragment user= new Fragment4();
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        Bundle bundle = new Bundle();
                        bundle.putString("destinationUid", uid);
                        user.setArguments(bundle);
                        Toast.makeText(getApplicationContext(),"lfaksjdlfjs",Toast.LENGTH_LONG).show();



                        vp.setCurrentItem(3);

                        return true;

                    case R.id.bell:
                        vp.setCurrentItem(4);
                        return true;

                }
                return false;
            }
        });


    }

    PermissionListener permissionListener= new PermissionListener() {
        @Override
        public void onPermissionGranted() {

        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(),"권한이 설정되어 있지 않습니다.",Toast.LENGTH_LONG).show();
                finish();

        }
    };

    private void setPermission() {
        TedPermission.with(this).setPermissionListener(permissionListener).setDeniedMessage("이 앱에서 요구하는 권한들이 있습니다.\n[설정]" +
                "->[권한]에서 해당 권한을 활성화 해 주세요").setPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_CONTACTS,Manifest.permission.INTERNET)
                .check();

    }



    @Override
    public void onBackPressed() {

        if(num!=3){
            finish();
        }
        else{
            Toast.makeText(getApplicationContext(),"'뒤로' 가기를 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
            timer.sendEmptyMessage(0);
        }
    }



    Handler timer= new Handler(){
        @Override
        public void handleMessage(Message msg) {

            timer.sendEmptyMessageDelayed(0,1000);
            if(num>0){
                --num;
            }
            else{
                num=3;

                timer.removeMessages(0);
            }
        }
    };
}

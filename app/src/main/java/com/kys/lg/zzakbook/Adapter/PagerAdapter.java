package com.kys.lg.zzakbook.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.LinearLayout;

import com.kys.lg.zzakbook.Fragment.Fragment1;
import com.kys.lg.zzakbook.Fragment.Fragment2;
import com.kys.lg.zzakbook.Fragment.Fragment3;
import com.kys.lg.zzakbook.Fragment.Fragment4;
import com.kys.lg.zzakbook.Fragment.Fragment5;

public class PagerAdapter extends FragmentStatePagerAdapter {
    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){

            case 0:return new Fragment1();
            case 1: return new Fragment2();
            case 2: return new Fragment3();
            case 3:return new Fragment4();
            case 4: return new Fragment5();
            default: return null;

        }

    }

    @Override
    public int getCount() {
        return 5;
    }
}

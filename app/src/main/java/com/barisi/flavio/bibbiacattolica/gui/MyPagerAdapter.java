package com.barisi.flavio.bibbiacattolica.gui;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.barisi.flavio.bibbiacattolica.fragments.LeggiFragment;


public abstract class MyPagerAdapter extends FragmentStatePagerAdapter {
    
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public abstract LeggiFragment getmCurrentFragment();

}

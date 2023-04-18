package com.barisi.flavio.bibbiacattolica.gui;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.barisi.flavio.bibbiacattolica.fragments.LeggiFragment;


public abstract class MyPagerAdapter extends FragmentStatePagerAdapter {
    
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public abstract LeggiFragment getmCurrentFragment();

}

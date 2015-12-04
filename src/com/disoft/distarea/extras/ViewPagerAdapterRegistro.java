package com.disoft.distarea.extras;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.disoft.distarea.RegCP;
import com.disoft.distarea.RegFin;
import com.disoft.distarea.RegOp1;
import com.disoft.distarea.RegOp2;
import com.disoft.distarea.RegRecPass;

public class ViewPagerAdapterRegistro extends FragmentPagerAdapter {
	 	SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    final int PAGE_COUNT = 5;
    public ViewPagerAdapterRegistro(FragmentManager fm){super(fm);}
    @Override public Fragment getItem(int position) {
    	switch (position) {
        case 0: return new RegCP();
        case 1: return new RegRecPass();
        case 2:	return new RegOp1();
        case 3:	return new RegOp2();
        case 4:	return new RegFin();
        } return null;}
    @Override public int getCount(){return PAGE_COUNT;}
}
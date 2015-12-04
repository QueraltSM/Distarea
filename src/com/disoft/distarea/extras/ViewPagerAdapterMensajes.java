package com.disoft.distarea.extras;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.disoft.distarea.Enviados;
import com.disoft.distarea.Recibidos;
import com.disoft.distarea.Redactar;

public class ViewPagerAdapterMensajes extends FragmentPagerAdapter {
 
    final int PAGE_COUNT = 3;
    public ViewPagerAdapterMensajes(FragmentManager fm){super(fm);}
    @Override public Fragment getItem(int position) {
    	switch (position) {
        case 0:
            return new Recibidos();
        case 1: 
            return new Enviados();
        case 2:	
            return new Redactar();
        } return null;
    }
    @Override public int getCount(){return PAGE_COUNT;}
}
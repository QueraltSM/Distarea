package com.disoft.distarea;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/*import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;*/
import com.disoft.distarea.extras.LockableViewPager;
import com.disoft.distarea.extras.ViewPagerAdapterRegistro;

public class Registro2 extends AppCompatActivity {
  ActionBar mActionBar; LockableViewPager vp; 
  SharedPreferences sharedPrefs; static ActionBar.Tab tab;

  @Override protected void onCreate(Bundle savedInstanceState) {
  	super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.registro2);
    sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    mActionBar = getSupportActionBar();
    mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME|ActionBar.DISPLAY_HOME_AS_UP);
    mActionBar.setTitle(getString(R.string.ru));
    vp = (LockableViewPager) findViewById(R.id.pager);
    FragmentManager fm = getSupportFragmentManager();
    ViewPager.SimpleOnPageChangeListener ViewPagerListener = new ViewPager.SimpleOnPageChangeListener() {
      @Override public void onPageSelected(int position) {
      	super.onPageSelected(position);
        mActionBar.setSelectedNavigationItem(position); }};
    vp.setOnPageChangeListener(ViewPagerListener);
    ViewPagerAdapterRegistro vpa = new ViewPagerAdapterRegistro(fm);
    vp.setAdapter(vpa);
    // Capture tab button clicks
    //activarNavegacion();
  	}
    
  @Override public boolean onOptionsItemSelected(MenuItem item) {
  	if(sharedPrefs.getBoolean("ch",true)==true) vp.performHapticFeedback(1);
      if (item.getItemId() == android.R.id.home) {
      	if(RegFin.flagventana==0){
       	Intent intent = new Intent(this, ListaCompra.class);
    		startActivity(intent); finish(); }}return true; }
    
  @Override public void onBackPressed() {
  	if(RegFin.flagventana==0){
    super.onBackPressed();   
    Intent intent = new Intent(this, ListaCompra.class);
    startActivity(intent); finish(); }}

  public void activarNavegacion(){
  	mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		mActionBar.setStackedBackgroundDrawable(getResources().getDrawable(R.drawable.azbackground2));
			ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	    	@Override public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	    		// Pass the position on tab click to ViewPager
	        mActionBar.setSubtitle(tab.getTag().toString());
	       	if(vp.getChildCount()>=tab.getPosition())
	       		vp.setCurrentItem(tab.getPosition()); }
	    @Override public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}
	    @Override public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {} };
	    mActionBar.addTab(mActionBar.newTab().setText("1")
	      .setTabListener(tabListener).setTag(getString(R.string.ru)));
	    mActionBar.addTab(mActionBar.newTab().setText("2")
	    	.setTabListener(tabListener).setTag(getString(R.string.seg)));
	    mActionBar.addTab(mActionBar.newTab().setText("3")
	    	.setTabListener(tabListener).setTag(getString(R.string.op1)));
	    mActionBar.addTab(mActionBar.newTab().setText("4")
	    	.setTabListener(tabListener).setTag(getString(R.string.op2)));
	    mActionBar.addTab(mActionBar.newTab().setText("5")
	    	.setTabListener(tabListener).setTag(getString(R.string.fin)));
	   vp.setPageLock(false);
  }
}
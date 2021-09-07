package com.disoft.distarea;

import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.ViewGroup;

//import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class OpcionesVoz extends PreferenceActivity {

	private AppCompatDelegate delegado;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opciones);
	}

	private AppCompatDelegate getDelegado() {
		if (delegado == null) {
			delegado = AppCompatDelegate.create(this, null);
		}
		return delegado;
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getDelegado().onPostCreate(savedInstanceState);
	}

	/*@Override
	public void setContentView(View view) {
		getDelegado().setContentView(view);
	}*/

	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().setContentView(view, params);
	}

	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		getDelegado().addContentView(view, params);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		getDelegado().onPostResume();
	}

	@Override
	protected void onTitleChanged(CharSequence title, int color) {
		super.onTitleChanged(title, color);
		getDelegado().setTitle(title);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		getDelegado().onConfigurationChanged(newConfig);
	}

	@Override
	protected void onStop() {
		super.onStop();
		getDelegado().onStop();
	}

	public void invalidateOptionsMenu() {
		getDelegado().invalidateOptionsMenu();
	}
}

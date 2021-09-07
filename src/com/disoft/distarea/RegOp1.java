package com.disoft.distarea;

import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/*import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;*/
import com.disoft.distarea.extras.DatabaseHandler;

public class RegOp1 extends Fragment {

	SharedPreferences sharedPrefs; DatabaseHandler db;
	FragmentActivity sfa; SharedPreferences.Editor spe;
	static TelephonyManager tMgr; View view;
	public static EditText nombre, tlf, dir;
	
	
	@Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		sfa = getActivity();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(sfa.getBaseContext());
		try{
			tMgr =(TelephonyManager)sfa.getSystemService(Context.TELEPHONY_SERVICE);
			((EditText)sfa.findViewById(R.id.tlf)).setText(tMgr.getLine1Number());
		}catch(Exception e){e.printStackTrace();}
        nombre = (EditText)sfa.findViewById(R.id.nombre);
        tlf = (EditText)sfa.findViewById(R.id.tlf);
        dir = (EditText)sfa.findViewById(R.id.dir);
	}

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {super.onViewCreated(view, savedInstanceState);}
    @Override public View onCreateView
    (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.regop1, container, false);
        return view;}
    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        setUserVisibleHint(true);}
}

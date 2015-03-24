package com.duam.porky;

import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.duam.porky.helpers.PorkyOpenHelper;

public abstract class PorkyActivity extends RoboActivity 
{
	private static final String TAG = PorkyActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		SharedPreferences prefs = getSharedPreferences(ConstantesPorky.PORKY_PREFS, MODE_PRIVATE);
		String username = prefs.getString(ConstantesPorky.PREF_NOMBRE_USUARIO, ""); 
		
		Log.d(TAG, "Nombre de usuario: '"+ username +"'");
		if (username.isEmpty())
		{
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.porky, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		if (item.getItemId() == R.id.action_logout)
		{
			PorkyOpenHelper poh = new PorkyOpenHelper(this);
			int cantConceptosBorrados = poh.deleteAllConceptos();
			
			SharedPreferences prefs = getSharedPreferences(ConstantesPorky.PORKY_PREFS, MODE_PRIVATE);
			SharedPreferences.Editor editor = prefs.edit();
			
			editor.remove(ConstantesPorky.PREF_ID_USUARIO);
			editor.remove(ConstantesPorky.PREF_NOMBRE_USUARIO);
			editor.commit();
			
			Toast toast = Toast.makeText(this, "Se eliminaron los "+ cantConceptosBorrados +" conceptos del usuario.", Toast.LENGTH_LONG);
			toast.show();
			
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			
			return true;
		}
		else
		{
			return false;
		}
	}

}

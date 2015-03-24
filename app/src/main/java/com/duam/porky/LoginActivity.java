package com.duam.porky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.duam.porky.model.Usuario;
import com.duam.porky.tasks.LoginTask;

public class LoginActivity extends Activity 
{
	private static final String TAG = LoginActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() 
		{
			@Override
			public void onClick(View view) 
			{
				Log.d(TAG, "onClick");
				attemptLogin();
			}
		});
	}

	public void attemptLogin() 
	{
		String username = ((TextView) findViewById(R.id.editUsuario)).getText().toString();
		String password = ((TextView) findViewById(R.id.editClave)).getText().toString();
		
		final ProgressDialog pd = ProgressDialog.show(this, "Consultando al servidor", "Aguarde un momento por favor...");
		
		LoginTask task = new LoginTask()
		{
			@Override
			protected void onPostExecute(Usuario usuario) 
			{
				pd.dismiss();
				
				if (usuario != null)
				{
					SharedPreferences prefs = getSharedPreferences(ConstantesPorky.PORKY_PREFS, MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					
					editor.putString(ConstantesPorky.PREF_NOMBRE_USUARIO, usuario.getNombreUsuario());
					editor.putLong(ConstantesPorky.PREF_ID_USUARIO, usuario.get_id());
					editor.commit();
					
					Intent intent = new Intent(LoginActivity.this, MovimientoActivity.class);
					startActivity(intent);
				}
				else
				{
					Toast toast = Toast.makeText(LoginActivity.this, "Usuario o clave incorrectas", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		};
		task.execute(username, password);
	}

}

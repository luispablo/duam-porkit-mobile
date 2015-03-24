package com.duam.porky.tasks;

import static com.duam.porky.ConstantesPorky.URL_PORKY;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.duam.porky.model.Usuario;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.kevinsawicki.http.HttpRequest.HttpRequestException;

public class LoginTask extends AsyncTask<String, Void, Usuario>
{
	private static final String TAG = LoginTask.class.getName();

	/**
	 * <p>
	 * 	 params[0]: username <br/>
	 *   params[1]: password <br/>
	 * </p>
	 */
	@Override
	protected Usuario doInBackground(String... params) 
	{
		String url = URL_PORKY +"/ws/usuarios/login";
		
		Map<String, String> formParams = new HashMap<String, String>();
		formParams.put("username", params[0]);
		formParams.put("password", params[1]);

		HttpRequest req = HttpRequest.post(url).form(formParams);
		Usuario usuario = null;
		
		if (req.ok())
		{
			Log.d(TAG, "Response OK, parsing JSON.");
			
			try 
			{
				JSONObject jsonUsuario = new JSONObject(req.body());
				usuario = new Usuario();
				usuario.set_id(jsonUsuario.getLong("id"));
				usuario.setApellido(jsonUsuario.getString("apellido"));
				usuario.setEmail(jsonUsuario.getString("email"));
				usuario.setNombre(jsonUsuario.getString("nombre"));
				usuario.setNombreUsuario(jsonUsuario.getString("nombreUsuario"));
			} 
			catch (HttpRequestException e) 
			{
				throw new RuntimeException(e);
			} 
			catch (JSONException e) 
			{
				throw new RuntimeException(e);
			}
		}
		else
		{
			Log.d(TAG, "Response not OK...");
		}
		
		return usuario;
	}

}

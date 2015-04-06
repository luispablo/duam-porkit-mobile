package com.duam.porky;

public class ConstantesPorky 
{
	public static final String DEFAULT_USERNAME = "";
	public static final String URL_PORKY = "http://duam-porky.ddns.net";//"http://192.168.1.10:8080"; //
	public static final String WS_FIELD_SEPARATOR = "|||";
	public static final String ENCODING_UTF_8 = "UTF-8";
	public static final String ENCODING_ISO_8859_1 = "ISO-8859-1";
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	
	public static final String GET_CONCEPTOS_URI = "/ws/conceptos";
	public static final String GET_MOVIMIENTOS_URI = "/ws/movimientos";
	public static final String NUEVO_MOVIMIENTO_URI = "/ws/movimientos/nuevo";
	public static final String BORRAR_MOVIMIENTO_URI = "/ws/movimientos/borrar";
	public static final String MODIFICAR_MOVIMIENTO_URI = "/ws/movimientos/modificar";
	
	public static final String PORKY_PREFS = "PorkyPrefs";
	public static final String PREF_NOMBRE_USUARIO = "nombreUsuario";
	public static final String PREF_ID_USUARIO = "usuarioId";

	public class PorkitAPI {
		public static final String URL = "http://porkitapi.duamsistemas.com.ar/";
		public static final String URI_MOVIMIENTOS_LIST = "movimientos/%s/%s/%s/%d/%d";
		public static final String URI_MOVIMIENTOS_DELETE = "movimientos/%d";
	}
}

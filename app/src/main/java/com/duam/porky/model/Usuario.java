package com.duam.porky.model;

public class Usuario 
{
	private long _id; 
    private String nombre;
    private String apellido;
    private String nombreUsuario;
    private String email;
    private String clave;
    private long comunidadId;

    public Usuario()
    {
    	
    }
    
    public Usuario(long _id, String nombre, String apellido, String nombreUsuario, String email, String clave, long comunidadId)
    {
    	this._id = _id;
    	this.nombre = nombre;
    	this.apellido = apellido;
    	this.nombreUsuario = nombreUsuario;
    	this.email = email;
    	this.clave = clave;
    	this.comunidadId = comunidadId;
    }

	public long get_id() 
	{
		return _id;
	}

	public void set_id(long _id) 
	{
		this._id = _id;
	}

	public String getNombre() 
	{
		return nombre;
	}

	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}

	public String getApellido() 
	{
		return apellido;
	}

	public void setApellido(String apellido) 
	{
		this.apellido = apellido;
	}

	public String getNombreUsuario() 
	{
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) 
	{
		this.nombreUsuario = nombreUsuario;
	}

	public String getEmail() 
	{
		return email;
	}

	public void setEmail(String email) 
	{
		this.email = email;
	}

	public String getClave() 
	{
		return clave;
	}

	public void setClave(String clave) 
	{
		this.clave = clave;
	}

	public long getComunidadId() 
	{
		return comunidadId;
	}

	public void setComunidadId(long comunidadId) 
	{
		this.comunidadId = comunidadId;
	}

}

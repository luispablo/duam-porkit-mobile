package com.duam.porky.model;

public class Concepto 
{
	private long _id;
	private long usuarioId;
	private String nombre;
	private long factorId;

	public Concepto()
	{
		
	}
	
	public Concepto(long _id, long usuarioId, String nombre, long factorId)
	{
		this._id = _id;
		this.usuarioId = usuarioId;
		this.nombre = nombre;
		this.factorId = factorId;
	}

	public long get_id() 
	{
		return _id;
	}

	public void set_id(long _id) 
	{
		this._id = _id;
	}

	public long getUsuarioId() 
	{
		return usuarioId;
	}

	public void setUsuarioId(long usuarioId) 
	{
		this.usuarioId = usuarioId;
	}

	public String getNombre() 
	{
		return nombre;
	}

	public void setNombre(String nombre) 
	{
		this.nombre = nombre;
	}

	public long getFactorId() 
	{
		return factorId;
	}

	public void setFactorId(long factorId) 
	{
		this.factorId = factorId;
	}

}

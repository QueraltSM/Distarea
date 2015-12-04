package com.disoft.distarea.models;

public class Iof
{
	int oid, tipo;
	String quien, fecha, hora, texto, imagen, http, categorias, cliente, idmovil;
	public Iof(){}
	public Iof(int oid, String quien, String fecha, String hora, String texto, String imagen,
			String http, String categorias, String cliente, int tipo, String idmovil) 
	{this.oid=oid; this.quien=quien; this.fecha=fecha; this.hora=hora; this.texto=texto; 
	this.imagen=imagen; this.http=http; this.categorias=categorias; this.tipo=tipo; this.idmovil=idmovil;}
	public int getOid(){return oid;}
	public void setOid(int oid){this.oid = oid;}
	public String getQuien(){return quien;}
	public void setQuien(String quien){this.quien = quien;}
	public String getFecha(){return fecha;}
	public void setFecha(String fecha){this.fecha = fecha;}
	public String getHora(){return hora;}
	public void setHora(String hora){this.hora = hora;}
	public String getTexto(){return texto;}
	public void setTexto(String texto){this.texto = texto;}
	public String getImagen(){return imagen;}
	public void setImagen(String imagen){this.imagen = imagen;}
	public String getHttp(){return http;}
	public void setHttp(String http){this.http = http;}
	public String getCategorias(){return categorias;}
	public void setCategorias(String categorias){this.categorias = categorias;}
	public String getCliente(){return cliente;}
	public void setCliente(String cliente){this.cliente = cliente;}
	public int getTipo(){return tipo;}
	public void setTipo(int tipo){this.tipo = tipo;}
	public String getIdmovil(){return idmovil;}
	public void setIdmovil(String idmovil){this.idmovil = idmovil;}
}

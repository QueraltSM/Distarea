package com.disoft.distarea.models;

public class CliF
{
	int idcf, idest, iddis, iddis_vend;
	String nombre, mail, pais, provincia, municipio, direccion, tel, movil, fechanac, nif, ref, sexo, tipocliente;
	public CliF(){}
	//Basic de cuando las asesorías
	public CliF(int idcf, String nombre, String ref) 
		{this.idcf=idcf; this.nombre=nombre; this.ref=ref; }
	//En base a los NOT NULL de la tabla ClientesF
	public CliF(int idcf, String nombre, String mail, int idest, String pais, 
			String direccion, String tel, String ref, int iddis_vend, String sexo) 
	{this.idcf=idcf; this.nombre=nombre; this.mail=mail; this.idest=idest; this.pais=pais;
	this.direccion=direccion; this.tel=tel; this.ref=ref; this.iddis_vend=iddis_vend; this.sexo=sexo; }
	//Todo en 1
	public CliF(int idcf, String nombre, String mail, int idest, String pais, String provincia, 
			String municipio, String direccion, String tel, String movil, String fechanac, 
			String nif, String ref, int iddis, int iddis_vend, String sexo, String tipocliente) 
	{this.idcf=idcf; this.nombre=nombre; this.mail=mail; this.idest=idest; this.pais=pais;
	this.provincia=provincia; this.municipio=municipio; this.direccion=direccion; this.tel=tel;
	this.movil=movil; this.fechanac=fechanac; this.nif=nif; this.ref=ref; this.iddis=iddis; 
	this.iddis_vend=iddis_vend; this.sexo=sexo; this.tipocliente=tipocliente;}
	//
	public int getIdcf(){return idcf;}
	public void setIdcf(int idcf){this.idcf = idcf;}
	public String getNombre(){return nombre;}
	public void setNombre(String nombre){this.nombre = nombre;}
	public String getMail(){return mail;}
	public void setMail(String mail){this.mail = mail;}
	public int getIdest(){return idest;}
	public void setIdest(int idest){this.idest = idest;}
	public String getPais(){return pais;}
	public void setPais(String pais){this.pais = pais;}
	public String getProvincia(){return provincia;}
	public void setProvincia(String provincia){this.provincia = provincia;}
	public String getMunicipio(){return municipio;}
	public void setMunicipio(String municipio){this.municipio = municipio;}
	public String getDireccion(){return direccion;}
	public void setDireccion(String direccion){this.direccion = direccion;}
	public String getTel(){return tel;}
	public void setTel(String tel){this.tel = tel;}
	public String getMovil(){return movil;}
	public void setMovil(String movil){this.movil = movil;}
	public String getFechanac(){return fechanac;}
	public void setFechanac(String fechanac){this.fechanac = fechanac;}
	public String getNif(){return nif;}
	public void setNif(String nif){this.nif = nif;}
	public String getRef(){return ref;}
	public void setRef(String ref){this.ref = ref;}
	public int getIddis(){return iddis;}
	public void setIddis(int iddis){this.iddis = iddis;}
	public int getIddisVend(){return iddis_vend;}
	public void setIddisVend(int iddis_vend){this.iddis_vend = iddis_vend;}
	public String getSexo(){return sexo;}
	public void setSexo(String sexo){this.sexo = sexo;}
	public String getTipocliente(){return tipocliente;}
	public void setTipocliente(String tipocliente){this.tipocliente = tipocliente;}
}

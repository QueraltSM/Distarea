package com.disoft.distarea.models;

public class Msj
{
	int mid, clienteglobal, eid, midbd;// idmsjappmovil; <-- Es autonumérico, no se usa salvo en la BBDD
	String mensaje, http, fecharealiz, horarealiz, zonainf, tipomsj, desdefecha, 
		   hastafecha, fecharec, horarec, estado, rmte, xml;
	public Msj(){}
	public Msj(int mid, int clienteglobal, String mensaje, String http, String fecharealiz, String horarealiz,
			String zonainf, int eid, String tipomsj, String desdefecha, String hastafecha, String fecharec, 
			String horarec, String estado, String rmte, int midbd, String xml) 
	{this.mid=mid; this.clienteglobal=clienteglobal; this.mensaje=mensaje; this.http=http; 
	this.fecharealiz=fecharealiz; this.horarealiz=horarealiz; this.zonainf=zonainf; this.eid=eid; 
	this.tipomsj=tipomsj; this.desdefecha=desdefecha; this.hastafecha=hastafecha; this.fecharec=fecharec; 
	this.horarec=horarec; this.estado=estado; this.rmte=rmte; this.midbd=midbd; this.xml=xml;}
	public int getMid(){return mid;}
	public void setMid(int mid){this.mid = mid;}
	public int getClienteglobal(){return clienteglobal;}
	public void setClienteglobal(int clienteglobal){this.clienteglobal = clienteglobal;}
	public String getMensaje(){return mensaje;}
	public void setMensaje(String mensaje){this.mensaje = mensaje;}
	public String getHttp(){return http;}
	public void setHttp(String http){this.http = http;}
	public String getFecharealiz(){return fecharealiz;}
	public void setFecharealiz(String fecharealiz){this.fecharealiz = fecharealiz;}
	public String getHorarealiz(){return horarealiz;}
	public void setHorarealiz(String horarealiz){this.horarealiz = horarealiz;}
	public String getZonainf(){return zonainf;}
	public void setZonainf(String zonainf){this.zonainf = zonainf;}
	public int getEid(){return eid;}
	public void setEid(int eid){this.eid = eid;}
	public String getTipomsj(){return tipomsj;} // M=masivo(publi); A=AppGlobal; G=GlobalApp
	public void setTipomsj(String tipomsj){this.tipomsj = tipomsj;}
	public String getDesdefecha(){return desdefecha;}
	public void setDesdefecha(String desdefecha){this.desdefecha = desdefecha;}
	public String getHastafecha(){return hastafecha;}
	public void setHastafecha(String hastafecha){this.hastafecha = hastafecha;}
	public String getFecharec(){return fecharec;}
	public void setFecharec(String fecharec){this.fecharec = fecharec;}
	public String getHorarec(){return horarec;}
	public void setHorarec(String horarec){this.horarec = horarec;}
	public String getEstado(){return estado;} // P=...;R=!;L=(tick);else=?; //F=Recibido por el otro
	public void setEstado(String estado){this.estado = estado;}
	public String getRmte(){return rmte;}
	public void setRmte(String rmte){this.rmte = rmte;}
	public int getMidbd(){return midbd;}
	public void setMidbd(int midbd){this.midbd = midbd;}
	public String getXml(){return xml;}
	public void setXml(String xml){this.xml = xml;}
}
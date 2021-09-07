package com.disoft.distarea.extras;

public class InterpreteVoz {
	String tipo; 
	Integer ini, fin;
	
	public InterpreteVoz(){}
	public InterpreteVoz(String tipo, Integer ini){
		this.tipo=tipo; this.ini=ini; }
	
	public void setTipo(String tipo){this.tipo=tipo;}
	public String getTipo(){return tipo;}
	public void setIni(Integer ini){this.ini=ini;}
	public Integer getIni(){return ini;}
	public void setFin(Integer fin){this.fin=fin;}
	public Integer getFin(){return fin;}	
}
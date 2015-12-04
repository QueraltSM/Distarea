package com.disoft.distarea.models;

public class Est
{
	int eid, eggid, prior, tarifa;//, citas, idfacdis;
	String nombre, tel, mail, fechaultima, codcliente, cvisual, gps, cinterno,
	 cid, cih, tv, zi, logo, codinvpub, restrped, passTV, user, reqfirm, reqtel,
	 msjmail, referencia, configura, cnae;
	//sincroprod, sincrofreq;
	boolean activo, fav;//sincro, relfamsubfam;
	float val;
	//Temporales de un pedido
	String referenciapedido, fechacita, horacita, observaciones; 
	public Est(){}
	public Est(int eid, int eggid, String nombre, String tel, String mail, 
			String fechaultima, String codcliente, boolean activo, boolean fav, 
			int prior, float val, String cvisual, String gps, String cinterno, String cid, 
			String cih, String tv, String zi, String logo, int tarifa, String codinvpub, 
			String restrped, String passTV, String user, String reqfirm, String reqtel, 
			String msjmail, String referencia, String configura, String cnae 
			//,int citas, boolean sincro, String sincroprod, String sincrofreq,
			//boolean relfamsubfam, int idfacdis
			){
		this.eid=eid; this.nombre=nombre; this.tel=tel; this.mail=mail; 
		this.fechaultima=fechaultima; this.codcliente=codcliente; this.activo=activo; 
		this.fav=fav; this.prior=prior; this.val=val; this.cvisual=cvisual; this.gps=gps; 
		this.cinterno=cinterno; this.cid=cid; this.cih=cih; this.tv=tv; this.zi=zi; 
		this.logo=logo; this.tarifa=tarifa; this.codinvpub=codinvpub; this.restrped=restrped; 
		this.passTV=passTV; this.user=user; this.reqfirm=reqfirm; this.reqtel=reqtel; 
		this.msjmail=msjmail; this.referencia=referencia; this.configura=configura; this.cnae=cnae;
		}//this.citas=citas; this.sincro=sincro; this.sincroprod=sincroprod; 
		//this.sincrofreq=sincrofreq; this.relfamsubfam=relfamsubfam; this.idfacdis=idfacdis;
	//Nuevo constructor con los 4 vacíos de Pedidos
	public Est(int eid, int eggid, String nombre, String tel, String mail, 
			String fechaultima, String codcliente, boolean activo, boolean fav, 
			int prior, float val, String cvisual, String gps, String cinterno, String cid, 
			String cih, String tv, String zi, String logo, int tarifa, String codinvpub, 
			String restrped, String passTV, String user, String reqfirm, String reqtel, 
			String msjmail, String referencia, String configura, String referenciapedido,
			String fechacita, String horacita, String observaciones, String cnae 
			//,int citas, boolean sincro, String sincroprod, String sincrofreq,
			//boolean relfamsubfam, int idfacdis
			){
		this.eid=eid; this.nombre=nombre; this.tel=tel; this.mail=mail; 
		this.fechaultima=fechaultima; this.codcliente=codcliente; this.activo=activo; 
		this.fav=fav; this.prior=prior; this.val=val; this.cvisual=cvisual; this.gps=gps; 
		this.cinterno=cinterno; this.cid=cid; this.cih=cih; this.tv=tv; this.zi=zi; 
		this.logo=logo; this.tarifa=tarifa; this.codinvpub=codinvpub; this.restrped=restrped; 
		this.passTV=passTV; this.user=user; this.reqfirm=reqfirm; this.reqtel=reqtel; 
		this.msjmail=msjmail; this.referencia=referencia; this.configura=configura; 
		this.referenciapedido=referenciapedido; this.fechacita=fechacita; this.horacita=horacita;
		this.observaciones=observaciones; this.cnae=cnae;
		}//this.citas=citas; this.sincro=sincro; this.sincroprod=sincroprod; 
		//this.sincrofreq=sincrofreq; this.relfamsubfam=relfamsubfam; this.idfacdis=idfacdis;
	
	
	public int getEid(){return eid;}														//0
	public void setEid(int eid){this.eid = eid;}											//0
	public int getEggid(){return eggid;}													//1
	public void setEggid(int eggid){this.eggid = eggid;}									//1
	public String getNombre(){return nombre;}												//2
	public void setNombre(String nombre){this.nombre = nombre;}								//2
	public String getTel(){return tel;}														//3
	public void setTel(String tel){this.tel = tel;}											//3
	public String getMail(){return mail;}													//4
	public void setMail(String mail){this.mail = mail;}										//4
	public String getFechaultima(){return fechaultima;}										//5
	public void setFechaultima(String fechaultima){this.fechaultima = fechaultima;}			//5
	public String getCodcliente(){return codcliente;}										//6
	public void setCodcliente(String codcliente){this.codcliente = codcliente;}				//6
	public boolean getActivo(){return activo;}												//7
	public void setActivo(boolean activo){this.activo = activo;}							//7
	public boolean getFav(){return fav;}													//8
	public void setFav(boolean fav){this.fav = fav;}										//8
	public int getPrior(){return prior;}													//9
	public void setPrior(int prior){this.prior = prior;}									//9
	public float getVal(){return val;}														//10
	public void setVal(float val){this.val = val;}											//10
	public String getCvisual(){return cvisual;}												//11
	public void setCvisual(String cvisual){this.cvisual = cvisual;}							//11
	public String getGps(){return gps;}														//12
	public void setGps(String gps){this.gps = gps;}											//12
	public String getCinterno(){return cinterno;}											//13
	public void setCinterno(String cinterno){this.cinterno = cinterno;}						//13
	public String getCid(){return cid;}														//14
	public void setCid(String cid){this.cid = cid;}											//14
	public String getCih(){return cih;}														//15
	public void setCih(String cih){this.cih = cih;}											//15
	public String getTv(){return tv;}														//16
	public void setTv(String tv){this.tv = tv;}												//16
	public String getZi(){return zi;}														//17
	public void setZi(String zi){this.zi = zi;}												//17
	public String getLogo(){return logo;}													//18
	public void setLogo(String logo){this.logo = logo;}										//18
	public int getTarifa(){return tarifa;}													//19
	public void setTarifa(int tarifa){this.tarifa = tarifa;}								//19
	public String getCodinvpub(){return codinvpub;}											//20
	public void setCodinvpub(String codinvpub){this.codinvpub = codinvpub;}					//20
	public String getRestrped(){return restrped;}											//21
	public void setRestrped(String restrped){this.restrped = restrped;}						//21
	public String getPassTV(){return passTV;}												//22
	public void setPassTV(String passTV){this.passTV = passTV;}								//22
	public String getUser(){return user;}													//23
	public void setUser(String user){this.user = user;}										//23
	public String getReqfirm(){return reqfirm;}												//24
	public void setReqfirm(String reqfirm){this.reqfirm = reqfirm;}							//24
	public String getReqtel(){return reqtel;}												//25
	public void setReqtel(String reqtel){this.reqtel = reqtel;}								//25
	public String getMsjmail(){return msjmail;}												//26
	public void setMsjmail(String msjmail){this.msjmail = msjmail;}							//26
	public String getReferencia(){return referencia;}										//27
	public void setReferencia(String referencia){this.referencia = referencia;}				//27
	public String getConfigura(){return configura;}											//28
	public void setConfigura(String configura){this.configura = configura;}					//28
	//Temporales de un pedido
	public String getReferenciapedido(){return referenciapedido;}										//29
	public void setReferenciapedido(String referenciapedido){this.referenciapedido = referenciapedido;}	//29
	public String getFechacita(){return fechacita;}														//30
	public void setFechacita(String fechacita){this.fechacita = fechacita;}								//30
	public String getHoracita(){return horacita;}														//31
	public void setHoracita(String horacita){this.horacita = horacita;}									//31
	public String getObservaciones(){return observaciones;}												//32
	public void setObservaciones(String observaciones){this.observaciones = observaciones;}				//32
	public String getCnae(){return cnae;}													//33
	public void setCnae(String cnae){this.cnae = cnae;}										//33
	
	
	/*public int getCitas(){return citas;}													//34
	public void setCitas(int citas){this.citas = citas;}									//34
	public boolean getSincro(){return sincro;}												//35
	public void setSincro(boolean sincro){this.sincro = sincro;}							//35
	public String getSincroprod(){return sincroprod;}										//36
	public void setSincroprod(String sincroprod){this.sincroprod= sincroprod;}				//36
	public String getSincrofreq(){return sincrofreq;}										//37
	public void setSincrofreq(String sincrofreq){this.sincrofreq = sincrofreq;}				//37
	public boolean getRelfamsubfam(){return relfamsubfam;}									//38								
	public void setRelfamsubfam(boolean relfamsubfam){this.relfamsubfam = relfamsubfam;}	//38
	public int getIdfacdis(){return idfacdis;}												//39
	public void setIdfacdis(int idfacdis){this.idfacdis = idfacdis;}						//39
	¿Date SincroTV?																			//40
*/	
}
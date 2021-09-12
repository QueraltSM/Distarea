package com.disoft.distarea.extras;

import android.util.Log;

import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
 
 
public class Automailer extends javax.mail.Authenticator { 
  
  private String[] _to; 
  private String[] _bcc;
  
  private String _host; 
  private String _port; 
  private String _sport; 
 
  private String _user; 
  private String _pass; 
  private String _from; 
  private String _subject; 
  private String _body; 
 
  private boolean _debuggable; 
  private boolean _auth; 
   
  private Multipart _multipart;
  
  private int html;
 
  public Automailer() { 
    _host = "smtp.gmail.com";//"smtp.disoftweb.com"; // default smtp server 
    _port = "465";//"25"; // default smtp port 
    _sport = "465";//"25"; // default socketfactory port 
 
    _user = ""; // username 
    _pass = ""; // password 
    _from = ""; // email sent from 
    _subject = ""; // email subject 
    _body = ""; // email body 
 
    _debuggable = false; // debug mode on or off - default off 
    _auth = true; // smtp authentication - default on 
 
    _multipart = new MimeMultipart(); 
    
    html=0;
 
    // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added. 
    MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
    mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
    mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
    mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
    mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
    mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822"); 
    CommandMap.setDefaultCommandMap(mc); 
  } 
 
  public Automailer(String user, String pass) {
    Log.e("entro automailer", "yes!");
    this._user = user;
    this._pass = pass;
  } 
  
  public void setHTML(){ html=1; }
 
  public synchronized boolean send() throws Exception {
    Properties props = _setProperties(); 
 
    if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) { 
      Session session = Session.getInstance(props, this);
      
      MimeMessage msg = new MimeMessage(session); 
 
      msg.setFrom(new InternetAddress(_from));
      
      /*InternetAddress[] addressTo = new InternetAddress[_to.length]; 
      */
      InternetAddress[] addressTo = new InternetAddress[_to.length];
      for (int i = 0; i < _to.length; i++) {
            addressTo[i] = new InternetAddress(_to[i]);
          }
      msg.setRecipients(RecipientType.TO, addressTo);
      msg.setSubject(_subject);
      msg.setSentDate(new Date());
      
      InternetAddress[] addressFrom = new InternetAddress[1]; 
      addressFrom[0] = new InternetAddress(_from);
      msg.setReplyTo(addressFrom);
 
      // setup message body 
      BodyPart messageBodyPart = new MimeBodyPart();
      if(html==0) messageBodyPart.setText(_body);
      else messageBodyPart.setContent(_body, "text/html; charset=utf8");
      _multipart.addBodyPart(messageBodyPart); 
 
      // Put parts in message smtp.gmail.com"
      msg.setContent(_multipart); 
 
      // send email 
        Log.e("estoy aqui", "siiahsvahjvsasa");
      	Transport.send(msg);
      Log.e("estoy aqui", "sendt");
      return true; 
    } else { return false; } 
  } 
 
  public void addAttachment(String filename) throws Exception { 
    BodyPart messageBodyPart = new MimeBodyPart(); 
    DataSource source = new FileDataSource(filename); 
    messageBodyPart.setDataHandler(new DataHandler(source)); 
    messageBodyPart.setFileName(filename); 
 
    _multipart.addBodyPart(messageBodyPart); 
  } 
 
  @Override 
  public PasswordAuthentication getPasswordAuthentication() { 
    return new PasswordAuthentication(_user, _pass); } 
 
  private Properties _setProperties() { 
    Properties props = new Properties(); 
 
    props.put("mail.smtp.host", _host); 
 
    if(_debuggable) { props.put("mail.debug", "true"); } 
    if(_auth) { props.put("mail.smtp.auth", "true"); } 
 
    props.put("mail.smtp.port", _port); 
    props.put("mail.smtp.socketFactory.port", _sport); 
    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
    props.put("mail.smtp.socketFactory.fallback", "false"); 
 
    return props; 
  } 
 
  // the getters and setters 
  public String getBody() { return _body; } 
  public void setBody(String _body) { this._body = _body; } 
  public String getSubject() { return _subject; }	 
  public void setSubject(String _subject) { this._subject = _subject; } 
  public String[] getTo() { return _to; } 
  public void setTo(String[] _to) { this._to= _to; }
  public String[] getBcc() { return _bcc; } 
  public void setBcc(String[] _bcc) { this._bcc= _bcc; }
  public String getFrom() { return _from; } 
  public void setFrom(String _from) { this._from = _from; }
} 
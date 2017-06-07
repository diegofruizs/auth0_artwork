/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.artwork.auth.config;

import co.edu.uniandes.csw.artwork.dtos.minimum.UserDTO;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.lang.Collections;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Asistente
 */
public class AuthenticationApi {
    
  private  Properties prop = new Properties();
  private InputStream input = null;
  private String path;
  private AuthorizationApi authorization;
  
  
  public AuthenticationApi() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException{
     authorization= new AuthorizationApi(); 
     
     path= this.getClass().getProtectionDomain()
             .getCodeSource().getLocation().toString()
             .split("target")[0].substring(6)
             .concat("src/main/java/co/edu/uniandes/csw"
                     + "/artwork/auth/properties/auth0.properties");
      try {  
           input = new FileInputStream(path);
          try {
              prop.load(input);
          } catch (IOException ex) {
              Logger.getLogger(AuthenticationApi.class.getName()).log(Level.SEVERE, null, ex);
          }
      } catch (FileNotFoundException ex) {
          Logger.getLogger(AuthenticationApi.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
  
     public HttpResponse<String> managementToken() throws UnirestException{
     return Unirest.post(prop.getProperty("accessToken").trim())
             .header("content-type", "application/json")
             .body("{\"grant_type\":\"client_credentials\","
             + "\"client_id\": \""+prop.getProperty("clientId")+"\","
             + "\"client_secret\": \""+prop.getProperty("secretKey")+"\","
             + "\"audience\": \"https://alejandr0.auth0.com/api/v2/\"}").asString();
     
     }
     
      public  HttpResponse<String> managementGetUser(String id) throws UnirestException, JSONException{   
     
         return Unirest.get(prop.getProperty("users").trim()+"/"+id.replace("|", "%7C"))
                      .header("content-type", "application/json")
                      .header("Authorization", "Bearer "+getManagementAccessToken()).asString();    
    } 
     
     public HttpResponse<String> authenticationToken(UserDTO dto) throws UnirestException{
    return Unirest.post(prop.getProperty("accessToken"))
                     .header("content-type", "application/json")
                     .body("{\"grant_type\":\"password\","
                     + "\"username\":\""+dto.getUserName()+"\","
                     + "\"password\":\""+dto.getPassword()+"\","
                     + "\"client_id\":\""+prop.getProperty("clientId").trim()+"\","
                     + "\"client_secret\":\""+prop.getProperty("secretKey").trim()+"\"}").asString();
    }
     
     public  HttpResponse<String> authenticationSignUP(UserDTO dto) throws UnirestException{
       
         return Unirest.post(prop.getProperty("signUp").trim())
                     .header("content-type", "application/json")
                     .body("{\"client_id\":\""+prop.getProperty("clientId").trim()+"\","
                     + "\"email\":\""+dto.getEmail()+"\","
                     + "\"password\":\""+dto.getPassword()+"\","
                     + "\"connection\":\""+prop.getProperty("connection").trim()+"\","
                     + "\"user_metadata\":{\"given_name\":\""+dto.getGivenName()+"\","
                     + "\"email\":\""+dto.getEmail()+"\","
                     + "\"username\":\""+dto.getUserName()+"\","
                     + "\"middle_name\":\""+dto.getMiddleName()+"\","
                     + "\"sur_name\":\""+dto.getSurName()+"\"}}").asString();
   }
     public  HttpResponse<String> authenticationUserInfo(UserDTO dto,HttpServletResponse rsp) throws UnirestException, JSONException{   
        return Unirest.get(prop.getProperty("userInfo").trim())
                      .header("Authorization", "Bearer "+getAuthenticationAccessToken(dto, rsp)).asString();    
    } 
   
  
   public  void authenticationLogout(){      
       Unirest.get(prop.getProperty("logout").trim());  
   }
  
   public String getManagementAccessToken() throws UnirestException, JSONException{
    HttpResponse<String> res=managementToken();
    JSONObject json = new JSONObject(res.getBody());
    return (String) json.get("access_token");
   }
    
   public String getAuthenticationAccessToken(UserDTO dto,HttpServletResponse rsp) throws UnirestException, JSONException{
      HttpResponse<String> res=authenticationToken(dto);  
      JSONObject json = new JSONObject(res.getBody());
      rsp.addHeader("id_token", json.get("id_token").toString());
   return (String) json.get("access_token");
   }
   //get user profile
  
   public String getSubject(UserDTO dto,HttpServletResponse rsp) throws UnirestException, JSONException{
   HttpResponse<String> res = authenticationUserInfo(dto,rsp);
   JSONObject json = new JSONObject(res.getBody());
   return  json.get("sub").toString();
   }
   
   public List<String> getRoles(JSONObject json) throws JSONException{
    
   String[] roles=json.getJSONObject("app_metadata")
           .getJSONObject("authorization").get("roles").toString().replaceAll("\"","").replace("[", "").replace("]","").split(",");
    return Collections.arrayToList(roles);
   }
   
    public void HttpServletResponseBinder(HttpResponse<String> rsp,HttpServletResponse res) throws IOException{  
       res.setHeader("content-type", "application/json");
       res.setStatus(rsp.getCode());
       res.getWriter().print(rsp.getBody());
       res.flushBuffer();  
   }
   
    public Jws<Claims> decryptToken(HttpServletRequest req){
       Cookie[] cookie=req.getCookies();
       String jwt=null;
       SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
       String secret = prop.getProperty("secretKey");
       Key signingKey = new SecretKeySpec(secret.getBytes(), signatureAlgorithm.getJcaName());
       Jws<Claims> j;
       for (Cookie c : cookie) {
       if ("id_token".equals(c.getName())) {
          jwt = c.getValue();
        }
   }
       try{
           if(jwt!=null)
      j= Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
           else
               throw new SignatureException("no autenticado");
       }catch(SignatureException se){
        return null;
       }
       return j;
   }
}

    
    

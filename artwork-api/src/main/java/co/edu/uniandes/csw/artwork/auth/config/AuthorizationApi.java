/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.uniandes.csw.artwork.auth.config;


import co.edu.uniandes.csw.artwork.dtos.minimum.UserDTO;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequestWithBody;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.nio.client.HttpAsyncClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class AuthorizationApi {
  private  Properties prop = new Properties();
  private InputStream input = null;
  private String path;
  
  
  
  public AuthorizationApi() throws IOException, UnirestException, JSONException{
      
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
  
  public HttpResponse<String> authorizationAccessToken() throws UnirestException{
      return  Unirest.post(prop.getProperty("accessToken").trim())
                     .header("content-type", "application/json")
                     .body("{\"grant_type\":\""+prop.getProperty("grantType").trim()+"\","
                     + "\"audience\":\""+prop.getProperty("audience").trim()+"\"," 
                     + "\"client_id\":\""+prop.getProperty("clientId").trim()+"\","
                     + "\"client_secret\":\""+prop.getProperty("secretKey").trim()+"\"}").asString();
  }
  
   public HttpResponse<String> authorizationGetGroups() throws UnirestException, JSONException, InterruptedException, ExecutionException{  
  return Unirest.get(prop.getProperty("authorizationExtension").trim()+"/groups")
          .header("Authorization", "Bearer "+getAuthorizationAccessToken()).asString();
  
  }
   
  public HttpResponse<String> authorizationGetGroupRoles() throws UnirestException, JSONException, InterruptedException, ExecutionException{  
  return Unirest.get(prop.getProperty("authorizationExtension").trim()+"/groups/"+getGroupID(getGroups())+"/roles")
          .header("Authorization", "Bearer "+getAuthorizationAccessToken()).asString();
  
  }
   
  
public void authorizationAddUserToGroup(String userId) throws UnirestException, JSONException, InterruptedException, ExecutionException{
String user="auth0%7C"+userId;
Unirest.patch(prop.getProperty("authorizationExtension").trim()+"/users/"+user+"/groups")
          .header("Authorization", "Bearer "+getAuthorizationAccessToken())
          .body("[\""+getGroupID(getGroups())+"\"]").asString();
   }

public void authorizationAddRoleToUser(String userId,String roles) throws UnirestException, JSONException, InterruptedException, ExecutionException{
String user="auth0%7C"+userId;
System.out.println(user);
System.out.println(roles);
HttpResponse<String> rsp=Unirest.patch(prop.getProperty("authorizationExtension").trim()+"/users/"+user+"/roles")
          .header("Authorization", "Bearer "+getAuthorizationAccessToken())
          .body("[\""+roles+"\"]").asString();
//
System.out.println(rsp.getHeaders());
}

public HttpResponse<String> authorizationGetRoles() throws UnirestException, JSONException, InterruptedException, ExecutionException{

return Unirest.get(prop.getProperty("authorizationExtension").trim()+"/roles")
          .header("Authorization", "Bearer "+getAuthorizationAccessToken()).asString();


}
 
   public String getAuthorizationAccessToken() throws UnirestException, JSONException{
  HttpResponse<String> res= authorizationAccessToken(); 
  return getField(res,"access_token");
  }

   public JSONObject getGroups() throws UnirestException, JSONException, InterruptedException, ExecutionException{ 
     return new JSONObject(authorizationGetGroups().getBody());
        
  }
   
   public String getGroupID(JSONObject jsonObject) throws JSONException{
      
      JSONArray jsonArray=jsonObject.getJSONArray("groups");
      for(int i=0;i<jsonArray.length();i++){
          if(jsonArray.getJSONObject(i).get("name").equals(prop.getProperty("groupName").trim()))
            return jsonArray.getJSONObject(i).get("_id").toString();
      }
      return null;
  
  }
   
  public String getField(HttpResponse<String> rsp,String field) throws JSONException{
       JSONObject json = new JSONObject(rsp.getBody());
       return (String) json.get(field);  
   }
  
  public List<String> getSignUpRolesId(UserDTO user,HttpResponse<String> rsp) throws UnirestException, JSONException, InterruptedException, ExecutionException{
  Iterator<String> it = user.getRoles().iterator();
  List<String> list = new ArrayList<>();
  String roles = rsp.getBody();
  while(it.hasNext())
    list.add(getRoleByName(it.next(),roles));
  return list;
  }
  public String getRoleByName(String name,String roles) throws UnirestException, JSONException, InterruptedException, ExecutionException{ 
  JSONObject json=new JSONObject(roles);
  JSONArray jsonArray = json.getJSONArray("roles");
  for(int i=0;i<jsonArray.length();i++){
     if(jsonArray.getJSONObject(i).get("name").equals(name))
         return (String) jsonArray.getJSONObject(i).get("_id");
     }
  return null;
  }
 
}

/*new Callback<String>(){
    @Override
    public void completed(HttpResponse<String> hr) {
         int code = hr.getCode();
         Map<String, String> headers = hr.getHeaders();
         String body = hr.getBody();
         InputStream rawBody = hr.getRawBody(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void failed(UnirestException ue) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void cancelled() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
          
          }*/
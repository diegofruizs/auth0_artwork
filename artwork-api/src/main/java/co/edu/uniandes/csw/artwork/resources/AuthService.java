
/*
The MIT License (MIT)

Copyright (c) 2015 Los Andes University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package co.edu.uniandes.csw.artwork.resources;
import co.edu.uniandes.csw.artwork.auth.config.AuthenticationApi;
import co.edu.uniandes.csw.artwork.auth.config.AuthorizationApi;
import co.edu.uniandes.csw.artwork.dtos.minimum.UserDTO;
import co.edu.uniandes.csw.auth.provider.StatusCreated;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.HttpRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.servlet.http.Cookie;
import javax.ws.rs.core.MediaType;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONArray;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthService {
   
    @Context
    private HttpServletResponse rsp;
    
    @Context
    private HttpServletRequest req;
    
    private final  AuthenticationApi auth;
     private final  AuthorizationApi authorization;
    
   
    public AuthService() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
        this.auth = new AuthenticationApi();
        this.authorization = new AuthorizationApi();       
    }
    
    @Path("/login") 
    @POST
    public UserDTO login(UserDTO user) throws UnirestException, JSONException, IOException, InterruptedException, ExecutionException{
    String str = auth.getSubject(user,rsp);
    List<String> permissions=null;
    HttpResponse<String> rp=authorization.authorizationGetUserRoles(str);
    permissions = authorization.getPermissionsPerRole(authorization.getRolesIDPerUser(rp));
    user.setRoles(authorization.getRoles(new JSONArray(rp.getBody())));
    user.setPermissions(permissions);
    System.out.println(user);
       return user;
    }
    
    @Path("/logout")
    @GET 
    public void logout() {   
       auth.authenticationLogout();
    } 
    
    @Path("/register")
    @POST
    @StatusCreated
    public void register(UserDTO user) throws UnirestException, JSONException, IOException, InterruptedException, ExecutionException {
     
    HttpResponse<String> rs=auth.authenticationSignUP(user);
    JSONObject json=new JSONObject(rs.getBody());
    String str = (String)json.get("_id");
    authorization.authorizationAddUserToGroup(str);
    HttpResponse<String>  response = authorization.authorizationGetRoles();
    List<String> list = authorization.getSignUpRolesId(user, response);
    Iterator<String> it = list.iterator();
    while(it.hasNext()){
        authorization.authorizationAddRoleToUser(str, it.next());
    }
    auth.HttpServletResponseBinder(rs,rsp);  
    }
    
    @Path("/me")
    @GET
    public UserDTO getCurrentUser() throws JSONException, UnirestException, IOException, InterruptedException, ExecutionException {    
    
     Jws<Claims> claim = auth.decryptToken(req);
     String subject="";
     List<String> roles=null;
     List<String> permissions=null;
     if(claim!=null){
         subject = claim.getBody().getSubject();
     HttpResponse<String> resp= auth.managementGetUser(subject);
     JSONObject json = new JSONObject(resp.getBody());
     UserDTO user = new UserDTO(json.getJSONObject("user_metadata"));
     HttpResponse<String> rp = authorization.authorizationGetUserRoles(subject); 
     permissions = authorization.getPermissionsPerRole(authorization.getRolesIDPerUser(rp));
     roles = authorization.getRoles(new JSONArray(rp.getBody()));
     user.setRoles(roles);
     user.setPermissions(permissions);
     
     return user;
     }
     return null;  
    }
   
}
 

    
 

    
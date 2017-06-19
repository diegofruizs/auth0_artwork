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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
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

  private Properties prop = new Properties();
  private InputStream input = null;
  private String path;

  public AuthenticationApi() throws IOException {
    path = this.getClass().getProtectionDomain()
            .getCodeSource().getLocation().toString()
            .split("target")[0].substring(6)
            .concat("src/main/java/co/edu/uniandes/csw"
                    + "/artwork/auth/properties/authentication.properties");
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

  public static HttpRequestWithBody mainPostHeaders(String domain) {
    return Unirest.post(domain)
            .header("content-type", "application/json");
  }
  // obtiene id token para autenticacion

  public HttpResponse<String> setBodyIdToken(HttpRequestWithBody headers, UserDTO dto) throws UnirestException, JSONException {
    return headers.body("{\"client_id\":\"" + prop.getProperty("clientId").trim() + "\","
            + "\"username\":\"" + dto.getUserName() + "\","
            + "\"grant_type\":\"password\","
            + "\"password\":\"" + dto.getPassword() + "\","
            + "\"connection\":\"" + prop.getProperty("connection").trim() + "\","
            + "\"scope\":\"" + prop.getProperty("scope").trim() + "\"}").asString();
  }

  public HttpResponse<String> setBodySignUP(HttpRequestWithBody headers, UserDTO dto) throws UnirestException {
    return headers.body("{\"client_id\":\"" + prop.getProperty("clientId").trim() + "\","
            + "\"email\":\"" + dto.getEmail() + "\","
            + "\"password\":\"" + dto.getPassword() + "\","
            + "\"connection\":\"" + prop.getProperty("connection").trim() + "\","
            + "\"user_metadata\":{\"given_name\":\"" + dto.getGivenName() + "\","
            + "\"email\":\"" + dto.getEmail() + "\","
            + "\"username\":\"" + dto.getUserName() + "\","
            + "\"middle_name\":\"" + dto.getMiddleName() + "\","
            + "\"sur_name\":\"" + dto.getSurName() + "\","
            + "\"roles\":\"" + dto.getRoles().toString().replace(" ", "") + "\"}}").asString();
  }

  public static HttpResponse<String> setBodyUserProfile(HttpRequestWithBody headers, UserDTO dto, String token) throws UnirestException {
    return headers.body("{\"id_token\":\"" + token + "\"}").asString();
  }

  public static HttpResponse<String> setBodyUserProfile(HttpRequestWithBody headers, String token) throws UnirestException {
    return headers.body("{\"id_token\":\"" + token + "\"}").asString();
  }
  //Account methods

  public void signUp(UserDTO dto, HttpServletResponse res) throws UnirestException, JSONException, IOException {
    HttpServletResponseBinder(setBodySignUP(mainPostHeaders(prop.getProperty("signUp").trim()), dto), res);
  }
  //authenticate and retrieves user profile

  public UserDTO login(UserDTO dto, HttpServletResponse res) throws UnirestException, JSONException, IOException {
    HttpResponse<String> rsp = setBodyIdToken(mainPostHeaders(prop.getProperty("idTokenResource").trim()), dto);
    if (!getField(rsp, "error").contains("access_denied")) {
      String id_token = getField(rsp, "id_token");
      rsp = setBodyUserProfile(mainPostHeaders(prop.getProperty("tokenInfo").trim()), dto, id_token);
      res.addHeader("id_token", id_token);
      JSONObject json = getUserMetadata(rsp);
      return new UserDTO(json);
    }
    throw new RuntimeException("No se pudo realizar la autenticacion correctamente");
  }

  public void logout() {
    Unirest.get(prop.getProperty("logout").trim());
  }

  public UserDTO getCurrentUser(HttpServletRequest req) throws UnirestException, JSONException, IOException {
    HttpResponse<String> res = setBodyUserProfile(mainPostHeaders(prop.getProperty("tokenInfo").trim()), getCookieVal(req.getCookies(), "id_token"));
    if (res.getCode() == 200) {
      JSONObject json = getUserMetadata(res);
      return new UserDTO(json);
    }
    return null;
  }

  //response binder
  public void HttpServletResponseBinder(HttpResponse<String> rsp, HttpServletResponse res) throws IOException {
    res.setHeader("content-type", "application/json");
    res.setStatus(rsp.getCode());
    res.getWriter().print(rsp.getBody());
    res.flushBuffer();
  }
  // json tasks

  public String getField(HttpResponse<String> rsp, String field) throws JSONException {
    JSONObject json = new JSONObject(rsp.getBody());
    return json.get(field).toString();
  }

  public JSONObject getUserMetadata(HttpResponse<String> rsp) throws JSONException {
    JSONObject json = new JSONObject(rsp.getBody());
    return (JSONObject) json.get("user_metadata");
  }

  private String getCookieVal(Cookie[] cookies, String name) {
    for (Cookie c : cookies) {
      if (name.equals(c.getName())) {
        return c.getValue();
      }
    }
    return null;
  }

  public String getToken(UserDTO dto) throws UnirestException, JSONException {
    HttpResponse<String> res = setBodyIdToken(mainPostHeaders(prop.getProperty("idTokenResource").trim()), dto);

    return getField(res, "id_token");
  }

  public String login(UserDTO dto) throws UnirestException, JSONException, IOException {
    HttpResponse<String> rsp = setBodyIdToken(mainPostHeaders(prop.getProperty("idTokenResource").trim()), dto);
    String id_token = getField(rsp, "id_token");
    rsp = setBodyUserProfile(mainPostHeaders(prop.getProperty("tokenInfo").trim()), dto, id_token);

    JSONObject json = getUserMetadata(rsp);

    return json.get("roles").toString();
  }
}

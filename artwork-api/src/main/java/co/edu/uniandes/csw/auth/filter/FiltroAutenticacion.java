package co.edu.uniandes.csw.auth.filter;

import co.edu.uniandes.csw.artwork.auth.config.AuthenticationApi;
import co.edu.uniandes.csw.artwork.auth.config.AuthorizationApi;
import co.edu.uniandes.csw.artwork.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.auth.provider.WebApplicationExceptionMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Arrays;
import io.jsonwebtoken.lang.Collections;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.EJB;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.xml.bind.DatatypeConverter;
import org.json.JSONException;

/**
 * Este filtro procesa las excepciones que se arrojan hacia arriba y generan un
 * mecanismo estandar de salida de la excepci&oacute;n
 *
 * @generated
 */

@WebFilter(filterName = "FiltroAutenticacion", urlPatterns = {"/api/*"})
public class FiltroAutenticacion implements Filter {
    private   AuthenticationApi auth;
    private   AuthorizationApi authorization;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {

  }

  /**
   * procesa las excepciones y las arroja hacia la capa superior
   */

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            this.auth = new AuthenticationApi();
            this.authorization = new AuthorizationApi();
        } catch (UnirestException | JSONException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(FiltroAutenticacion.class.getName()).log(Level.SEVERE, null, ex);
        }
      PropertiesLoader prop = new PropertiesLoader();
      HttpResponse<String> rp=null;
      String usuario = null, jwt = null, path=null, resource=null, subject=null;   
      Jws<Claims> claim = null;
      List<String> permissions=null;
      path = ((HttpServletRequest) request).getRequestURI().substring(((HttpServletRequest) request).getContextPath().length()).replace("[/]+$", "");
      path = String.join("/", Arrays.copyOfRange(path.split("/"), 0, 3)).trim();
      resource = path.split("/")[2];
      boolean allowedPath = prop.containsKey(path);
      Cookie[] cookie = ((HttpServletRequest) request).getCookies();
     
      for (Cookie c : cookie) {
        if ("id_token".equals(c.getName())) {
          jwt = c.getValue();
        }
        if ("username".equals(c.getName())) {
          usuario = c.getValue();
        }
      }  
      
   try {
      if(usuario != null && jwt != null ){
  claim =  auth.decryptToken((HttpServletRequest)request);
  subject = claim.getBody().getSubject();
  rp = authorization.authorizationGetUserRoles(subject);
  permissions = authorization.getPermissionsPerRole(authorization.getRolesIDPerUser(rp));
      }else{
          if(!allowedPath & !"users".equals(resource))
        throw new SignatureException("No autenticado");
      }         
   } catch (SignatureException e) {
        errorResponse(403,"Usuario no autenticado",(HttpServletResponse)response);
      ((HttpServletResponse)response).sendRedirect("http://localhost:8080"+((HttpServletRequest)request).getContextPath()+"/api/users/me");
   }    catch (UnirestException | JSONException | InterruptedException | ExecutionException ex) {
            Logger.getLogger(FiltroAutenticacion.class.getName()).log(Level.SEVERE, null, ex);
        }
       if(permissions != null & !"users".equals(resource) & !allowedPath){
       if(!permissions.contains(methodMapper(((HttpServletRequest)request).getMethod()).concat(":").concat(resource)))
       errorResponse(405,"no existe permiso "+methodMapper(((HttpServletRequest)request).getMethod()).concat(":").concat(resource),(HttpServletResponse) response);
           }    
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    //no realiza ninguna accion
  }

  public static void errorResponse(Integer code, String msg, HttpServletResponse response) throws IOException {
    response.setStatus(code);
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(msg);
    response.flushBuffer();
  }
public static String methodMapper(String s){
      String str;
   switch(s){
       case "POST": 
       str="create";
       break;
       case "GET": 
       str="read";
       break;
       case "PUT": 
       str="update";
       break;
       case "DELETE": 
       str="delete";
       break;
       default:
           str="";
   }
   return str;
  }
}
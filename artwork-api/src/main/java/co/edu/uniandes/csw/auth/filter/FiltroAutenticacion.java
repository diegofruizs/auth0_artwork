package co.edu.uniandes.csw.auth.filter;

import co.edu.uniandes.csw.artwork.exceptions.BusinessLogicException;
import co.edu.uniandes.csw.auth.provider.WebApplicationExceptionMapper;
import io.jsonwebtoken.Claims;
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

/**
 * Este filtro procesa las excepciones que se arrojan hacia arriba y generan un
 * mecanismo estandar de salida de la excepci&oacute;n
 *
 * @generated
 */ 
@WebFilter(filterName = "FiltroAutenticacion", urlPatterns = {"/api/*"})
public class FiltroAutenticacion implements Filter {
    
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    
  }

  /**
   * procesa las excepciones y las arroja hacia la capa superior
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
  WebApplicationExceptionMapper mapper = new WebApplicationExceptionMapper();
  String usuario=null,jwt=null,secret,path,host;
  List<String> roles=new ArrayList<>();
  List<String> methods=new ArrayList<>();
  PropertiesLoader prop = new PropertiesLoader();
  secret = prop.getPropertyAsString("secretKey");
  path = ((HttpServletRequest) request).getRequestURI().substring(((HttpServletRequest) request).getContextPath().length()).replace("[/]+$", ""); 
  path= String.join("/",Arrays.copyOfRange(path.split("/"), 0, 3));
  boolean allowedPath = prop.containsKey(path);
  boolean allowedPathByRole =  prop.containsRole(path);
  String resource= path.split("/")[2];
  host=prop.getPropertyAsString("host");
  SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;
  Key signingKey = new SecretKeySpec(secret.getBytes(), signatureAlgorithm.getJcaName());
  Cookie[] cookie=((HttpServletRequest) request).getCookies();
  
  for(Cookie c:cookie){
      if("id_token".equals(c.getName()))
          jwt = c.getValue();
      if("username".equals(c.getName()))
          usuario = c.getValue();
      if("roles".equals(c.getName())){
          if(c.getValue().contains("%2C"))
              roles = Collections.arrayToList(c.getValue().split("%2C"));
          else
              roles.add(c.getValue());
      }
  }
 
  try {
      if(usuario != null && jwt != null )
        Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);  
      else
          if(!allowedPath)
              throw new SignatureException("No autenticado");
      //OK, we can trust this JWT
    } catch (SignatureException e) {
        errorResponse(403,"Usuario no autenticado",(HttpServletResponse)response);
       ((HttpServletResponse)response).sendRedirect(host+((HttpServletRequest)request).getContextPath()+"/api/users/me");
        return;
    }
 /* 
  if(allowedPath){
      if(allowedPathByRole & !roles.contains(prop.getRole(path))){
            errorResponse(405,"El recurso "+resource+" no esta permitido para el rol "+prop.getRole(path),(HttpServletResponse)response);   
     return;
      }
  }else{
      errorResponse(405,"No existen permisos para recurso "+resource,(HttpServletResponse)response); 
      return;
  }  
  
  if(allowedPath){
   if(allowedPathByRole & roles.contains(prop.getRole(path))){
       if(prop.isMethodList(path))
           methods = prop.getMethodsAsList(path);
       else
           methods.add(prop.getMethodsAsString(path));
    
       if(!methods.contains(prop.methodMapper(((HttpServletRequest)request).getMethod()))){
          errorResponse(405,prop.methodMapper(((HttpServletRequest)request).getMethod())+" no esta permitido para recurso "+resource,(HttpServletResponse)response);            
          return;
       }
}
  }
  */
     
    chain.doFilter(request, response);
  }

  @Override
  public void destroy() {
    //no realiza ninguna accion
  } 

   public static void errorResponse(Integer code,String msg,HttpServletResponse response) throws IOException{
    response.setStatus(code);
    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(msg);
    response.flushBuffer();
   }
  
}



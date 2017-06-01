/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.artwork.tests.postman;

import co.edu.uniandes.csw.artwork.dtos.detail.ArtistDetailDTO;
import co.edu.uniandes.csw.artwork.entities.ArtistEntity;
import co.edu.uniandes.csw.artwork.resources.ArtistResource;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Asistente
 */
@RunWith(Arquillian.class)
public class ArtistIT {
   String path= this.getClass().getProtectionDomain()
             .getCodeSource().getLocation().toString()
             .split("target")[0].substring(6)
             .concat("run.bat");
   PodamFactory factory = new PodamFactoryImpl();
   JSONParser parser = new JSONParser();
   Gson gson = new Gson();
 

   

    private final static List<ArtistEntity> oraculo = new ArrayList<>();

    private final String artistPath = "artists";
    
    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class,"artwork-api.war")
                // Se agrega las dependencias
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .importRuntimeDependencies().resolve()
                        .withTransitivity().asFile())
                // Se agregan los compilados de los paquetes de servicios
                .addPackage(ArtistResource.class.getPackage())
                .addPackage("co.edu.uniandes.csw.auth.filter")
                // El archivo que contiene la configuracion a la base de datos.
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // El archivo beans.xml es necesario para injeccion de dependencias.
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                // El archivo web.xml es necesario para el despliegue de los servlets
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
                 
    }   
    

public void setPostmanCollectionValues(String action) throws FileNotFoundException, IOException, ParseException{
 
    FileReader reader = new FileReader(CollectionPrepare.getPATH());
    Object obj = parser.parse(reader);
    reader.close();
    FileWriter writer = new FileWriter(CollectionPrepare.getPATH());
    ArtistDetailDTO artist = factory.manufacturePojo(ArtistDetailDTO.class);
    JsonArray jsonArray=gson.toJsonTree(obj).getAsJsonObject().get("item").getAsJsonArray();
    Integer index= CollectionPrepare.findJsonIndex(jsonArray,action);
    JsonElement jsonElement=gson.toJsonTree(obj);
    CollectionPrepare.setCollectionBody(jsonElement, index, artist, gson);   
    writer.write(jsonElement.toString());
    writer.flush();
    writer.close();
}


    @Test 
    public void postman() throws FileNotFoundException, IOException, ParseException{
        
      setPostmanCollectionValues("create");
      setPostmanCollectionValues("edit");
   try {              
            Process process = Runtime.getRuntime().exec(path);
            InputStream inputStream = process.getInputStream();
            BufferedReader bf= new BufferedReader(new InputStreamReader(inputStream));
            String line="";
            String ln;
            while ((ln=bf.readLine()) != null) {
                line=line.concat(ln+"\n");
            }
              System.out.println(line);   
            inputStream.close();
            bf.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } 
    }
    
}

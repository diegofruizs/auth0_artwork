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
package co.edu.uniandes.csw.artwork.tests.rest;

import co.edu.uniandes.csw.auth.conexions.AuthenticationApi;
import co.edu.uniandes.csw.auth.model.UserDTO;
import co.edu.uniandes.csw.artwork.entities.ArtistEntity;
import co.edu.uniandes.csw.artwork.dtos.detail.ArtistDetailDTO;
import co.edu.uniandes.csw.artwork.dtos.minimum.ArtistDTO;
import co.edu.uniandes.csw.artwork.resources.ArtistResource;
import co.edu.uniandes.csw.auth.service.AuthService;
import co.edu.uniandes.csw.artwork.tests.Utils;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/*
 * Testing URI: artists/
 */
@RunWith(Arquillian.class)
public class ArtistTest {

    private WebTarget target;
    private final String apiPath = Utils.apiPath;
    private final String username = Utils.username;
    private final String password = Utils.password;
    PodamFactory factory = new PodamFactoryImpl();

    private final int OK = Status.OK.getStatusCode();
    private final int Created = Status.CREATED.getStatusCode();
    private final int OK_WITHOUT_CONTENT = Status.NO_CONTENT.getStatusCode();

    private final static List<ArtistEntity> oraculo = new ArrayList<>();

    private final String artistPath = "artists";
    private  AuthenticationApi auth;
    private String roles;
    

    @ArquillianResource
    private URL deploymentURL;

    @Deployment
    public static WebArchive createDeployment() {
        return ShrinkWrap.create(WebArchive.class)
                // Se agrega las dependencias
                .addAsLibraries(Maven.resolver().loadPomFromFile("pom.xml")
                        .importRuntimeDependencies().resolve()
                        .withTransitivity().asFile())
                // Se agregan los compilados de los paquetes de servicios
                .addPackage(ArtistResource.class.getPackage())
                .addPackage("co.edu.uniandes.csw.auth.provider")
                .addPackage("co.edu.uniandes.csw.auth.filter")
                .addPackage("co.edu.uniandes.csw.auth.config")
                // El archivo que contiene la configuracion a la base de datos.
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // El archivo beans.xml es necesario para injeccion de dependencias.
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                
                // El archivo web.xml es necesario para el despliegue de los servlets
                .setWebXML(new File("src/main/webapp/WEB-INF/web.xml"));
    }

   

    private WebTarget createWebTarget() {
        return ClientBuilder.newClient().target(deploymentURL.toString()).path(apiPath);
    }

    @PersistenceContext(unitName = "ArtworkPU")
    private EntityManager em;

    @Inject
    private UserTransaction utx;

    private void clearData() {
        em.createQuery("delete from ArtistEntity").executeUpdate();
        oraculo.clear();
    }

   /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * @generated
     */
    public void insertData() {
        for (int i = 0; i < 3; i++) {            
            ArtistEntity artist = factory.manufacturePojo(ArtistEntity.class);
            artist.setId(i + 1L);
            em.persist(artist);
            oraculo.add(artist);
        }
    }

    /**
     * Configuración inicial de la prueba.
     *
     * @generated
     */
    @Before
    public void setUpTest() {
        try {
            utx.begin();
            clearData();
            insertData();
            utx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                utx.rollback();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        target = createWebTarget()
                .path(artistPath);
    }

    /**
     * Login para poder consultar los diferentes servicios
     *
     * @param username Nombre de usuario
     * @param password Clave del usuario
     * @return Cookie con información de la sesión del usuario
     * @throws java.io.IOException
     * @generated
     */
    
     public String login(String username, String password) throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException { 
        auth=new AuthenticationApi();
        UserDTO user = new UserDTO();
        user.setUserName(username);
        user.setPassword(password);
        JSONObject json = new JSONObject(auth.authenticationToken(user).getBody()); 
        return (String)json.get("id_token");
    }
   
    
    
    /**
     *
     * Prueba para crear un Artist
     *
     * @generated
     */
    @Test
    public void createArtistTest() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
       String token= login("su@su.com","Sunshine3");
        ArtistDetailDTO artist = factory.manufacturePojo(ArtistDetailDTO.class);
        Response response = target
            .request()
                .cookie("username", "su@su.com")
                .cookie("id_token",token)
            .post(Entity.entity(artist, MediaType.APPLICATION_JSON));

        ArtistDetailDTO  artistTest = (ArtistDetailDTO) response.readEntity(ArtistDetailDTO.class);

        Assert.assertEquals(Created, response.getStatus());

        Assert.assertEquals(artist.getName(), artistTest.getName());

        ArtistEntity entity = em.find(ArtistEntity.class, artistTest.getId());
        Assert.assertNotNull(entity);
    }
     @Test
    public void getArtistByIdTest() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
        String token= login("su@su.com","Sunshine3");

        ArtistDTO artistTest = target
            .path(oraculo.get(0).getId().toString())
            .request()
                .cookie("username", "su@su.com")
                .cookie("id_token",token)
                .get(ArtistDTO.class);
        
        Assert.assertEquals(artistTest.getId(), oraculo.get(0).getId());
        Assert.assertEquals(artistTest.getName(), oraculo.get(0).getName());
    }

    /**
     * Prueba para consultar la lista de Artists
     *
     * @generated
     */
    @Test
    public void listArtistTest() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
        String token= login("su@su.com","Sunshine3");

        Response response = target
            .request()
                .cookie("username", "su@su.com")
                .cookie("id_token",token)
                .get();

        String listArtist = response.readEntity(String.class);
        List<ArtistDTO> listArtistTest = new ObjectMapper().readValue(listArtist, List.class);
        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals(3, listArtistTest.size());
    }

    /**
     * Prueba para actualizar un Artist
     *
     * @generated
     */
    @Test
    public void updateArtistTest() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
        String token= login("su@su.com","Sunshine3");
        ArtistDTO artist = new ArtistDTO(oraculo.get(0));

        ArtistDTO artistChanged = factory.manufacturePojo(ArtistDTO.class);

        artist.setName(artistChanged.getName());

        Response response = target
            .path(artist.getId().toString())
            .request()    
            .cookie("username", "su@su.com")
             .cookie("id_token",token)
            .put(Entity.entity(artist, MediaType.APPLICATION_JSON));

        ArtistDTO artistTest = (ArtistDTO) response.readEntity(ArtistDTO.class);

        Assert.assertEquals(OK, response.getStatus());
        Assert.assertEquals(artist.getName(), artistTest.getName());
    }

    /**
     * Prueba para eliminar un Artist
     *
     * @generated
     */
    @Test
    public void deleteArtistTest() throws IOException, UnirestException, JSONException, InterruptedException, ExecutionException {
        String token= login("su@su.com","Sunshine3");
        ArtistDTO artist = new ArtistDTO(oraculo.get(0));
        Response response = target
            .path(artist.getId().toString())
            .request()
             .cookie("username", "su@su.com")
             .cookie("id_token",token).delete();

        Assert.assertEquals(OK_WITHOUT_CONTENT, response.getStatus());
    }
}
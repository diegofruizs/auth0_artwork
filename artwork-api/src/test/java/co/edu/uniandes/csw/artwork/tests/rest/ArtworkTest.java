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

import co.edu.uniandes.csw.auth.model.UserDTO;
import co.edu.uniandes.csw.artwork.entities.ArtworkEntity;
import co.edu.uniandes.csw.artwork.entities.ArtistEntity;
import co.edu.uniandes.csw.artwork.dtos.detail.ArtworkDetailDTO;
import co.edu.uniandes.csw.artwork.resources.ArtworksResource;
import co.edu.uniandes.csw.artwork.tests.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.transaction.UserTransaction;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/*
 * Testing URI: artists/{artworksId: \\d+}/artworks/
 */
@RunWith(Arquillian.class)
public class ArtworkTest {

    private WebTarget target;
    private final String apiPath = Utils.apiPath;
    private final String username = Utils.username;
    private final String password = Utils.password;
    PodamFactory factory = new PodamFactoryImpl();

    private final int Ok = Status.OK.getStatusCode();
    private final int Created = Status.CREATED.getStatusCode();
    private final int OkWithoutContent = Status.NO_CONTENT.getStatusCode();

    private final static List<ArtworkEntity> oraculo = new ArrayList<>();

    private final String artistPath = "artists";
    private final String artworkPath = "artworks";

    ArtistEntity fatherArtistEntity;

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
                .addPackage(ArtworksResource.class.getPackage())
				.addPackage("co.edu.uniandes.csw.auth.provider")
                // El archivo que contiene la configuracion a la base de datos.
                .addAsResource("META-INF/persistence.xml", "META-INF/persistence.xml")
                // El archivo beans.xml es necesario para injeccion de dependencias.
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"))
                // El archivo shiro.ini es necesario para injeccion de dependencias
                .addAsWebInfResource(new File("src/main/webapp/WEB-INF/shiro.ini"))
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
        em.createQuery("delete from ArtworkEntity").executeUpdate();
        em.createQuery("delete from ArtistEntity").executeUpdate();
        oraculo.clear();
    }

   /**
     * Datos iniciales para el correcto funcionamiento de las pruebas.
     *
     * @generated
     */
    public void insertData() {
        fatherArtistEntity = factory.manufacturePojo(ArtistEntity.class);
        fatherArtistEntity.setId(1L);
        em.persist(fatherArtistEntity);

        for (int i = 0; i < 3; i++) {            
            ArtworkEntity artwork = factory.manufacturePojo(ArtworkEntity.class);
            artwork.setId(i + 1L);
            artwork.setArtist(fatherArtistEntity);
            em.persist(artwork);
            oraculo.add(artwork);
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
                .path(artistPath)
                .path(fatherArtistEntity.getId().toString())
                .path(artworkPath);
    }

    /**
     * Login para poder consultar los diferentes servicios
     *
     * @param username Nombre de usuario
     * @param password Clave del usuario
     * @return Cookie con información de la sesión del usuario
     * @generated
     */
    /**
     * Prueba para crear un Artwork
     *
     * @generated
     */
    @Test
    public void createArtworkTest() throws IOException {
        ArtworkDetailDTO artwork = factory.manufacturePojo(ArtworkDetailDTO.class);
        Response response = target
            .request()
            .post(Entity.entity(artwork, MediaType.APPLICATION_JSON));

        ArtworkDetailDTO  artworkTest = (ArtworkDetailDTO) response.readEntity(ArtworkDetailDTO.class);

        Assert.assertEquals(Created, response.getStatus());

        Assert.assertEquals(artwork.getName(), artworkTest.getName());
        Assert.assertEquals(artwork.getImage(), artworkTest.getImage());
        Assert.assertEquals(artwork.getPrice(), artworkTest.getPrice());

        ArtworkEntity entity = em.find(ArtworkEntity.class, artworkTest.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Prueba para consultar un Artwork
     *
     * @generated
     */
    @Test
    public void getArtworkByIdTest() {

        ArtworkDetailDTO artworkTest = target
            .path(oraculo.get(0).getId().toString())
            .request().get(ArtworkDetailDTO.class);
        
        Assert.assertEquals(artworkTest.getId(), oraculo.get(0).getId());
        Assert.assertEquals(artworkTest.getName(), oraculo.get(0).getName());
        Assert.assertEquals(artworkTest.getImage(), oraculo.get(0).getImage());
        Assert.assertEquals(artworkTest.getPrice(), oraculo.get(0).getPrice());
    }

    /**
     * Prueba para consultar la lista de Artworks
     *
     * @generated
     */
    @Test
    public void listArtworkTest() throws IOException {
        Response response = target
            .request().get();

        String listArtwork = response.readEntity(String.class);
        List<ArtworkDetailDTO> listArtworkTest = new ObjectMapper().readValue(listArtwork, List.class);
        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(oraculo.size(), listArtworkTest.size());
    }

    /**
     * Prueba para actualizar un Artwork
     *
     * @generated
     */
    @Test
    public void updateArtworkTest() throws IOException {
        ArtworkDetailDTO artwork = new ArtworkDetailDTO(oraculo.get(0));

        ArtworkDetailDTO artworkChanged = factory.manufacturePojo(ArtworkDetailDTO.class);

        artwork.setName(artworkChanged.getName());
        artwork.setImage(artworkChanged.getImage());
        artwork.setPrice(artworkChanged.getPrice());

        Response response = target
            .path(artwork.getId().toString())
            .request()
            .put(Entity.entity(artwork, MediaType.APPLICATION_JSON));

        ArtworkDetailDTO artworkTest = (ArtworkDetailDTO) response.readEntity(ArtworkDetailDTO.class);

        Assert.assertEquals(Ok, response.getStatus());
        Assert.assertEquals(artwork.getName(), artworkTest.getName());
        Assert.assertEquals(artwork.getImage(), artworkTest.getImage());
        Assert.assertEquals(artwork.getPrice(), artworkTest.getPrice());
    }

    /**
     * Prueba para eliminar un Artwork
     *
     * @generated
     */
    @Test
    public void deleteArtworkTest() {
        ArtworkDetailDTO artwork = new ArtworkDetailDTO(oraculo.get(0));
        Response response = target
            .path(artwork.getId().toString())
            .request().delete();

        Assert.assertEquals(OkWithoutContent, response.getStatus());
    }
}

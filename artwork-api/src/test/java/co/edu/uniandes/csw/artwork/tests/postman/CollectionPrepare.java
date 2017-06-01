/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.uniandes.csw.artwork.tests.postman;

import co.edu.uniandes.csw.artwork.dtos.detail.ArtistDetailDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Iterator;
/**
 *
 * @author Asistente
 */
public class CollectionPrepare  {
    
   private static final String PATH = "/Users/Asistente/Documents/crud_artwork.postman_collection.json";
   

 public static Integer findJsonIndex(JsonArray jarray,String action){
     Iterator<JsonElement> it=jarray.iterator();
     int count =0;
    while(it.hasNext()){
       if(it.next().getAsJsonObject().get("name").getAsString().contains(action))
          break;
       count++;
           }
    return count;
 }
public static void setCollectionBody(JsonElement jsonElement,Integer index,ArtistDetailDTO artist,Gson gson){
    
    String val="{\"name\":\""+artist.getName()+"\",\"id\":\""+artist.getId()+"\"}";
   
jsonElement.getAsJsonObject().get("item")
      .getAsJsonArray().get(index)
      .getAsJsonObject().get("request")
      .getAsJsonObject().get("body")
      .getAsJsonObject().addProperty("raw", val);
}

    /**
     * @return the PATH
     */
    public static String getPATH() {
        return PATH;
    }
    
  
}
      
  
  
    

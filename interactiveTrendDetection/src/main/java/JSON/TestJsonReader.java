package JSON;


import java.util.Scanner;
import java.io.*;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.FileReader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

public class TestJsonReader {
	
	 /**
     * @param args the command line arguments
     */
    public static void main(String[] args)throws Exception {
    	
    	String url = "smb://147.172.84.20/iw/korpora/TestSamples/";
    	NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(null, "fadaei", "noushin2016");
    	SmbFile dir = new SmbFile(url, auth);
    	
    	
    	String currentLine;
    	
    	for (SmbFile f : dir.listFiles())
    	{
    		BufferedReader br = new BufferedReader(new FileReader("\\\\147.172.84.20\\iw\\korpora\\TestSamples\\"+f.getName()));
    		
            while ((currentLine = br.readLine())!= null) {
            	if(currentLine.contains("_type")) continue;
            	if(currentLine.equals("")) break;
                //System.out.println("Record:\t" + currentLine);
                
                JsonElement jelement = new JsonParser().parse(currentLine);
            	String patenLanguage=getDETDL(jelement);
        	    	if(patenLanguage.equals("\"en\"")){
        	    		String patenDETD=getEPFULLDetailedDescription(jelement);
        	    		System.out.println(patenDETD);
        	    	}
            }

    	 }
    	
    }
    
//Finding a pack of terms for the criterion "sensitive terms" (coming from WordNet Class)
    

//Extract important sections (sentences from patents)
   

    
//Stopword removal
    
    
//Making vectors out of the keywords
    
 static public String getIndex(JsonElement jelement){
	 String result;
        
        JsonObject  jobject = jelement.getAsJsonObject();
        jobject = jobject.getAsJsonObject();
        JsonPrimitive tsPrimitive = jobject.getAsJsonPrimitive("_index");
        //result=tsPrimitive.getAsString();
        result = tsPrimitive.toString();
       
        return result;
    }
    
    static public String getClaim(JsonElement jelement){
    
    JsonObject  jobject = jelement.getAsJsonObject();
    jobject = jobject.getAsJsonObject("fields");
    JsonArray jarray = jobject.getAsJsonArray("CLMEN");//claim, title, patent number are stored in the form of lists; however this list contains only one member 
    String result = jarray.get(0).toString();//the only member of the list is stored in the form of String 
    return result;
}
    static public String getTitle(JsonElement jelement){
    JsonObject  jobject = jelement.getAsJsonObject();
    JsonObject  jobject1 = jobject.getAsJsonObject("fields");
    JsonArray jarray = jobject1.getAsJsonArray("TIEN");
    String result = jarray.get(0).toString();
    return result;
}
    static public String getEPFULLTitle(JsonElement jelement){
    	String result="null";
    	
    	JsonObject  jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject();
        JsonElement jse=jobject.get("TIEN");
        
        if(!jse.isJsonNull()){
        
        JsonPrimitive tsPrimitive = jobject.getAsJsonPrimitive("TIEN");
        result=tsPrimitive.getAsString();
        result = tsPrimitive.toString();
        
        }
        
        return result;
    }
    static public String getEPFULLDetailedDescription(JsonElement jelement){
    	String result="null";
    	
    	JsonObject  jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject();
        JsonElement jse=jobject.get("DETD");
        
        if(!jse.isJsonNull()){
        
        JsonPrimitive tsPrimitive = jobject.getAsJsonPrimitive("DETD");
        result=tsPrimitive.getAsString();
        result = tsPrimitive.toString();
        
        }
        
        return result;
    }
    
    static public String getDETDL(JsonElement jelement){
        String result="null";
    	
    	JsonObject  jobject = jelement.getAsJsonObject();
        //jobject = jobject.getAsJsonObject();
        JsonElement jse=jobject.get("DETDL");
        
        if(!jse.isJsonNull()){
        
        JsonPrimitive tsPrimitive = jobject.getAsJsonPrimitive("DETDL");
        result=tsPrimitive.getAsString();
        result = tsPrimitive.toString();
        
        }
        
        return result;
    }
    
}

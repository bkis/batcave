package de.uni.koeln.spinfo.bkiss.batcave.db.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.bson.Document;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * Class used to transform data from the ARC project db into a new purpose-built db structure
 * 
 * @author kiss
 *
 */
public class DbTransform {

	public static void main(String[] args) {
		
		//load db access properties
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		Properties props = new Properties();
		try(InputStream resourceStream = loader.getResourceAsStream("db.properties")) {
		    props.load(resourceStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//init mongo client
		String host = props.getProperty("host");
		int port = Integer.valueOf(props.getProperty("port"));
		MongoClient mongo = new MongoClient(host, port);
		
		//start transformation
		try {
			transform(
					mongo.getDatabase("crestomazia"),
					mongo.getDatabase("batcave")
			);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mongo.close();
		}
		
		//cleanup
		mongo.close();
	}
	
	
	private static void transform(MongoDatabase s, MongoDatabase t) throws Exception {
		//get ARC collections
		MongoCursor<Document> words = s.getCollection("words").find().iterator();
		
		int count = 0;
		
		try {
		    while (words.hasNext() && count < 100) {
		    	String json = words.next().toJson();
		        System.out.println((String)JsonPath.read(json, "$.versions[0].version"));
		        count++;
		    }
		} finally {
		    words.close();
		}
	}

	
}

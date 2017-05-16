package de.uni.koeln.spinfo.bkiss.batcave.db.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.bson.Document;

import com.jayway.jsonpath.JsonPath;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;

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
		//tokens list
		Set<PageDocument> targetPages = new HashSet<PageDocument>();
		//get ARC words collection interator
		MongoCursor<Document> words = s.getCollection("words").find().iterator();
		//get ARC pages, chapters, volumes and languages Collections
		MongoCollection<Document> pages = s.getCollection("pages");
		MongoCollection<Document> chapters = s.getCollection("chapters");
		MongoCollection<Document> volumes = s.getCollection("volumes");
		MongoCollection<Document> languages = s.getCollection("languages");
		//collect token data and generate PageDocument instances
		try {
		    while (words.hasNext()) {
		    	String json = words.next().toJson();
		        System.out.println((String)JsonPath.read(json, "$.versions[0].version"));
		    }
		} finally {
		    words.close();
		}
	}

	
}

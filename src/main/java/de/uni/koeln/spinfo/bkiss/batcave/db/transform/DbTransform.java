package de.uni.koeln.spinfo.bkiss.batcave.db.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Configuration.ConfigurationBuilder;
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
		
		//set logging
		Logger.getLogger("org.mongodb.driver.protocol.command").setLevel(Level.SEVERE);
		Logger.getLogger("com.jayway.jsonpath.internal.path.CompiledPath").setLevel(Level.SEVERE);
		
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
		int count = 0;
		try {
		    while (words.hasNext() && count < 10) {
		    	ReadContext json = JsonPath.parse(words.next().toJson());	//get json object
		    	//TODO iterate words, construct pagedocuments
		        System.out.println((Object)json.read("$.versions"));
		        count++;
		    }
		} finally {
		    words.close();
		}
		
	}
	
	
}

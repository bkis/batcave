package de.uni.koeln.spinfo.bkiss.batcave.db.transform;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.types.ObjectId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.ScanPosition;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;
import net.minidev.json.JSONArray;


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
		
		//create list for PageDocuments
		List<PageDocument> pages = null;
		
		//start transformation
		try {
			//TODO run without test-mode
			pages = transform(
					mongo.getDatabase("crestomazia"),
					mongo.getDatabase("batcave"),
					true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//write collected data to target db
		writeNewDbData(pages, mongo.getDatabase("batcave").getCollection("pages"));
		
		//cleanup
		mongo.close();
	}
	
	
	@SuppressWarnings("unchecked")
	private static List<PageDocument> transform(MongoDatabase s, MongoDatabase t, boolean testRun) throws Exception {
		
		//target pages set
		Map<String, PageDocument> targetPages = new HashMap<String, PageDocument>();
		
		//get ARC words collection interator
		MongoCursor<Document> words = s.getCollection("words").find().iterator();
		
		//get ARC pages, chapters, volumes and languages Collections
		MongoCollection<Document> pages = s.getCollection("pages");
		MongoCollection<Document> chapters = s.getCollection("chapters");
		MongoCollection<Document> volumes = s.getCollection("volumes");
		MongoCollection<Document> languages = s.getCollection("languages");
		
		//words count
		double wordsCount = s.getCollection("words").count();
		double progress = 0;
		
		//jsonpath config
		Configuration conf = Configuration
	              .builder()
	              .options(Option.DEFAULT_PATH_LEAF_TO_NULL)
	              .build();
		
		int count = 0;
		
		//collect token data and generate Token instances
		try {
		    while (words.hasNext() && count < 1000) {
		    	ReadContext json = JsonPath.using(conf).parse(words.next().toJson());	//get json object
		    	
		    	////gather data
		    	//index
		    	int index = json.read("$.index");
		    	//page id
		    	String pageId = json.read("$.pageId");
		    	//volume id
		    	String volumeId = json.read("$.volumeId");
		    	//image file
		    	String imageFile = ((String)pages.find(new BasicDBObject("_id", new ObjectId(pageId))).first().get("url")).replaceFirst("\\.xml", ".png");
		    	//form
		    	String form;
		    	if (((JSONArray)json.read("$.versions[?(@.userId != 'OCR')].version")).size() == 0){
		    		form = json.read("$.versions[0].version");
		    	} else {
		    		form = (String)((JSONArray)json.read("$.versions[?(@.userId != 'OCR')].version")).get(0);
		    	}
		    	//form == null?
		    	if (form == null){
		    		continue;
		    	}
		    	//scan positions
		    	int x = json.read("$.rectangle.x");
		    	int y = json.read("$.rectangle.y");
		    	int width = json.read("$.rectangle.width");
		    	int height = json.read("$.rectangle.height");
		    	ScanPosition pos = new ScanPosition(x, y, width, height);
		    	
		    	//tags
		    	Set<String> tags = new HashSet<String>();
		    	if (((JSONArray)json.read("$.posList[?(@.userId != 'matcher')].posTag")).size() == 0){
		    		tags.addAll((List<String>)json.read("$.posList[*].posTag"));
		    	} else {
		    		tags.addAll((List<String>)json.read("$.posList[?(@.userId != 'matcher' && @.posTag != 'NOT_TAGGED') ].posTag"));
		    	}
		    	
		    	//construct Token instance
		    	Token token = new Token(form, index, pos, tags);

		    	//new PageDocument instance to construct
		    	PageDocument page;
		    	if (targetPages.containsKey(pageId)){
		    		page = targetPages.get(pageId);
		    	} else {
		    		page = new PageDocument(pageId);
		    		targetPages.put(pageId, page);
		    		page.setVolume(volumes.find(new BasicDBObject("_id", new ObjectId(volumeId))).first().getString("title"));
		    		page.setImageFile(imageFile);
		    	}
		    	
		    	////add data to page
		    	//token
		    	page.addToken(token);
		    	//chapter
		    	BasicDBObject query = new BasicDBObject();
		    	query.append("start", new BasicDBObject("$lte", index));
		    	query.append("end", new BasicDBObject("$gte", index));
	    		Document chapter = chapters.find(query).first();
	    		if (chapter != null) page.addChapter(chapter.getString("title"));
	    		//language
	    		Document language = languages.find(query).first();
	    		if (language != null)
	    			page.addLanguage(language.getString("title"));

	    		//test run counter
	    		if (testRun)
	    			count++;
	    		
	    		//check if page is complete - if so, write to target db
		    	int pageEndIndex = ((int)pages.find(new BasicDBObject("_id", new ObjectId(pageId))).first().get("end"));
		    	if (pageEndIndex == index){
		    		writeNewDbData(page, t.getCollection("pages"));
		    		targetPages.remove(pageId);
		    	}
		    	
	    		progress++;
	    		System.out.println("[PROGRESS]\t" + (progress / wordsCount) + "\n[PAGES QUEUE]\t" + targetPages.size());
		    }
		} finally {
		    words.close();
		}
		
		//construct list of remaining PageDocuments
		List<PageDocument> pagesList = new ArrayList<PageDocument>(targetPages.values());
		Collections.sort(pagesList);
		
		return pagesList;
	}
	
	
	private static void writeNewDbData(List<PageDocument> docs, MongoCollection<Document> collection){
		Gson gson = new GsonBuilder().create();
		List<Document> converted = new ArrayList<Document>();
		
		for (PageDocument page : docs)
			converted.add(Document.parse(gson.toJson(page)));
		
		collection.insertMany(converted);
	}
	
	
	private static void writeNewDbData(PageDocument doc, MongoCollection<Document> collection){
		Gson gson = new GsonBuilder().create();
		Document dbDoc = Document.parse(gson.toJson(doc));
		collection.insertOne(dbDoc);
	}
	
	
}

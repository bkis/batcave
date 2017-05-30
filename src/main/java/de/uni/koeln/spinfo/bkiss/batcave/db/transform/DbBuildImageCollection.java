package de.uni.koeln.spinfo.bkiss.batcave.db.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.bson.Document;
import org.bson.types.Binary;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;


public class DbBuildImageCollection {
	

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
		
		//prepare data
		String imageDirPath = props.getProperty("imageSourceDir");
		String imageNamePattern = ".+\\.png";
		MongoCollection<Document> imageCollection = mongo.getDatabase(
				props.getProperty("targetDb"))
				.getCollection(props.getProperty("imageCollectionName"));
		
		//insert images into new Collection
		try {
			insertImages(imageCollection, imageDirPath, imageNamePattern);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//cleanup
		mongo.close();
	}
	
	
	public static void insertImages(
			MongoCollection<Document> collection,
			String dirPath,
			String fileNamePattern) throws IOException{
		
		File dir = new File(dirPath);
		
		//check image directory
		if (!dir.exists() || !dir.isDirectory()){
			System.err.println("[ERROR] " + dir.getName() + " is not an existing directory.");
			return;
		}
		
		//get image files
		File[] images = dir.listFiles(new FilenameFilter(){
			@Override
			public boolean accept(File dir, String name) {
				return name.matches(fileNamePattern);
			}
		});
		System.out.println("Found " + images.length + " image files matching pattern " + fileNamePattern);
		
		for (File image : images){
            FileInputStream f = new FileInputStream(image);
            byte b[] = new byte[f.available()];
            f.read(b);
 
            Binary data = new Binary(b);
            Document doc = new Document();
            doc.append("_id", image.getName()).append("image", data);
            collection.insertOne(doc);
            f.close();
		}
	}

}

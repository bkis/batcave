package de.uni.koeln.spinfo.bkiss.batcave.analysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;

public abstract class AbstractVectorSpace implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private static final String FILE_EXT = ".vec";
	
	private String typeName;
	private String fileName;
	private File vectorSpaceFile;
	protected Map<String, Map<String, Integer>> vectorSpace;
	
	
	public AbstractVectorSpace(String typeName, String vectorsDirPath){
		this.typeName = typeName
				.toUpperCase()
				.replaceAll("[^\\w\\-\\_\\d]+", "");
		this.fileName = this.typeName+ FILE_EXT;
		
		//create vector space file object
		this.vectorSpaceFile = new File(
				vectorsDirPath + 
				(vectorsDirPath.endsWith(File.separator) ? "" : File.separator) + 
				this.fileName);
	}

	
	public String loadVectorSpace(){
		if (vectorSpace != null){
			return "Vector space \"" + typeName + "\" already loaded.";
		}
		if (!vectorSpaceFile.exists()){
			return "Error: Vector space \"" + typeName + "\" not created, yet!";
		}
		
		try {
			vectorSpace = readVectorSpace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if (vectorSpace != null){
			return "Vector space \"" + typeName + "\" loaded.";
		} else {
			return "Error: Unable to read vector space file \"" + typeName + "\".";
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private Map<String, Map<String, Integer>> readVectorSpace() throws Exception{
		BufferedInputStream bis;
		ObjectInputStream ois;
		Map<String, Map<String, Integer>> vspace = null;
		
		bis = new BufferedInputStream(new FileInputStream(vectorSpaceFile));
		ois = new ObjectInputStream(bis);
		vspace = (Map<String, Map<String, Integer>>) ois.readObject();
		ois.close();
		
		return vspace;
	}
	
	
	protected void writeVectorSpace() throws Exception{
		if (vectorSpace == null) return;
		if (vectorSpaceFile.exists()) vectorSpaceFile.delete();
		FileOutputStream fos = new FileOutputStream(vectorSpaceFile);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(vectorSpace);
		oos.close();
	}
	
	
	protected <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map, final boolean ascending) {
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			@Override
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if (ascending)
					return (o1.getValue()).compareTo(o2.getValue());
				else
					return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
	
	public abstract void createVectorSpace(List<PageDocument> data);

}

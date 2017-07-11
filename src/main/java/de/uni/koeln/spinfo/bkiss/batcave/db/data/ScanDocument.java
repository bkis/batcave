package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data class to represent a scan image from the DB collection "scans"
 * @author kiss
 *
 */
@Document(collection="scans")
public class ScanDocument implements Comparable<ScanDocument> {
	
	@Id
	private String id;
	private String imageFile;
	private byte[] image;
	
	
	public ScanDocument() {
		
	}

    public ScanDocument(String id) {
    	this();
        this.id = id;
    }
    
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public byte[] getImage() {
		return image;
	}
	
	public String getImageFile() {
		return imageFile;
	}

	public void setImageFile(String imageFile) {
		this.imageFile = imageFile;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	@Override
    public String toString() {
        return id.toString();
    }
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof ScanDocument
				&& ((ScanDocument)obj).getId().equals(id);
	}

	@Override
	public int compareTo(ScanDocument o) {
		return id.compareTo(o.getId());
	}
    

}

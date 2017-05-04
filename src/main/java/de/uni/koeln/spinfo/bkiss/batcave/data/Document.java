package de.uni.koeln.spinfo.bkiss.batcave.data;

import org.springframework.data.annotation.Id;

public class Document {
	
	@Id
	private String id;
	private String title;
	private String content;
	
	public Document() {}

    public Document(String title, String content) {
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format(
                "Document[id=%s, title='%s', content='%s']",
                id, title, content);
    }

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
    
    

}

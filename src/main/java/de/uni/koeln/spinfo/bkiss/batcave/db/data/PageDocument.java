package de.uni.koeln.spinfo.bkiss.batcave.db.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PageDocument {
	
	private String id;
	private String xmlFile;
	private String volume;
	private Set<String> chapters;
	private Set<String> languages;
	private List<Token> tokens;
	
	
	public PageDocument() {
		this.chapters = new HashSet<String>();
		this.languages = new HashSet<String>();
		this.tokens = new ArrayList<Token>();
	}

    public PageDocument(String id) {
    	this();
        this.id = id;
    }

    
    public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getId() {
		return id;
	}

	public Set<String> getChapters() {
		return chapters;
	}
	
	public void addChapter(String chapter){
		chapters.add(chapter);
	}

	public Set<String> getLanguages() {
		return languages;
	}
	
	public void addLanguage(String language){
		languages.add(language);
	}

	public List<Token> getTokens() {
		return tokens;
	}
	
	public void sortTokens(){
		Collections.sort(tokens);
	}
	
	public void addToken(Token token){
		tokens.add(token);
	}
	

	@Override
    public String toString() {
        return String.format(
                "Document[id=%s, title='%s', content='%s']",
                id, xmlFile, volume);
    }
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof PageDocument
				&& ((PageDocument)obj).getId().equals(id);
	}
    

}

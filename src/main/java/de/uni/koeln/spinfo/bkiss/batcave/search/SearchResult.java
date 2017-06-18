package de.uni.koeln.spinfo.bkiss.batcave.search;

public class SearchResult {
	
	private String pageId;
	private String hit;
	private String tag;
	private String tagPrev;
	private String tagNext;
	private int index;
	private String context;
	private String chapter;
	private String volume;
	private String language;
	
	
	public SearchResult(
			String pageId,
			String hit,
			String tag,
			String tagPrev,
			String tagNext,
			int index) {
		
		super();
		this.pageId = pageId;
		this.hit = hit;
		this.tag = tag;
		this.tagPrev = tagPrev;
		this.tagNext = tagNext;
		this.index = index;
		this.context = "";
	}
	
	
	public String getTagPrev() {
		return tagPrev;
	}

	
	public void setTagPrev(String tagPrev) {
		this.tagPrev = tagPrev;
	}

	
	public String getTagNext() {
		return tagNext;
	}

	
	public void setTagNext(String tagNext) {
		this.tagNext = tagNext;
	}

	
	public String getPageId() {
		return pageId;
	}


	public void setPageId(String pageId) {
		this.pageId = pageId;
	}


	public String getHit() {
		return hit;
	}


	public void setHit(String hit) {
		this.hit = hit;
	}


	public String getTag() {
		return tag;
	}


	public void setTag(String tag) {
		this.tag = tag;
	}


	public int getIndex() {
		return index;
	}


	public void setIndex(int index) {
		this.index = index;
	}


	public String getContext() {
		return context;
	}


	public void setContext(String context) {
		this.context = context;
	}
	
	
	public void appendToContext(String toAppend){
		if (context.length() > 0)
			context += " ";
		context += "toAppend";
	}


	public String getChapter() {
		return chapter;
	}


	public void setChapter(String chapter) {
		this.chapter = chapter;
	}


	public String getVolume() {
		return volume;
	}


	public void setVolume(String volume) {
		this.volume = volume;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}
	
	
}

package de.uni.koeln.spinfo.bkiss.batcave.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;


/**
 * Service class offering index creation routines and search logic.
 * @author kiss
 *
 */
@Service
public class SearchService {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	@Value("${data.index}")
	private String indexDirPath;
	
	private Directory indexDirectory;
	private IndexReader indexReader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	
	private MultiFieldQueryParser queryParser;
	
	
	/**
	 * Creates the search index. Will delete old index if present.
	 * @return
	 */
	public String createIndex(){
		List<PageDocument> pages = pageRepo.findAll();
		
		init();
		IndexWriter w = getIndexWriter();
		
		if (indexExists()){
			try {
				w.deleteAll();
				w.commit();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		for (PageDocument page : pages){
			for (int i = 0; i < page.getTokens().size(); i++){
				Token t = page.getTokens().get(i);
				Document doc = new Document();
				doc.add(new TextField("id", page.getId(), Store.YES));
				doc.add(new TextField("token", t.getForm(), Store.YES));
				doc.add(new StoredField("index", t.getIndex()));
				
				//token tags
				for (String tag : t.getTags()){
					doc.add(new TextField("tag", tag, Store.YES));
				}
				
				//prev tags
				if (i > 0)
					for (String tag : page.getTokens().get(i-1).getTags()){
						doc.add(new TextField("tagPrev", tag, Store.YES));
					}
				
				//next tags
				if (i < page.getTokens().size() - 1)
					for (String tag : page.getTokens().get(i+1).getTags()){
						doc.add(new TextField("tagNext", tag, Store.YES));
					}
				
				try {
					w.addDocument(doc);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			w.commit();
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "Such-Index wurde erstellt.";
	}
	
	
	/**
	 * Search request method. Returns list of SearchResult objects.
	 * @param token
	 * @param tag
	 * @param tagPrev
	 * @param tagNext
	 * @param fuzzy
	 * @param contextWindow
	 * @return list of SearchResult objects
	 */
	public List<SearchResult> search(
			String token,
			String tag,
			String tagPrev,
			String tagNext,
			boolean fuzzy,
			int contextWindow){
		
		init();
		
		if (this.searcher == null)
			initIndexSearcher();
		
		List<SearchResult> results = new ArrayList<SearchResult>();
		if (token.length()
				+ tag.length()
				+ tagPrev.length()
				+ tagNext.length() == 0)
			return results;
		
		//apply fuzzy search
		if (fuzzy && token != null && token.length() > 0)
			token = token.concat("~");
		
		//construct query string
		String queryString = "";
		queryString += (token.length() > 0 ? "token:" + token : "");
		queryString += (tag.length() > 0 ? (queryString.length() > 0 ? " AND " : "") + "tag:" + tag.toUpperCase() : "");
		queryString += (tagPrev.length() > 0 ? (queryString.length() > 0 ? " AND " : "") + "tagPrev:" + tagPrev.toUpperCase() : "");
		queryString += (tagNext.length() > 0 ? (queryString.length() > 0 ? " AND " : "") + "tagNext:" + tagNext.toUpperCase() : "");
		
		//parse query
		Query q = null;
		try {
			q = queryParser.parse(queryString);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		//get top docs
		TopDocs topDocs = null;
        try {
			topDocs = searcher.search(q, 50, Sort.RELEVANCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        //contruct search results
        for (ScoreDoc sd : topDocs.scoreDocs){
        	try {
				results.add(generateResult(sd, contextWindow));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        return results;
	}
	
	
	private IndexWriter getIndexWriter(){
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		try {
			return new IndexWriter(this.indexDirectory, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private void initIndexSearcher(){
		if (this.indexReader == null){
			try {
				this.indexReader = DirectoryReader.open(indexDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.searcher == null){
			this.searcher = new IndexSearcher(indexReader);
		}
	}
	
	
	/*
	 * Helper method generating a search result
	 */
	private SearchResult generateResult(ScoreDoc scoreDoc, int contextWindow) throws IOException{
		Document doc = searcher.doc(scoreDoc.doc);
		
		SearchResult result = new SearchResult(
				doc.get("id"),
				doc.get("token"),
				doc.get("tag"),
				doc.get("tagPrev"),
				doc.get("tagNext"),
				Integer.valueOf(doc.get("index")));
		
		PageDocument page = pageRepo.findOne(result.getPageId());
		
		result.setContext(getContext(page,
				result.getIndex(),
				contextWindow));
		
		result.setChapter(page.getChapters().toString());
		result.setVolume(page.getVolume());
		result.setLanguage(page.getLanguages().toString());
		
		return result; 
	}
	
	
	/*
	 * Helper method returning the context of a token in a document
	 */
	private String getContext(PageDocument page, int tokenIndex, int contextWindow){
		if (page == null)
			return "";
		
		int index = page.getTokens().indexOf(new Token(tokenIndex));
		StringBuilder sb = new StringBuilder();
		
		for (int i = Math.max(0, index - contextWindow);
				i < Math.min(page.getTokens().size(), index + contextWindow);
				i++) {
			sb.append(page.getTokens().get(i).getForm());
			sb.append(" ");
		}
		
		return sb.toString().trim();
	}
	
	
	/*
	 * Helper method checking if index is present
	 */
	private boolean indexExists(){
		try {
			return DirectoryReader.indexExists(indexDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private File initIndexDirectory(){
		File indexDir = new File(indexDirPath);
		if (!indexDir.exists()) indexDir.mkdirs();
		return indexDir;
	}
	
	/*
	 * initializes the necessary fields
	 */
	private void init(){
		if (this.analyzer != null) return;
		this.analyzer = new StandardAnalyzer();
		this.queryParser = new MultiFieldQueryParser(
                new String[] {"token", "tag", "tagPrev, tagNext"},
                analyzer);
		try {
			this.indexDirectory = FSDirectory.open(
					initIndexDirectory().toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}

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
import org.springframework.stereotype.Service;

import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocument;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.PageDocumentRepository;
import de.uni.koeln.spinfo.bkiss.batcave.db.data.Token;


@Service
public class SearchService {
	
	@Autowired
	private PageDocumentRepository pageRepo;
	
	private Directory ramDirectory;
	private IndexReader indexReader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	
	private MultiFieldQueryParser queryParser;
	
	public SearchService(){
		this.analyzer = new StandardAnalyzer();
		this.queryParser = new MultiFieldQueryParser(
                new String[] {"token", "tag"},
                analyzer);
		try {
			this.ramDirectory = FSDirectory.open(new File("index").toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String createIndex(List<PageDocument> pages){
		IndexWriter w = getIndexWriter();
		
		for (PageDocument page : pages){
			for (Token t : page.getTokens()){
				Document doc = new Document();
				doc.add(new TextField("id", page.getId(), Store.YES));
				doc.add(new TextField("token", t.getForm(), Store.YES));
				doc.add(new StoredField("index", t.getIndex()));
				
				for (String tag : t.getTags()){
					doc.add(new TextField("tag", tag, Store.YES));
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
		
		return "index created";
	}
	
	
	public String updateIndex(List<PageDocument> pages){
		return "index updated";
	}
	
	
	public List<SearchResult> search(String token, String tag, int contextWindow){
		if (this.searcher == null)
			initIndexSearcher();
		
		//construct query string
		String queryString = "";
		queryString += (token.length() > 0 ? "token:" + token : "") + " ";
		queryString += tag.length() > 0 ? "tag:" + tag.toUpperCase() : "";
		
		Query q = null;
		try {
			q = queryParser.parse(queryString);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		
		TopDocs topDocs = null;
        try {
			topDocs = searcher.search(q, 20, Sort.RELEVANCE);
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        List<SearchResult> results = new ArrayList<SearchResult>();
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
			return new IndexWriter(this.ramDirectory, config);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	private void initIndexSearcher(){
		if (this.indexReader == null){
			try {
				this.indexReader = DirectoryReader.open(ramDirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.searcher == null){
			this.searcher = new IndexSearcher(indexReader);
		}
	}
	
	
	private SearchResult generateResult(ScoreDoc scoreDoc, int contextWindow) throws IOException{
		Document doc = searcher.doc(scoreDoc.doc);
		
		SearchResult result = new SearchResult(
				doc.get("id"),
				doc.get("token"),
				doc.get("tag"),
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
	
	
}

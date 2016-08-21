package in.shamit.wikipedia.parse.article.parts;

import java.io.File;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

import in.shamit.wikipedia.parse.article.parser.ArticleTextExtractor;
import in.shamit.wikipedia.parse.article.parser.ArticleXMLParser;
import in.shamit.wikipedia.parse.article.vo.Article;
import in.shamit.wikipedia.parse.article.vo.ArticleData;


public class FragmentProcessor implements Runnable {
	XMLFragmenlHolder xml;
	PrintWriter out;
	static XPathFactory pathFactory = XPathFactory.newInstance();
	static TransformerFactory tFactory = TransformerFactory.newInstance();
	public static String delim = "%##$$##%";
	static File articleDir=null;
	static AtomicInteger count=new AtomicInteger(0);
	ArticleXMLParser parser = new ArticleXMLParser();
	private PrintWriter textOut;
	public FragmentProcessor(XMLFragmenlHolder xml,PrintWriter out, PrintWriter textOut) {
		super();
		this.xml = xml;
		this.out = out;
		this.textOut = textOut;
	}

	@Override
	public void run()  {
		try{
			Document doc = xml.getXMLDoc();
			ArticleData data = parser.loadDocumentFromXML(doc);
			Article art = new Article(data.getId(), null, data.getTitle());
			saveArticle(doc,art,data);
			persistArticleCSV(art,data);			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveArticle(Document doc, Article art, ArticleData data) throws TransformerException {
		if(articleDir!=null){
			File articleFile = createDestinationFile(art.getWikiId()+"");
			Transformer transformer = tFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			transformer.transform(source, new StreamResult(articleFile));
		}
		if(textOut!=null){
			ArticleTextExtractor extract = new ArticleTextExtractor();
			String text=extract.getPlainText(data);
			textOut.println(text);
		}
	}

	private void persistArticleCSV(Article art,ArticleData data) {
		int curCount=count.incrementAndGet();
		String type="other";
		if(data.isPerson()){
			type="person";
		}else{
			if(data.isCompany()){
				type="company";
			}
		}
		String csvline = "\""+art.getWikiId()+"\""+delim+"\""+art.getTitle()+"\""+delim+data.isPerson()+delim+type;
		out.println(csvline);
		if(curCount%10000==0){
			System.err.println("Article count # "+curCount);
		}
	}

	public static File getArticleDir() {
		return articleDir;
	}

	public static void setArticleDir(File articleDir) {
		FragmentProcessor.articleDir = articleDir;
	}
	private File createDestinationFile(String id){
		File ret = getPathForId(articleDir, id);
		ret.getParentFile().mkdirs();
		return ret;
	}
	public static File getPathForId(File baseDir, String id){
		StringBuffer path = new StringBuffer();
		char ctr[] = id.toCharArray();
		for(char c:ctr ){
			path.append(c);
			path.append(File.separatorChar);
		}
		path.append(id+".xml");
		File ret = new File(baseDir, path.toString());
		return ret;
	}
}

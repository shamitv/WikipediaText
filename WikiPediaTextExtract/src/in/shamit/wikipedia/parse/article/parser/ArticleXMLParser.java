package in.shamit.wikipedia.parse.article.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import in.shamit.wikipedia.parse.article.vo.ArticleData;

public class ArticleXMLParser {
	
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	static XPathFactory pathFactory = XPathFactory.newInstance();
	static String personPattern = "{Persondata";
	static String companyPattern = "\\{Infobox.*company";
	
	public ArticleData loadDocumentFromFile(File xmlFile){
		try{
			ArticleData art = new ArticleData();
			Document doc = getXMLDoc(xmlFile);
			fillArticleData(art, doc);
			return art;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
		//System.out.print(id+",");
	}
	
	public ArticleData loadDocumentFromXML(Document doc){
		try{
			ArticleData art = new ArticleData();
			fillArticleData(art, doc);
			return art;
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
	
	void fillArticleData(ArticleData art, Document doc) throws XPathExpressionException{
		XPath xpath = pathFactory.newXPath();
		String expression = "/page/id";
		Node idNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		String id = idNode.getTextContent();
		expression = "/page/title";
		Node titleNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		String title = titleNode.getTextContent();
		art.setId(Integer.parseInt(id));
		art.setTitle(title);
		expression = "page/redirect";
		Node redirectNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
		if(redirectNode!=null){
			art.setRedirect(true);
		}else{
			art.setRedirect(false);
			expression = "page/revision/text";
			Node textNode = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
			if(textNode!=null){
				String text = textNode.getTextContent();
				art.setText(text);
				if(text.contains(personPattern)){
					art.setPerson(true);
				}
				if(Pattern.compile(companyPattern).matcher(text).find()){
					art.setCompany(true);
				}
			}
		}
	}
	
	Document getXMLDoc(File xmlFile){
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new FileInputStream(xmlFile)  ));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}

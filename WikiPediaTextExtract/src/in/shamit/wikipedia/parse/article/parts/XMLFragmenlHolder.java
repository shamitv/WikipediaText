package in.shamit.wikipedia.parse.article.parts;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFragmenlHolder {
	static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	public StringBuffer buff = new StringBuffer();
	public void add(String s){
		buff.append(s);
		buff.append('\n');
	}
	public Document getXMLDoc(){
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			return builder.parse(new InputSource(new ByteArrayInputStream(buff.toString().getBytes(StandardCharsets.UTF_8))));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println(buff.toString());
			throw new RuntimeException(e);
		}
	}
}

package in.shamit.wikipedia.parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import in.shamit.wikipedia.parse.article.parts.FragmentProcessor;
import in.shamit.wikipedia.parse.article.parts.XMLFragmenlHolder;
import in.shamit.wikipedia.parse.article.parts.threadpool.BoundedExecutor;



public class ParseWikipediaXML {

	String xmlPath;
	String csvPath;
	File articleDir=null;
	PrintWriter out;
	PrintWriter textOut;
	XMLFragmenlHolder xml = null;  
	int fragCount=0;
	int maxThread = 500;
	ExecutorService service = Executors.newFixedThreadPool(maxThread);
	BoundedExecutor exec=new BoundedExecutor(service, maxThread);
		
	Date start;
	Date end;
	
	public ParseWikipediaXML(String xmlPath, String csvPath, String articlePath, String textPath) throws FileNotFoundException {
		super();
		this.xmlPath = xmlPath;
		this.csvPath = csvPath;
		if(articlePath!=null){
			this.articleDir = new File(articlePath);
			if(!articleDir.exists()){
				articleDir.mkdirs();
			}
			FragmentProcessor.setArticleDir(articleDir);
		}
		out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(csvPath), StandardCharsets.UTF_8), true);
		textOut = new PrintWriter(new OutputStreamWriter(new FileOutputStream(textPath), StandardCharsets.UTF_8), true);
	}

	public void parse  () throws IOException{
		start = new Date();
		File xmlFile = new File(xmlPath);
		//BZip2CompressorInputStream inStream = new BZip2CompressorInputStream(new FileInputStream(xmlFile));
		//BufferedReader reader = new BufferedReader( new InputStreamReader(inStream, "UTF-8"));
		
		FileInputStream inStream = new FileInputStream(xmlPath);
		BufferedReader reader = new BufferedReader( new InputStreamReader(inStream, "UTF-8"));
		
		String line = null;
		while((line = reader.readLine()) != null) {
			switch(line.trim()){
			case "<page>" :	createNewFragment(line); break;						
			case "</page>" : finishFragment(line);	break;
			default : addLine(line); break;
			}
		}
		end = new Date();
		reader.close();
	}

	private void addLine(String l) {
		if(xml!=null){
			xml.add(l);
		}
	}

	private void createNewFragment(String l) {
		xml = new XMLFragmenlHolder();
		xml.add(l);
		fragCount++;
	}
	private void finishFragment(String l) {
		xml.add(l);
		if(fragCount%100000==0){
			System.out.println(fragCount);
		}
		addFragmentToQ();
	}
	private void addFragmentToQ()  {
		FragmentProcessor task = new FragmentProcessor(xml,out,textOut);
		//service.submit(task);
		try {
			exec.submitTask(task);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void cleanup() {
		service.shutdown();
		out.close();
		textOut.close();
	}

	public static void main(String[] args) throws IOException {
		String bz2path = "L:\\work\\nlp\\datasets\\wikipedia\\enwiki-20151102-pages-articles.xml";
		String csvPath="L:\\work\\nlp\\datasets\\wikipedia\\article.csv";
		String textPath="L:\\work\\nlp\\datasets\\wikipedia\\wikiText.txt";
		String articlePath=null;
		if(args.length>1){
			bz2path = args[0];
			csvPath = args[1];
			textPath = args[2]; 
		}
		if(args.length>3){
			articlePath=args[3];
		}
		ParseWikipediaXML parser = new ParseWikipediaXML(bz2path,csvPath,articlePath,textPath);
		System.out.println(parser.xmlPath);
		System.out.println(parser.csvPath);
		System.out.println(parser.articleDir);
		parser.parse();
		parser.cleanup();
		System.out.println("Started at "+parser.start.toString()+"\nComplted at "+parser.end.toString()+"\nFragments "+parser.fragCount);
	}
}

package in.shamit.wikipedia.parse.article.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.shamit.wikipedia.parse.article.vo.ArticleData;

public class ArticleTextExtractor {
	private static final Pattern infoboxStartPattern = Pattern
			.compile("\\{\\{Infobox", Pattern.DOTALL);
	private static final Pattern tableTemplatePattern = Pattern
			.compile("\\(\\[\\[Template:.*\\]]\\).*\\(.*\\)", Pattern.DOTALL);	
	private static final Pattern templateStartOrEndPattern = Pattern
			.compile("(\\{\\{)|(\\}\\})", Pattern.DOTALL);
	private static final Pattern formatStartOrEndPattern = Pattern
			.compile("(\\{\\|)|(\\|\\})", Pattern.DOTALL);
	
	private static final Pattern imagePattern = Pattern
			.compile("\\[\\[Image:" );
	private static final Pattern filePattern = Pattern
			.compile("\\[\\[File:" );
	private static final Pattern squareBraceStartOrEndPattern = Pattern
			.compile("(\\[\\[)|(\\]\\])", Pattern.DOTALL);
	private static final Pattern htmlNbspPattern = Pattern
			.compile("&nbsp;", Pattern.DOTALL);
	private static final Pattern xmlCommentPattern = Pattern
			.compile("<!--.*?-->", Pattern.DOTALL);
	private static final Pattern refPattern = Pattern
			.compile("<ref.*?.[f/]>", Pattern.DOTALL);
	private static final Pattern tableRowPattern = Pattern
			.compile("^\\|.*$", Pattern.MULTILINE);
	//Regex for categories
	private static final Pattern categoryPattern = Pattern  
			.compile("\\[\\[Category:.+\\]\\]");
	
	// Regex for internal WiKi links
	private static final Pattern linkPattern = Pattern  
			.compile("(?:\\[\\[([^\\]\\|]+?)\\s*(?:\\|\\s*([^\\]]*))?\\]\\])");

	// Regex for Bold / Italic
	private static final Pattern boldPattern = Pattern  
			.compile("''+");

	// Regex for section headline
	private static final Pattern sectionPattern = Pattern  
			.compile("==+");
	private static final Pattern bulletLinePattern = Pattern
			.compile("^\\S*\\*.*$", Pattern.MULTILINE);

	private static final Pattern pipeLinePattern = Pattern
			.compile("^\\S*\\|.*$", Pattern.MULTILINE);
	

	public String getPlainText(ArticleData art){
		String simpleText = null;
		String text =  art.getText();
		simpleText = text;
		simpleText = removeHtmlEntities(simpleText);
		simpleText = removeXmlComment(simpleText);
		simpleText = removeImages(simpleText);
		simpleText = removeFiles(simpleText);
		simpleText = removeTemplates(simpleText);
		simpleText = removeRefTags(simpleText);
		simpleText = removeCategories(simpleText);
		simpleText = removeInternalLinks(simpleText);
		simpleText = removeBolditalics(simpleText);
		simpleText = removeSectionMarks(simpleText);
		simpleText = removeLinesStartingWithBullet(simpleText);
		simpleText = removeLinesStartingWithPipe(simpleText);
		//Remove Empty lines after all this substitution with blanks
		simpleText = removeEmptyLines(simpleText); 
		return simpleText;
	}
	private String removeLinesStartingWithPipe(String text) {
		Matcher m = pipeLinePattern.matcher(text);
		return m.replaceAll(" ");	}
	private String removeLinesStartingWithBullet(String text) {
		Matcher m = bulletLinePattern.matcher(text);
		return m.replaceAll(" ");
	}
	String removeHtmlEntities(String text){
		Matcher m = htmlNbspPattern.matcher(text);
		return m.replaceAll(" ");
	}
	String removeXmlComment(String text){
		Matcher m = xmlCommentPattern.matcher(text);
		return m.replaceAll(" ");
	}
	
	String removeRefTags(String text) {
		Matcher m = refPattern.matcher(text);
		return m.replaceAll(" ");
	}
	String removeMatchingPatterns(Pattern matchGroup, String text){
		Matcher m = matchGroup.matcher(text);
		while(m.find()){
			int parenthesisCount=0;
			int start = m.start(); 
			int end = m.end();
			int pos = end;
			parenthesisCount++;
			m.region(pos, text.length());
			while(parenthesisCount>0 && pos < text.length() ){
				if(m.find()){
					if(m.start(1) != -1){
						// Opening braces I.e. {{ were found
						parenthesisCount++;
					}else{
						// Closing braces I.e. }} were found
						parenthesisCount--;
					}
					pos = m.end();
					m.region(pos, text.length());
				}else{
					throw new PatternMismatchException("Mismatched template open and close pattern");		
				}
			}
			text = text.substring(0,start) + text.substring(pos);
			m.reset(text);
		}
		return text;
	}
	String removeTables(String text){
		try{
			text =  removeMatchingPatterns(formatStartOrEndPattern,text);	
		}catch(PatternMismatchException e){
			Matcher m = tableRowPattern.matcher(text);
			if(m.find()){
				text = m.replaceAll(" ");	
			}
		}
		return text;
	}
	String removeTemplates(String text) {
		try{
			text =  removeMatchingPatterns(templateStartOrEndPattern,text);
			text=removeTables(text);
			Matcher m = tableTemplatePattern.matcher(text);
			return m.replaceAll(" ");			
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}

	String removeBolditalics(String text) {
		Matcher m = boldPattern.matcher(text);
		return m.replaceAll(" ");
	}

	String removeSectionMarks(String text) {
		Matcher m = sectionPattern.matcher(text);
		return m.replaceAll(" ");
	}

	String removeCategories(String text) {
		Matcher m = categoryPattern.matcher(text);
		return m.replaceAll(" ");
	}

	String removeInternalLinks(String text) {
		try{
			Matcher matcher = linkPattern.matcher(text);
			while (matcher.find()) {
				//System.out.print(frag + " : ");
				String link = matcher.group(1);
				String ref = matcher.group(2);
				//System.out.println(link + " : " + ref);
				String target = ref;
				if (target == null) {
					target = link;
				}
				//System.out.println("\t" + target);
				text = matcher.replaceFirst(Matcher.quoteReplacement(target));
				matcher.reset(text);
			}
			return text;			
		}catch(Exception ex){
			throw new RuntimeException(ex);
		}

	}

	String removeEmptyLines(String text) {
		StringBuffer ret = new StringBuffer();
		text = text.replaceAll("(?m)^\\s$", "");
		String lines[] = text.split("\\r?\\n");
		for (String l : lines) {
			String t = l.trim();
			if (!t.equals("")) {
				ret.append(t + "\r\n");
			}
		}
		return ret.toString();
	}
	
	String removeBlockInSquareBrackets(Pattern ptrn,String text){
		Matcher m = ptrn.matcher(text);
		while(m.find()){
			int parenthesisCount=1;
			int start = m.start(); 
			int end = m.end();
			int pos = end;
			Matcher parenMatcher = squareBraceStartOrEndPattern.matcher(text);
			parenMatcher.region(pos, text.length());
			while(parenthesisCount>0 && pos < text.length() ){
				if(parenMatcher.find()){
					if(parenMatcher.start(1) != -1){
						// Opening braces I.e. {{ were found
						parenthesisCount++;
					}else{
						// Closing braces I.e. }} were found
						parenthesisCount--;
					}
					pos = parenMatcher.end();
					parenMatcher.region(pos, text.length());
				}else{
					throw new PatternMismatchException("Mismatched template open and close pattern");	
				}
			}
			int initLen = text.length();
			String removedPart = text.substring(start,pos);
			text = text.substring(0,start) + text.substring(pos);
			int newLen = text.length();
			parenMatcher.reset(text);
			m.reset(text);
		}
		return text;		
	}
	
	String removeImages(String text){
		return removeBlockInSquareBrackets(imagePattern,text);
	}
	String removeFiles(String text){
		return removeBlockInSquareBrackets(filePattern,text);
	}
}

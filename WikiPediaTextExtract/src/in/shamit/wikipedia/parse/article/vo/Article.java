package in.shamit.wikipedia.parse.article.vo;

import java.io.Serializable;


/**
 * The persistent class for the article database table.
 * 
 */

public class Article implements Serializable {
	private static final long serialVersionUID = 1L;

	private int wikiId;

	private String redirectId;

	private String title;

	public Article() {
	}

	public int getWikiId() {
		return this.wikiId;
	}

	public Article(int wikiId, String redirectId, String title) {
		super();
		this.wikiId = wikiId;
		this.redirectId = redirectId;
		this.title = title;
	}

	public void setWikiId(int wikiId) {
		this.wikiId = wikiId;
	}

	public String getRedirectId() {
		return this.redirectId;
	}

	public void setRedirectId(String redirectId) {
		this.redirectId = redirectId;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
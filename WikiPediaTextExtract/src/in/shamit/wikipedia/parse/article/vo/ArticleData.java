package in.shamit.wikipedia.parse.article.vo;

public class ArticleData {
	int id;
	String title;
	boolean isRedirect=false;
	boolean isPerson=false;
	boolean isCompany=false;
	
	int redirectId;
	String text;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getRedirectId() {
		return redirectId;
	}
	public void setRedirectId(int redirectId) {
		this.redirectId = redirectId;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isRedirect() {
		return isRedirect;
	}
	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}
	public boolean isPerson() {
		return isPerson;
	}
	public void setPerson(boolean isPerson) {
		this.isPerson = isPerson;
	}
	public boolean isCompany() {
		return isCompany;
	}
	public void setCompany(boolean isCompany) {
		this.isCompany = isCompany;
	}
}

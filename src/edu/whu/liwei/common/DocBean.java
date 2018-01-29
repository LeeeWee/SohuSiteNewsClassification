package edu.whu.liwei.common;

/**
 * single doc data
 * @author Liwei
 *
 */
public class DocBean {
	
	// category url string
	private static final String AUTO_URL = "auto.sohu.com";
	private static final String BUSSINESS_URL = "business.sohu.com";
	private static final String IT_URL = "it.sohu.com";
	private static final String HEALTH_URL = "health.sohu.com";
	private static final String SPORTS_URL = "sports.sohu.com";
	private static final String TRAVEL_URL = "travel.sohu.com";
	private static final String EDUCATION_URL = "learning.sohu.com";
	private static final String CAREER_URL = "career.sohu.com";
	private static final String CULTURE_URL = "cul.sohu.com";
	private static final String SOCIETY_URL = "news.sohu.com";
	private static final String MILITARY_URL = "mil.news.sohu.com";
	private static final String OLYMPICS_URL = "2008.sohu.com";
	private static final String HOUSE_URL = "house.sohu.com";
	private static final String ENTERTAINMENT_URL = "yule.sohu.com";
	private static final String WOMEN_URL = "women.sohu.com";

	private int docId;
	private String no;
	private String url;
	private String title;
	private String content;
	private String category;
	
	/**
	 * Constructor
	 */
	public DocBean() {
		this.docId = 0;
		this.no = "";
		this.url = "";
		this.title = "";
		this.content = "";
		this.category = "";
	}

	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDocId() {
		return docId;
	}

	public void setDocId(Integer docId) {
		this.docId = docId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	} 
	
	public void setCategoryByUrl() {
		String indexUrl = url.substring(url.indexOf("http://") + 7, url.indexOf("com/")) + "com";
		switch (indexUrl) {
		case AUTO_URL:
			setCategory("auto"); break;
		case BUSSINESS_URL:
			setCategory("bussiness"); break;
		case IT_URL:
			setCategory("it"); break;
		case HEALTH_URL:
			setCategory("health"); break;
		case SPORTS_URL:
			setCategory("sports"); break;
		case TRAVEL_URL:
			setCategory("travel"); break;
		case EDUCATION_URL:
			setCategory("education"); break;
		case CAREER_URL:
			setCategory("career"); break;
		case CULTURE_URL:
			setCategory("culture"); break;
		case SOCIETY_URL:
			setCategory("society"); break;
		case MILITARY_URL:
			setCategory("military"); break;
		case OLYMPICS_URL:
			setCategory("olympics"); break;
		case HOUSE_URL:
			setCategory("house"); break;
		case ENTERTAINMENT_URL:
			setCategory("entertainment"); break;
		case WOMEN_URL:
			setCategory("women"); break;
		default:
			setCategory("");
		}
	}
	
}

package com.cl.spider.fetcher;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cl.spider.parser.PageParser;
import com.cl.spider.util.DBConn;

public class FetchDoubanBooks {
	private static final Logger LOG = Logger.getLogger(FetchDoubanBooks.class.getName());
	
	private String[] book_list = {"心理学", "人物传记", "中国历史", "旅行", "生活", "科普"};
	private String book_url = "http://www.douban.com/tag/"; 
	private static String[] user_agents = {"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:34.0) Gecko/20100101 Firefox/34.0", 
			"Mozilla/5.0 (Windows; U; Windows NT 6.1; en-US; rv:1.9.1.6) Gecko/20091201 Firefox/3.5.6", 
			"Mozilla/5.0 (Windows NT 6.2) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.12 Safari/535.11",
			"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2; Trident/6.0)",
			"Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:40.0) Gecko/20100101 Firefox/40.0",
			"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Ubuntu Chromium/44.0.2403.89 Chrome/44.0.2403.89 Safari/537.36"};
	
	private static Random random = new Random();
	
	public static HttpResponse getResponse(String url){
		if(null == url){
			LOG.error("---- URL is null -----");
			return null;
		}
		
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
		
		AbstractHttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent:", user_agents[random.nextInt(user_agents.length)]);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (Exception e) {
			// TODO: handle exception
			LOG.error(e);
		}
		
		return response;
	}
	
	public static String getContent(String url){
		String content = null;
		HttpResponse response = getResponse(url);
		if (response == null) {
			return content;
		}
		HttpEntity entity = response.getEntity();
		try {
			content = EntityUtils.toString(entity, "utf-8");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
	
	private void save2Db(String title, String desc, String link, float rate){
		String insertsql = "insert into doubanbooks(title, something, link, rate) values(?, ?, ?, ?)";
		Connection connection = DBConn.getConnection();
		PreparedStatement insertPS = null;
		
		try{
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			
			insertPS = connection.prepareStatement(insertsql);
			insertPS.clearParameters();
			insertPS.setString(1, title);
			insertPS.setString(2, desc);
			insertPS.setString(3, link);
			insertPS.setFloat(4, rate);
			
			insertPS.execute();
			connection.commit();
			
			insertPS.close();
			connection.close();
		}
		catch(Exception e){
			LOG.error(e);
		}
	}
	
	public void getPhysicBooks(String url){
		//String url = book_url + book_list[0] + "/book";
		String content = getContent(url);
		LOG.info(content);
		LOG.info(url);
		Document document = PageParser.getPageDocument(content);
		Elements elements = document.getElementsByClass("book-list");
		Element div = elements.get(0);
		Elements dlElements = div.getElementsByTag("dl");
		for(int i = 0; i < dlElements.size(); i++){
			Element ele = dlElements.get(i);
			Element titleEle = ele.getElementsByClass("title").get(0);
			Element descEle = ele.getElementsByTag("div").get(0);
			if(ele.getElementsByTag("div").size() == 2){
				Element rateEle = ele.getElementsByTag("div").get(1).getElementsByClass("rating_nums").get(0);
				LOG.info(titleEle.text() + "  " + titleEle.attr("href").toString() + "  评分---> " + rateEle.text());
				save2Db(titleEle.text(), descEle.text(), titleEle.attr("href").toString(), Float.parseFloat(rateEle.text()));
			}
		}
		
		Elements nextPages = document.getElementsByClass("next");
		Element next = nextPages.get(0).getElementsByTag("a").get(0);
		String nextUrl = next.attr("href").toString();
		//LOG.info(nextUrl);
		if (nextUrl == null) {
			return;
		}
		else {
			String _url = null;
			if(url.indexOf("?") != -1){
				_url = url.substring(0, url.indexOf("?"));
			}
			else {
				_url = url;
			}
			getPhysicBooks(_url + nextUrl);
		}
	}
	
	public void getBooks(){
		String url = book_url + book_list[0] + "/book?start=225";
		getPhysicBooks(url);
		//LOG.info(content);
	}
}

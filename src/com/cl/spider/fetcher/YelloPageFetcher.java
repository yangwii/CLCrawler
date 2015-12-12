package com.cl.spider.fetcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
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
import com.cl.spider.util.DegalImageQueue;
import com.cl.spider.util.ImageDownLoad;

public class YelloPageFetcher {
	private static final Logger Log = Logger.getLogger(YelloPageFetcher.class.getName());
	
	public static HttpResponse getResponse(String url){
		if(url == null){
			Log.warn("----URL is null----");
			return null;
		}
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
		HttpConnectionParams.setSoTimeout(params, 60 * 1000);
		AbstractHttpClient httpClient = new DefaultHttpClient(params);
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("User-Agent:", "Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
	
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
			//HttpEntity entity = response.getEntity();
		}catch(Exception e){
			Log.error(e);
		}
		return response;
	}
	
	public static String getContent(String url){
		if(url == null ) {
			url = "http://clsq.co/thread0806.php?fid=7";
		}
		String content = null;	
		HttpResponse response;
		try {
			response = getResponse(url);
			if(response == null){
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity, "GB2312");
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		return content;
	}
	
	public static void getContentFormDegal(String url){
		if (url == null) {
			url = "http://clsq.co/thread0806.php?fid=16&search=&page=2";
		}
		String content = getContent(url);
		PageParser.save2DegalTable(content);
	}
	
	public static void getContentFromTechDisucss(String url){
		Log.info("----Crawler Content from Techdiscuss---");
		if(url == null ) {
			url = "http://clsq.co/thread0806.php?fid=7&search=&page=2";
		}
		String content = getContent(url);
		PageParser.save2TechTable(content);
	}
	
	public static List<String> getFetcherURL(){
		Log.info("---Get C.L. URL---");
		String url = "http://mmda.site44.com/";
		String content;
		Document contentDoc = null;
		List<String> urls = new ArrayList<String>();
		HttpResponse response;
		try {
			response = getResponse(url);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				content = EntityUtils.toString(entity, "UTF-8");
				contentDoc = PageParser.getPageDocument(content);
				Elements elements = contentDoc.getElementsByTag("A");
				for(int i = 0; i < elements.size(); i++){
					Element element = elements.get(i);
					if (element.text().startsWith("µØÖ·")) {
						urls.add(element.attr("href"));
						Log.info(element.attr("href"));
					}
				}
				//Log.info(content);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		return urls;
	}
	
	public static boolean getImageFromDegal(){
		DegalImageQueue.initQueue();
		List<String> imgurls = new ArrayList<String>();
		int cnt = 0;
		for(Map.Entry<String, String> entry : DegalImageQueue.imageUrls.entrySet()){
			String name = entry.getKey();
			String imgurl = "http://clsq.co/" + entry.getValue();
			String content = getContent(imgurl);
			Document contentDoc = PageParser.getPageDocument(content);
			Elements elements = contentDoc.getElementsByTag("input");
			imgurls.clear();
			for(int i = 0; i < elements.size(); i++){
				Element element = elements.get(i);
				if("image".equals(element.attr("type"))){
					String _imgurl = element.attr("src");
					imgurls.add(_imgurl);
				}
			}
			//System.out.println(name);
			//System.out.println(imgurls);
			ImageDownLoad imageDownLoad = new ImageDownLoad(name, imgurls);
			new Thread(imageDownLoad).start();
			cnt ++;
			if (cnt == 2) {
				break;
			}
		}
		return true;
	}
	
	public static boolean testGetImageFromDegal(){
		List<String> imgurls = new ArrayList<String>();
		String name = "25";
		String imgurl = "http://clsq.co/htm_data/16/1512/1743487.html";
		String content = getContent(imgurl);
		Document contentDoc = PageParser.getPageDocument(content);
		Elements elements = contentDoc.getElementsByTag("input");
		imgurls.clear();
		for(int i = 0; i < elements.size(); i++){
			Element element = elements.get(i);
			if("image".equals(element.attr("type"))){
				String _imgurl = element.attr("src");
				imgurls.add(_imgurl);
			}
		}
		ImageDownLoad imageDownLoad = new ImageDownLoad(name, imgurls);
		new Thread(imageDownLoad).start();
		return true;
	}
	
	public static void fetchTechDiscuss(){
		List<String> urList = new ArrayList<String>();
		String page1url = "http://clsq.co/thread0806.php?fid=7";
		urList.add(page1url);
		for(int i = 2; i <= 5; i++){
			String _url = page1url + "&search=&page=" + i;
			urList.add(_url);
		}
		
		for(int i = 0; i < urList.size(); i++){
			String url = urList.get(i);
			new Thread(){
				@Override
				public void run() {
					getContentFromTechDisucss(url);
				}
			}.start();
		}
	}
}

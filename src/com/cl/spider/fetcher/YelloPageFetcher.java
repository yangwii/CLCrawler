package com.cl.spider.fetcher;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.Header;
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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cl.spider.parser.PageParser;
import com.cl.spider.util.CommonUtil;
import com.cl.spider.util.DegalImageQueue;
import com.cl.spider.util.DownLoadPicSpeakerImage;
import com.cl.spider.util.ImageDownLoad;
import com.cl.spider.util.PictureSpeaker;

public class YelloPageFetcher {
	private static final Logger Log = Logger.getLogger(YelloPageFetcher.class.getName());
	
	public static HttpResponse getResponse(String url){
		if(url == null){
			Log.warn("----URL is null----");
			return null;
		}
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
		HttpConnectionParams.setSoTimeout(params, 30 * 1000);
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
	
	public static void getEncodeType(String url){
		String charset = null;
		//String _url = url.substring(0, url.lastIndexOf("/"));
		//Log.info(_url);
		HttpResponse response = getResponse(url);
		Header header = response.getFirstHeader("Content-Type");
		if(header != null) {
			String value = header.getValue();
			if(value.contains("charset")){
				charset = value.substring(value.indexOf("=") + 1, value.length());
				Log.info(charset);
			}
		}
		if(charset == null || header == null){
			try {
				Connection connection = Jsoup.connect(url);
				connection.header("User-Agent:", "Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
				connection.execute();
				Document doc = connection.get();
				Element element = doc.getElementsByTag("meta").first();
				charset = element.attr("content").substring(element.attr("content").indexOf("=") + 1, element.attr("content").length());
			} catch (Exception e) {
				Log.error(e);
			}
		}
		if("gb2312".equalsIgnoreCase(charset)){
			charset = "GBK";
		}
		Log.info(charset);
		CommonUtil.charset = charset;
		//return charset.toUpperCase();
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
			//String charset = getEncodeType(response);
			if (entity != null) {
				content = EntityUtils.toString(entity, CommonUtil.charset);
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		return content;
	}
	
	public static void getContentFormDegal(String url){
		Log.info("----Crawler Content from Degalflag---");
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
			ImageDownLoad imageDownLoad = new ImageDownLoad(name, imgurls);
			new Thread(imageDownLoad).start();
			cnt ++;
			if (cnt == 5) {
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
		//getEncodeType(page1url);
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
	
	public static void fetchTechDiscussSingleThread(){
		List<String> urList = new ArrayList<String>();
		String page1url = "http://clsq.co/thread0806.php?fid=7";
		urList.add(page1url);
		//getEncodeType(page1url);
		for(int i = 2; i <= 5; i++){
			String _url = page1url + "&search=&page=" + i;
			urList.add(_url);
		}
		
		for(int i = 0; i < urList.size(); i++){
			String url = urList.get(i);
			getContentFromTechDisucss(url);
		}
		
		for(String _url : PictureSpeaker.pages){
			fetchTechDiscuss_PicSpeak("http://clsq.co/" + _url);
		}
	}
	
	public static void fetchDegalFlag(){
		List<String> urList = new ArrayList<String>();
		String page1url = "http://clsq.co/thread0806.php?fid=16";
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
					getContentFormDegal(url);
				}
			}.start();
		}
	}
	
	public static void fetchTechDiscuss_PicSpeak(String url) {
		//getEncodeType(url);
		String content = getContent(url);
		Document contentDoc = PageParser.getPageDocument(content);
		String titleElement = contentDoc.getElementsByTag("title").first().text(); 
		String header = "<html> <head> " + 
		        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312 \"> " +
		        "<title>"+ titleElement + "</title> " + "</head>";
		String title = titleElement.substring(titleElement.indexOf("£¨") + 1, titleElement.indexOf("£©"));
		Elements elements = contentDoc.getElementsByTag("div");
		String pre = "";
		for(int i = 0; i <  elements.size(); i++){
			Element element = elements.get(i);
			if("tpc_content do_not_catch".equals(element.attr("class"))){
				Log.info(element.toString());
				pre = element.toString().replaceAll("onclick=\"\\S*\"", "").replaceAll("style=\"\\S*\"", "");
				Pattern pattern = Pattern.compile("src=\"\\S*\"");
				Matcher matcher = pattern.matcher(pre);
				while(matcher.find()){
					String _url = matcher.group().substring(matcher.group().indexOf("=") + 2, matcher.group().length() - 1); 
					//Log.info(_url);
					String localURL = DownLoadPicSpeakerImage.downloadImg(_url, title);
					pre = pre.replace(_url, localURL);
				}
				//Log.info(pre);
				break;
			}
		}
		
		String html = header + "<body>" + pre +"</body>";
		Log.info(html);
		
		try {
			FileOutputStream fos = new FileOutputStream(new File("D:\\" + title + ".html"));
			OutputStreamWriter osw = new OutputStreamWriter(fos, "GBK");
			BufferedWriter bw = new BufferedWriter(osw);
			bw.write(html);
			bw.flush();
			fos.close();
			osw.close();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.error(e);
		}
	}
}

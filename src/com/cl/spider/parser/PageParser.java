package com.cl.spider.parser;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cl.spider.util.UrlSaveDb;

public class PageParser {
	private static final Logger Log = Logger.getLogger(Package.class.getName());
	
	public static Document getPageDocument(String content){
		return Jsoup.parse(content);
	}
	
	public static Map<String, String> getUrls(String content) {
		Document document = getPageDocument(content);
		Elements elements = document.getElementsByTag("a");
		Map<String, String> urls = new HashMap<String, String>();
		//List<String> urls = new ArrayList<String>();
		for(int i = 0; i < elements.size(); i++){
			Element element = elements.get(i);
			if(element.attr("href").startsWith("htm_data") && !element.hasAttr("title")){
				//urls.add(element.toString());
				urls.put(element.text(), element.attr("href"));
			}
		}
		for(Map.Entry<String, String> entry : urls.entrySet()){
			Log.info(entry.getKey() + "-->" + entry.getValue());
		}
		return urls;
		//return UrlSaveDb.TechSaveURL(urls);
	}
	
	public static boolean save2TechTable(String content) {
		Log.info("---Parse TechDiscuss Content----");
		Map<String, String> urls = getUrls(content);
		return UrlSaveDb.TechSaveURL(urls);
	}
	
	public static boolean save2DegalTable(String content) {
		Log.info("---Parse DegalFlag Content----");
		Map<String, String> urls = getUrls(content);
		return UrlSaveDb.DegalSaveURL(urls);
	}
}

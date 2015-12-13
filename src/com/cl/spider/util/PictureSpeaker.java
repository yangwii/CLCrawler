package com.cl.spider.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PictureSpeaker {
	public static Map<String, String> webPageUrl = new HashMap<String, String>();
	public static List<String> pages = new ArrayList<String>();
	
	public static synchronized void add(String title, String url) {
		webPageUrl.put(title, url);
	}
	
	public static void addUrl(String url) {
		pages.add(url);
	}
}

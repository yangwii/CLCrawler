package com.cl.spider.util;

import java.util.HashMap;
import java.util.Map;

public class PictureSpeaker {
	public static Map<String, String> webPageUrl = new HashMap<String, String>();
	
	public static synchronized void add(String title, String url) {
		webPageUrl.put(title, url);
	}
}

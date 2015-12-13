package com.cl.spider.test;

import org.apache.log4j.Logger;

import com.cl.spider.fetcher.YelloPageFetcher;

public class FunctionTest {
	private static final Logger Log = Logger.getLogger(FunctionTest.class.getName());
	
	public static void testCharset() {
		String url = "http://clsq.co/thread0806.php?fid=7";
		YelloPageFetcher.getEncodeType(url);
		//HttpResponse response = YelloPageFetcher.getResponse(url);
		//YelloPageFetcher.getEncodeType(response);
	}
	
	public static void main(String[] args){
		testCharset();
	}
}

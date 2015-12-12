package com.cl.spider;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.cl.spider.fetcher.YelloPageFetcher;
import com.cl.spider.util.CommonUtil;
import com.cl.spider.util.DBConn;

public class SpiderStart {
	private static final Logger Log = Logger.getLogger(SpiderStart.class.getName());
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		initParams();
		//YelloPageFetcher.getFetcherURL();
		YelloPageFetcher.fetchTechDiscuss();
		YelloPageFetcher.fetchDegalFlag();
		//YelloPageFetcher.getImageFromDegal();
	}

	public static void initParams(){
		InputStream in;
		try{
			in = new BufferedInputStream(new FileInputStream("conf/spider.properties"));
			Properties properties = new Properties();
			properties.load(in);
			
			DBConn.name = properties.getProperty("DB.username");
			DBConn.pwd = properties.getProperty("DB.password");
			DBConn.url = properties.getProperty("DB.connUrl");
			
			CommonUtil.ImageRoot = properties.getProperty("spider.imageRoot");
		}
		catch(IOException e){
			Log.error(e);
		}
	}
}

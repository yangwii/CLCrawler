package com.cl.spider.util;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

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

public class DownLoadPicSpeakerImage {
	private static final Logger Log = Logger.getLogger(DownLoadPicSpeakerImage.class.getName());
	private static Random random = new Random();
	
	public static String downloadImg(String url, String name) {
		// TODO Auto-generated method stub
		File file = new File("D:\\" + "tech\\"+ name);
		file.mkdirs();
		FileOutputStream outfile = null;
		String path = null;
		try {
			//Log.info(img);
			//String prefix = url.substring(url.lastIndexOf("/") + 1, url.length());
			String prefix = random.nextInt(10000) + ".jpg";
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
			HttpConnectionParams.setSoTimeout(params, 60 * 1000);
			AbstractHttpClient httpClient = new DefaultHttpClient(params);
			HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("User-Agent:", "Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
			byte[] buffer;
			HttpResponse response;
			response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				buffer = EntityUtils.toByteArray(entity);
				outfile = new FileOutputStream("D:\\" + "tech\\"+ name + "\\" + prefix);
				path = "D:\\" + "tech\\"+ name + "\\" + prefix;
				outfile.write(buffer);
				outfile.flush();
				outfile.close();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(url + " " + e);
		}
		//Log.info(url + " save ok---");
		
		return path;
	}
}

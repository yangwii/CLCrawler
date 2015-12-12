package com.cl.spider.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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

import com.cl.spider.fetcher.YelloPageFetcher;

public class ImageDownLoad implements Runnable{
	private final Logger Log = Logger.getLogger(ImageDownLoad.class.getName());
	
	private String name;
	private List<String> imgurl = new ArrayList<String>();
	public ImageDownLoad(String name, List<String> imgurl) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.imgurl.addAll(imgurl);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		File file = new File("D:\\" + "blue\\"+ name);
		file.mkdirs();
		FileOutputStream outfile = null;
		for(int i = 0; i < imgurl.size(); i++){
			String img = imgurl.get(i);
			try {
				//Log.info(img);
				String prefix = img.substring(img.lastIndexOf("/") + 1, img.length());
				HttpParams params = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(params, 60 * 1000);
				HttpConnectionParams.setSoTimeout(params, 60 * 1000);
				AbstractHttpClient httpClient = new DefaultHttpClient(params);
				HttpGet httpGet = new HttpGet(img);
				httpGet.setHeader("User-Agent:", "Mozilla/5.0 (Windows NT 6.3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.73 Safari/537.36");
				byte[] buffer;
				HttpResponse response;/* = YelloPageFetcher.getResponse(img);*/
				response = httpClient.execute(httpGet);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					buffer = EntityUtils.toByteArray(entity);
					outfile = new FileOutputStream("D:\\" + "blue\\"+ name + "\\" +prefix);	
					outfile.write(buffer);
					ImageCount.setSize(buffer.length);
					outfile.flush();
					outfile.close();
				}
			} catch (Exception e) {
				// TODO: handle exception
				Log.error(name + " " + e);
				return;
			}
			ImageCount.setCount();
			Log.info(name + " pic no." + i +" save ok---");
		}
		Log.info(name + "---download end --");
	}
	
	public synchronized void run1() {
		// TODO Auto-generated method stub
		File file = new File("D:\\" + "blue\\"+ name);
		file.mkdirs();
		URL url = null;
		FileOutputStream outfile = null;
		InputStream infile = null;
		URLConnection connection = null;
		for(int i = 0; i < imgurl.size(); i++){
			String img = imgurl.get(i);
			try{
				url = new URL(img);
				connection = url.openConnection();
				//connection.setConnectTimeout(10 * 1000);
				//connection.setReadTimeout(10 * 1000);
				infile = connection.getInputStream();
				String prefix = img.substring(img.lastIndexOf("/") + 1, img.length());
				outfile = new FileOutputStream("D:\\" + "blue\\"+ name + "\\" +prefix);
				byte[] buffer = new byte[1024];
				int len  = 0;
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				while ((len = infile.read(buffer)) != -1) {
					bos.write(buffer, 0, len);
				}
				outfile.write(bos.toByteArray());
				Log.info(name + " pic no." + i +" save ok---");
				infile.close();
				outfile.close();
			}
			catch(Exception e){
				Log.error(e);
				break;
			}
		}
		Log.info(name + "---download end --");
	}
	
}

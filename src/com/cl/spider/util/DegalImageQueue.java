package com.cl.spider.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

class DegalImage{
	public String name;
	public String url;
}

public class DegalImageQueue {
	private static final Logger Log = Logger.getLogger(DegalImageQueue.class.getName());
	
	public static Map<String, String> imageUrls = new HashMap<String, String>();
	
	public static synchronized DegalImage imagetUrl(){
		DegalImage image = new DegalImage();
		for(Map.Entry<String, String> entry : imageUrls.entrySet()){
			image.name = entry.getValue();
			image.url = entry.getValue();
			imageUrls.remove(image.name);
			break;
		}
		return image;
	}
	
	public static void initQueue(){
		Log.info("---init degal image queue---");
		String selectsql = "select * from degalflag";
		String updatesql = "update degalflag set isFetched = 1 where id = ?";
		Connection connection = DBConn.getConnection();
		PreparedStatement ps_update = null;
		Statement st = null; 
		ResultSet rs = null;
		
		try {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			ps_update = connection.prepareStatement(updatesql);
			st = connection.createStatement();
			rs = st.executeQuery(selectsql);
			while(rs.next()){
				String name = rs.getString("title");
				String url = rs.getString("url");
				int ifFetched = rs.getInt("isFetched");
				if(ifFetched == 0){
					ps_update.clearParameters();
					short id = rs.getShort("id");
					ps_update.setShort(1, id);
					//Log.info(ps_update.toString());;
					ps_update.execute();
					connection.commit();
					imageUrls.put(name, url);
					//Log.info(name + "----->>>" + url);
				}
			}
			connection.commit();
			ps_update.close();
			st.close();
			rs.close();
		}
		catch (Exception e) {
			// TODO: handle exception
			Log.equals(e);
		}
	}
}

package com.cl.spider.util;

import java.sql.DriverManager;
import org.apache.log4j.Logger;
import java.sql.Connection;

public class DBConn {
	private static final Logger Log = Logger.getLogger(DBConn.class.getName());
	public static String name;
	public static String pwd;
	public static String url;
	
	public DBConn(){
		
	}
	
	public static Connection getConnection(){
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection(url, name, pwd);
		} catch (Exception e) {
			Log.error(e);
		}
		return connection;
	}
}

package com.cl.spider.util;

import java.util.Map;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;


public class UrlSaveDb {
	private static final Logger Log = Logger.getLogger(UrlSaveDb.class.getName());
	
	public static boolean TechSaveURL(Map<String, String> urls){
		String insertsql = "insert into techdiscuss(title, url, hashCode) values(?, ?, ?)";
		String selectsql = "select * from techdiscuss where hashCode = ?";
		Connection connection = DBConn.getConnection();
		PreparedStatement ps_sel;
		PreparedStatement ps_ins;
		//Statement st = null; 
		ResultSet rs = null;
		
		try {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			ps_sel = connection.prepareStatement(selectsql);
			ps_ins = connection.prepareStatement(insertsql);
			//st = connection.createStatement();
			for(Map.Entry<String, String> entry : urls.entrySet()){
				
				ps_sel.clearParameters();
				ps_sel.setString(1, entry.getValue().hashCode() + "");
				//Log.info(ps_sel.toString());
				rs = ps_sel.executeQuery();
				if(rs.next()) continue;
				//Log.info("fail");
				ps_ins.clearParameters();
				ps_ins.setString(1, entry.getKey());
				ps_ins.setString(2, entry.getValue());
				ps_ins.setString(3, entry.getValue().hashCode() + "");
				//Log.info(ps_ins.toString());
				ps_ins.execute();
				connection.commit();
			}
			connection.commit();
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		Log.info("Good");
		return true;
	}
	
	public static boolean DegalSaveURL(Map<String, String> urls){
		String insertsql = "insert into degalflag(title, url, hashCode) values(?, ?, ?)";
		String selectsql = "select * from degalflag where hashCode = ?";
		Connection connection = DBConn.getConnection();
		PreparedStatement ps_sel;
		PreparedStatement ps_ins;
		//Statement st = null; 
		ResultSet rs = null;
		
		try {
			connection.setAutoCommit(false);
			connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
			ps_sel = connection.prepareStatement(selectsql);
			ps_ins = connection.prepareStatement(insertsql);
			//st = connection.createStatement();
			for(Map.Entry<String, String> entry : urls.entrySet()){
				
				ps_sel.clearParameters();
				ps_sel.setString(1, entry.getValue().hashCode() + "");
				//Log.info(ps_sel.toString());
				rs = ps_sel.executeQuery();
				if(rs.next()) continue;
				//Log.info("fail");
				ps_ins.clearParameters();
				ps_ins.setString(1, entry.getKey());
				ps_ins.setString(2, entry.getValue());
				ps_ins.setString(3, entry.getValue().hashCode() + "");
				//Log.info(ps_ins.toString());
				ps_ins.execute();
				connection.commit();
			}
			connection.commit();
			ps_ins.close();
			ps_ins.close();
			rs.close();
		} catch (Exception e) {
			// TODO: handle exception
			Log.error(e);
		}
		Log.info("Good");
		return true;
	}
}

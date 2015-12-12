package com.cl.spider.util;

public class ImageCount{
	private static int size = 0;
	private static int count = 0;
	
	public synchronized static void setCount(){
		count++;
	}
	
	public synchronized static void setSize(int s){
		size += s;
	}
	
	public static int getCount(){
		return count;
	}
	
	public static int getSize(){
		return size;
	}
}


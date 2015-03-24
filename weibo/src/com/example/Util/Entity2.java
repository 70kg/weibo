package com.example.Util;

import java.io.Serializable;
import java.util.ArrayList;

public class Entity2 implements Serializable{

	String name;
	String content;
	ArrayList<String> weibo_pic;
	String user_pic;
	public ArrayList<String> getWeibo_pic() {
		return weibo_pic;
	}
	public void setWeibo_pic(ArrayList<String> weibo_pic) {
		this.weibo_pic = weibo_pic;
	}
	public String getUser_pic() {
		return user_pic;
	}
	public void setUser_pic(String user_picl) {
		this.user_pic = user_picl;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}

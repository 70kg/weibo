package com.example.Util;

import java.io.Serializable;
import java.util.ArrayList;

import android.graphics.Bitmap;

public class Entity implements Serializable {

	String name;
	String content;
	ArrayList<String> weibo_pic;
	String user_pic;
	ArrayList<String> original_pic;
	String yuanshi_pic;
	public String getYuanshi_pic() {
		return yuanshi_pic;
	}
	public void setYuanshi_pic(String yuanshi_pic) {
		this.yuanshi_pic = yuanshi_pic;
	}
	public ArrayList<String> getOriginal_pic() {
		return original_pic;
	}
	public void setOriginal_pic(ArrayList<String> original_pic) {
		this.original_pic = original_pic;
	}
	Entity2 entity2;
	String id;
	int comments_counts;
	int reposts_count;
	int attitudes_count;                                  
	public int getReposts_count() {
		return reposts_count;
	}
	public void setReposts_count(int reposts_count) {
		this.reposts_count = reposts_count;
	}
	public int getAttitudes_count() {
		return attitudes_count;
	}
	public void setAttitudes_count(int attitudes_count) {
		this.attitudes_count = attitudes_count;
	}
	public int getComments_counts() {
		return comments_counts;
	}
	public void setComments_counts(int comments_counts) {
		this.comments_counts = comments_counts;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ArrayList<String> getWeibo_pic() {
		return weibo_pic;
	}
	public void setWeibo_pic(ArrayList<String> weibo_pic) {
		this.weibo_pic = weibo_pic;
	}

	public String getUser_pic() {
		return user_pic;
	}
	public void setUser_pic(String user_pic) {
		this.user_pic = user_pic;
	}


	public Entity2 getEntity2() {
		return entity2;
	}
	public void setEntity2(Entity2 entity2) {
		this.entity2 = entity2;
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

package com.example.Util;

import com.sina.weibo.sdk.openapi.models.Comment;
import com.sina.weibo.sdk.openapi.models.Status;
import com.sina.weibo.sdk.openapi.models.User;

public class CommentTimeLineEntity {
	/** 评论创建时间 */
	public String created_at;
	/** 评论的 ID */
	public String id;
	/** 评论的内容 */
	public String text;
	/** 评论的来源 */
	public String source;
	/** 评论作者的用户信息字段 */
	public User user;

	/** 评论的 MID */
	public String mid;
	/** 字符串型的评论 ID */
	public String idstr;
	/** 评论的微博信息字段 */
	public Status status;
	/** 评论来源评论，当本评论属于对另一评论的回复时返回此字段 */
	public Comment reply_comment;
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getIdstr() {
		return idstr;
	}
	public void setIdstr(String idstr) {
		this.idstr = idstr;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public Comment getReply_comment() {
		return reply_comment;
	}
	public void setReply_comment(Comment reply_comment) {
		this.reply_comment = reply_comment;
	}
}

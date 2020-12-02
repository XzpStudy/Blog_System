package net.person.blog.pojo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class Article {

	private String id;
	private String title;
	private String userId;
	private String categoryId;
	private String content;
	private String type;
	private String state;
	private String top;
	private String cover;
	private String summary;
	private String label;
	private long viewCount;
	private Date createTime;
	private Date updateTime;


  	private List<String> labels = new ArrayList<>();


	public List<String> getLabels() {
		return labels;
	}

	public void setLabels(List<String> labels) {
		this.labels = labels;
	}

	public String getTop() {
		return top;
	}

	public void setTop(String top) {
		this.top = top;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLabel() {
		if(this.label == null){
			return null;
		}
		this.labels.clear();
		if (!this.label.contains("-")) {
			this.labels.add(this.label);
		}else {
			String[] split = this.label.split("-");
			List<String> strings = Arrays.asList(split);
			this.labels.addAll(strings);
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public long getViewCount() {
		return viewCount;
	}

	public void setViewCount(long viewCount) {
		this.viewCount = viewCount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}

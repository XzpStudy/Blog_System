package net.person.blog.pojo;

import java.util.Date;

public class BlogUser {

	private String id;
	private String userName;
	private String password;
	private String roles;
	private String avatar;
	private String email;
	private String sign;
	private String state;
	private String regIp;
	private String loginIp;
	private Date createTime;
	private Date updateTime;


	public BlogUser() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getRoles() {
		return roles;
	}

	public void setRoles(String roles) {
		this.roles = roles;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}


	public String getRegIp() {
		return regIp;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
	}


	public String getLoginIp() {
		return loginIp;
	}

	public void setLoginIp(String loginIp) {
		this.loginIp = loginIp;
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

	@Override
	public String toString() {
		return "BlogUser{" +
				"id='" + id + '\'' +
				", userName='" + userName + '\'' +
				", password='" + password + '\'' +
				", roles='" + roles + '\'' +
				", avatar='" + avatar + '\'' +
				", email='" + email + '\'' +
				", sign='" + sign + '\'' +
				", state='" + state + '\'' +
				", regIp='" + regIp + '\'' +
				", loginIp='" + loginIp + '\'' +
				", createTime=" + createTime +
				", updateTime=" + updateTime +
				'}';
	}
}

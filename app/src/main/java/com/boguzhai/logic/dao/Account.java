package com.boguzhai.logic.dao;

import java.io.Serializable;

public class Account implements Serializable {
    private static final long serialVersionUID = -2813661484140531241L;
    public String username=""; // primary key
    public String password="";
    public String name="";
    public String email="";
    public String phone="";

    public int sex=-1;      // 1 for man, 0 for female, -1 for unknown.
    public int credits=0;   //not null

    public String wxcode="";      //nullable
    public String photoUrl="";    //nullable
    public String birthday="";    // nullable

	public Account(){}

	/************************ getters and setters ***************************/
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {	return email;	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getWxcode() {
		return wxcode;
	}
	public void setWxcode(String wxcode) {
		this.wxcode = wxcode;
	}
	public String getPhotoUrl() {
		return photoUrl;
	}
	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credits) {
		this.credits = credits;
	}
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

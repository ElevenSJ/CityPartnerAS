package com.lyp.contactsort;

public class QueryContactBean {

	private String gender;//客户性别（0：女，1：男）
	private String ageMax;//最大年龄
	private String ageMin;//最小年龄
	private String maritalStatus;//婚姻状态（0：未婚，1：已婚）
	private String haveChildren;//有无子女（0：无，1：有）
	
	
	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getAgeMax() {
		return ageMax;
	}
	public void setAgeMax(String ageMax) {
		this.ageMax = ageMax;
	}
	public String getAgeMin() {
		return ageMin;
	}
	public void setAgeMin(String ageMin) {
		this.ageMin = ageMin;
	}
	public String getMaritalStatus() {
		return maritalStatus;
	}
	public void setMaritalStatus(String maritalStatus) {
		this.maritalStatus = maritalStatus;
	}
	public String getHaveChildren() {
		return haveChildren;
	}
	public void setHaveChildren(String haveChildren) {
		this.haveChildren = haveChildren;
	}
	
	
	
}

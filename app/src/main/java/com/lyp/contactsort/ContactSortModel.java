package com.lyp.contactsort;

public class ContactSortModel {

    private String name;//显示的数据
    private String sortLetters;//显示数据拼音的首字母
    
    private String id;
    private String birthday;//YYYY-MM-DD
    private String nickname;
    private String gender;// 0:女，1:男  sex
    private String regtime;
    private String cuspoint;
    private String cphone;//telephone
    private String fixedtelephone;
    private String iccardno;
    private String caddress;//logisticsaddress
    private String cemail;//email
    private String avater;//cusicon
    private String marry;//婚姻状态（0未婚1已婚）
    private String district;
    private String unionshopsupid;
    private String zipcode;
    private String codepic;
	public String getName() {
		if (name == null || name.trim().length() <= 0) {
			return cphone == null ? fixedtelephone : cphone;
		}
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getRegtime() {
		return regtime;
	}
	public void setRegtime(String regtime) {
		this.regtime = regtime;
	}
	public String getCuspoint() {
		return cuspoint;
	}
	public void setCuspoint(String cuspoint) {
		this.cuspoint = cuspoint;
	}
	public String getCphone() {
		return cphone;
	}
	public void setCphone(String cphone) {
		this.cphone = cphone;
	}
	public String getFixedtelephone() {
		return fixedtelephone;
	}
	public void setFixedtelephone(String fixedtelephone) {
		this.fixedtelephone = fixedtelephone;
	}
	public String getIccardno() {
		return iccardno;
	}
	public void setIccardno(String iccardno) {
		this.iccardno = iccardno;
	}
	public String getCaddress() {
		return caddress;
	}
	public void setCaddress(String caddress) {
		this.caddress = caddress;
	}
	public String getCemail() {
		return cemail;
	}
	public void setCemail(String cemail) {
		this.cemail = cemail;
	}
	public String getAvater() {
		return avater;
	}
	public void setAvater(String avater) {
		this.avater = avater;
	}
	public String getMarry() {
		return marry;
	}
	public void setMarry(String marry) {
		this.marry = marry;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public String getUnionshopsupid() {
		return unionshopsupid;
	}
	public void setUnionshopsupid(String unionshopsupid) {
		this.unionshopsupid = unionshopsupid;
	}
	public String getZipcode() {
		return zipcode;
	}
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}
	public String getCodepic() {
		return codepic;
	}
	public void setCodepic(String codepic) {
		this.codepic = codepic;
	}
    
}

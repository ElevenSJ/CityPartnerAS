package com.lyp.bean;

public class OrderBean {
	private String orderId;
	private String ordertime;
	private String ordercode;
	private String status;
	private String shopamountpaid;
	private String shopname;
	private String userid;
	private String cusicon;
	private String cusname;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public String getOrdercode() {
		return ordercode;
	}
	public void setOrdercode(String ordercode) {
		this.ordercode = ordercode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getShopamountpaid() {
		return shopamountpaid;
	}
	public void setShopamountpaid(String shopamountpaid) {
		this.shopamountpaid = shopamountpaid;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getCusicon() {
		return cusicon;
	}
	public void setCusicon(String cusicon) {
		this.cusicon = cusicon;
	}
	public String getCusname() {
		return cusname;
	}
	public void setCusname(String cusname) {
		this.cusname = cusname;
	}
	public String getShopname() {
		return shopname;
	}
	public void setShopname(String shopname) {
		this.shopname = shopname;
	}
	
	
//	{
//	    "code": "",
//	    		"data": [
//	{
//	 "status": "",		订单状态(1已付款2未付款)
//	                "ordercode": "",						订单编号
//	                "shopamountpaid": "",				实付金额
//	                "id": "",								订单id
//	                "ordertime": "",						订单时间
//	                "userid": ""，						用户id
//	 "user": {								
//	"cusicon":"",					客户头像
//	"cusname":""					客户名称
//	}
//	]
//	    },
//	    "exception": "",
//	    "message": "查询商户订单成功",
//	    "result": 1,
//	    "success": true
//	}

	
}

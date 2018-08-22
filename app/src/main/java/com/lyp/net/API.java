package com.lyp.net;

public class API {
    public static  final String ROOT_WEB = "http://prj-sss.app-service-node.com";
    
    public static final String API_REGIST = ROOT_WEB + "/registShopByApp";
    /*login interface*/
    public static final String API_LOGIN = ROOT_WEB + "/shopLoginByCheckcode";
    
    /*get sms auth code interface*/
    public static final String API_GET_SMS_AUTH_CODE = ROOT_WEB + "/smsValidCodeToPhone";
    
    /*get customer info list interface*/
    public static final String API_GET_CUSTOMER_LIST = ROOT_WEB + "/queryCustomerList";
    
    /*get person info interface*/
    public static final String API_GET_PERSON_INFO = ROOT_WEB + "/queryShop";
    
    public static  final String API_MY_QRCODE = ROOT_WEB + "/queryShopCodePic";
    
    public static final String API_GET_SHOP_INFO = ROOT_WEB + "/queryShopInfo";
    
    public static final String API_GET_ORDER_LIST = ROOT_WEB + "/queryOrderList";
    
    public static final String API_GET_ORDER_DETAIL = ROOT_WEB + "/queryOrderById";
    
    public static final String API_MODIFY_SHOP_INFO = ROOT_WEB + "/saveShopFromMobile";
    
    public static final String API_GET_SHOP_PIC = ROOT_WEB + "/queryShopPic";
    
    public static final String API_ADD_ORDER = ROOT_WEB + "/addOrder";
    
    public static final String API_PING_PAY_ORDER = ROOT_WEB + "/orderPay";
    
    public static final String API_GET_TOKEN = ROOT_WEB + "/getToken";
    
    public static final String API_SAVE_SHOPPIC = ROOT_WEB + "/saveShopPic";
    
    public static final String API_LOGIN_BY_PWD = ROOT_WEB + "/shopLoginByPassword";
    
    public static final String API_GET_ORDER_LIST_BY_DATE = ROOT_WEB + "/queryOrderByDate";
    
    public static final String API_GET_ORDER_LIST_BY_ID = ROOT_WEB + "/queryOrderByCustomer";
    
    public static final String API_GET_CUSTOMER_DETAIL = ROOT_WEB + "/queryCustomerById";
    
    public static final String API_GET_INCOME_LIST = ROOT_WEB + "/queryShopIncomeHistory";
    
    public static final String API_GET_INCOME_BY_DATE = ROOT_WEB + "/queryShopIncomeByTime";
    
    public static final String API_GET_CUSTOMER_NEED_PAY = ROOT_WEB + "/getCustomerNeedPay";
    
    public static final String API_AFTER_CUSTOMER_PAY = ROOT_WEB + "/afterCustomerPay";
    
    public static final String API_PAY_SIGN = ROOT_WEB + "/getSign";
    
    public static final String API_EXCHANGE_SHOP_POINT = ROOT_WEB + "/addShopCashExchange";
    
    public static final String API_GET_CITY_INFO = ROOT_WEB + "/getCityInfo";
    
    public static final String API_DEL_SHOP_PIC = ROOT_WEB + "/delShopPic";
    
    public static final String API_SAVE_SHOP_ICON = ROOT_WEB + "/saveShopIcon";
    
    public static final String API_WX_PAY_SIGN = ROOT_WEB + "/WXunifiedorder";
    
    public static final String API_QUERY_WX_PAY_RESULT = ROOT_WEB + "/WXorderquery";
}

package com.lyp.utils;

public class Constant {
	public static final String SHARED_PREFERENCE = "member_system";

	public static final String IS_GUIDE = "isGuide";

	public static final String IS_FIRST = "isFirst";

	public static final String USER_NAME = "username";

	public static final String CURRENT_USER_NAME = "current_user_name";

	public static final String USER_PASSWORD = "user_password";
	
	public static final String USER_AVATER = "user_avater";
	
	public static final String ID = "id";
	
	public static final String TOKEN_ID = "token_id";
	
    public static final int DELAY_TIME = 2000;
    
    public static final String CONDITION_ONE = "0";//不启用规则
    
    public static final String CONDITION_TWO = "1";//启用规则，普通通知
    
    public static final String CONDITION_THREE = "2";//启用规则，生日通知
    
    public static final String RELOGIN = "-100";
    
    public static final String TOKEN_INVAILD = "8";//token 失效
    
    public static final String DIFF_DEVICE = "9";//不同设备登录
    
    
	// APP_ID 替换为你的应用从官方网站申请到的合法appId
    public static final String APP_ID = "wx7ca065e1e4e068de";

    public static class ShowMsgActivity {
		public static final String STitle = "showmsg_title";
		public static final String SMessage = "showmsg_message";
		public static final String BAThumbData = "showmsg_thumb_data";
	}
}

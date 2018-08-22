package com.lyp.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtils {
	/**
	*校验邮箱格式
	*/
	public static boolean isEmail(String value){
		boolean flag=false;
		Pattern p1 = null;
		Matcher m = null;
		p1 = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		m = p1.matcher(value);
		flag = m.matches();
		return flag;
	}
	/**
	 * @param checkType 校验类型：0校验手机号码，1校验座机号码，2两者都校验满足其一就可
	 * @param phoneNum
	 * */
	public static boolean validPhoneNum(String checkType,String phoneNum){
		boolean flag=false;
		Pattern p1 = null;
		Pattern p2 = null;
		Matcher m = null;
		p1 = Pattern.compile("^(((13[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1}))+\\d{8})?$");
		p2 = Pattern.compile("^(0[0-9]{2,3}\\-)?([1-9][0-9]{6,7})$");
		if("0".equals(checkType)){
			System.out.println(phoneNum.length());
			if(phoneNum.length()!=11){
				return false;
			}else{
				m = p1.matcher(phoneNum);
				flag = m.matches();
			}
		}else if("1".equals(checkType)){
			if(phoneNum.length()<11||phoneNum.length()>=16){
				return false;
			}else{
				m = p2.matcher(phoneNum);
				flag = m.matches();
			}
		}else if("2".equals(checkType)){
			if(!((phoneNum.length() == 11 && p1.matcher(phoneNum).matches())
					||(phoneNum.length()<16&&p2.matcher(phoneNum).matches()))){
				return false;
			}else{
				flag = true;
			}
		}
		return flag;
	}
	
    public static boolean isMobile(String mobile){  
        String regex = "(\\+\\d+)?1[34578]\\d{9}$";    
        return Pattern.matches(regex, mobile);  
    }  
    /** 
     * 区号+座机号码+分机号码 
     * @param fixedPhone 
     * @return 
     */  
    public static boolean isFixedPhone(String fixedPhone){  
        String reg="(?:(\\(\\+?86\\))(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)|" +  
                "(?:(86-?)?(0[0-9]{2,3}\\-?)?([2-9][0-9]{6,7})+(\\-[0-9]{1,4})?)";  
        return Pattern.matches(reg, fixedPhone);  
    }  
    /**  
     * 匹配中国邮政编码  
     * @param postcode 邮政编码  
     * @return 验证成功返回true，验证失败返回false  
     */   
    public static boolean isPostCode(String postCode){  
        String reg = "[1-9]\\d{5}";  
        return Pattern.matches(reg, postCode);  
    }  
}

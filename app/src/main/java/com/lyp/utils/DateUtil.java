package com.lyp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
	/**
	 * 日期转换成Java字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String DateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String str = format.format(date);
		return str;
	}

	/**
	 * 字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * yyyy-MM-dd字符串转换成日期
	 * 
	 * @param str
	 * @return date
	 */
	public static Date StrToDate2(String str) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * YY-MM-DD HH:MM:SS转换成YY-MM-DD
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr(String str) {
		if (str == null || !str.contains(" ")) {
			return str;
		}
		String[] split = str.split(" ");
		return split[0];
	}

	/**
	 * YY-MM-DD HH:MM:SS转换成HH:mm
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr2(String str) {
		if (str == null) {
			return str;
		}
		String[] split = str.split(" ");
		if (split.length > 1) {
			String time = split[1];
			String[] timeSplit = time.split(":");
			return timeSplit[0] + ":" + timeSplit[1];
		}
		return str;
	}

	/**
	 * YY-MM-DD HH:MM:SS转换成YY-MM-DD HH:mm
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr3(String str) {
		if (str == null) {
			return str;
		}
		String[] split = str.split(" ");
		if (split.length > 1) {
			String time = split[1];
			String[] timeSplit = time.split(":");
			return split[0] + " " + timeSplit[0] + ":" + timeSplit[1];
		}
		return str;
	}
	
	/**
	 * YY-MM-DD HH:MM:SS 转换成 MM-DD
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr4(String str) {
		if (str == null) {
			return str;
		}
		String[] split = str.split(" ");
		if (split.length > 1) {
			String date = split[0];
			String[] dateSplit = date.split("-");
			return dateSplit[1] + " " + dateSplit[2];
		}
		return str;
	}
	
	/**
	 * YY-MM-DD 转换成MM-DD
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr5(String str) {
		if (str == null) {
			return str;
		}
		String[] split = str.split("-");
		if (split.length > 1) {
			return addZeroStr(split[1]) + "-" + addZeroStr(split[2]);
		}
		return str;
	}
	
	public static String addZeroStr(String i) {
		if (i.length() == 1) {
			return "0" + i;
		}
		return i;
	}
	
	
	/**
	 * YY-MM-DD 转换成YY年MM月DD日
	 * 
	 * @param str
	 * @return str
	 */
	public static String stringToStr6(String str) {
		if (str == null) {
			return str;
		}
		String[] split = str.split("-");
		if (split.length > 1) {
			return split[0] + "年" + split[1] + "月" + split[2] + "日";
		}
		return str;
	}

	public static String getIntervalTime(String date1, String date2) {
		if (date1 == null && date2 == null) {
			return "";
		}
		try {
			int intervalTime = (int) ((StrToDate(date2).getTime() - StrToDate(date1)
					.getTime()) / 60000);
			if (intervalTime < 0) {
				intervalTime = intervalTime + 24 * 60;
			}
			int hour = intervalTime / 60;
			int minute = intervalTime - hour * 60;
			if (hour > 0) {
				if (minute == 0) {
					return hour + "小时";
				} else {
					return hour + "小时" + minute + "分";
				}
			} else {
				return minute + "分";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * 返回unix时间戳 (1970年至今的秒数)
	 * 
	 * @return
	 */
	public static long getUnixStamp() {
		return System.currentTimeMillis() / 1000;
	}

	/**
	 * 得到昨天的日期
	 * 
	 * @return
	 */
	public static String getYestoryDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String yestoday = sdf.format(calendar.getTime());
		return yestoday;
	}

	/**
	 * 得到今天的日期:yyyy-MM-dd
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(new Date());
		return date;
	}
	
	/**
	 * 得到某个日期提前几个月的日期
	 * 
	 * @return
	 */
	public static Date getDate(String remindTime, int month) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = format.parse(remindTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 时间戳转化为时间格式
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String timeStampToStr(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}

	/**
	 * 得到日期 yyyy-MM-dd
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String formatDate(long timeStamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(timeStamp * 1000);
		return date;
	}
	
	/**
	 * 得到日期 yyyy-MM-dd
	 * 
	 * @param dateStr
	 *            时间戳
	 * @return
	 */
	public static String formatDateStr(String dateStr) {
		if (dateStr == null || "".equals(dateStr)) {
			return "";
		}
		long timeStamp = Long.parseLong(dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date = sdf.format(timeStamp);
		return date;
	}
	
	/**
	 * 得到日期 yyyy-MM
	 * 
	 * @param dateStr
	 *            时间戳
	 * @return
	 */
	public static String formatDateStr1(String dateStr) {
		if (dateStr == null || "".equals(dateStr)) {
			return "";
		}
		long timeStamp = Long.parseLong(dateStr);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		String date = sdf.format(timeStamp);
		return date;
	}

	/**
	 * 得到时间 HH:mm:ss
	 * 
	 * @param timeStamp
	 *            时间戳
	 * @return
	 */
	public static String getTime(long timeStamp) {
		String time = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(timeStamp * 1000);
		String[] split = date.split("\\s");
		if (split.length > 1) {
			time = split[1];
		}
		return time;
	}

	/**
	 * 将一个时间戳转换成提示性时间字符串，如刚刚，1秒前
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String convertTimeToFormat(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (dateStr == null || "".equals(dateStr)) {
			return "";
		}
		Date date = null;
		try {
			date = format.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long timeStamp = date.getTime() / 1000;
		long curTime = System.currentTimeMillis() / (long) 1000;
		long time = curTime - timeStamp;

		if (time < 60 && time > -30) {
			return "刚刚";
		} else if (time >= 60 && time < 3600) {
			return time / 60 + "分钟前";
		}

		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		// Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);

		Calendar yesterday = Calendar.getInstance(); // 昨天

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);

		Calendar beforeYesterday = Calendar.getInstance(); // 前天

		beforeYesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		beforeYesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		beforeYesterday.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 2);
		beforeYesterday.set(Calendar.HOUR_OF_DAY, 0);
		beforeYesterday.set(Calendar.MINUTE, 0);
		beforeYesterday.set(Calendar.SECOND, 0);

		current.setTime(date);

		if (current.after(today)) {
			return "今天  " + stringToStr2(dateStr);
		} else if (current.before(today) && current.after(yesterday)) {

			return "昨天  " + stringToStr2(dateStr);
		} else if (current.before(yesterday) && current.after(beforeYesterday)) {

			return "前天  " + stringToStr2(dateStr);
		} else {
			return stringToStr3(dateStr);
		}
	}

	/**
	 * 将一个时间戳转换成提示性时间字符串，(多少分钟)
	 * 
	 * @param timeStamp
	 * @return
	 */
	public static String timeStampToFormat(long timeStamp) {
		long curTime = System.currentTimeMillis() / (long) 1000;
		long time = curTime - timeStamp;
		return time / 60 + "";
	}

	public static String getAge(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (dateStr == null || "".equals(dateStr)) {
			return "";
		}
		try {
			Date brithDay = format.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			String currentTime = format.format(calendar.getTime());
			Date today = format.parse(currentTime);
			 
			   return String.valueOf(today.getYear() - brithDay.getYear());
		} catch (ParseException e) {
			e.printStackTrace();
			return dateStr;
		}

	}
	
	public static int getAgeNum(String dateStr) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		if (dateStr == null || "".equals(dateStr)) {
			return 0;
		}
		try {
			Date brithDay = format.parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			String currentTime = format.format(calendar.getTime());
			Date today = format.parse(currentTime);
			 
			   return today.getYear() - brithDay.getYear();
		} catch (ParseException e) {
			e.printStackTrace();
			return 0;
		}

	}
}

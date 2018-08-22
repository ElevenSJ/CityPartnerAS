package com.lyp.database;

import java.util.ArrayList;
import java.util.List;

import com.lyp.contactsort.ContactSortModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
	static private DBManager dbMgr = new DBManager();
	private DBHelper dbHelper;

	void onInit(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	public static synchronized DBManager getInstance() {
		return dbMgr;
	}

	/**
	 * 保存客户list
	 * 
	 * @param contactList
	 */
	synchronized public void saveContactList(List<ContactSortModel> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(CustomerDao.TABLE_NAME, null, null);
			for (ContactSortModel contactSortModel : contactList) {
				ContentValues values = new ContentValues();
				values.put(CustomerDao.COLUMN_NAME_ID, contactSortModel.getId());
				if (contactSortModel.getName() != null)
					values.put(CustomerDao.COLUMN_NAME_NAME, contactSortModel.getName());
				if (contactSortModel.getNickname() != null)
					values.put(CustomerDao.COLUMN_NAME_NICK, contactSortModel.getNickname());
				if (contactSortModel.getAvater() != null)
					values.put(CustomerDao.COLUMN_NAME_AVATAR, contactSortModel.getAvater());
				if (contactSortModel.getBirthday() != null)
					values.put(CustomerDao.COLUMN_NAME_BIRTHDAY, contactSortModel.getBirthday());
				if (contactSortModel.getCodepic() != null)
					values.put(CustomerDao.COLUMN_NAME_CODEPIC, contactSortModel.getCodepic());
				if (contactSortModel.getCaddress() != null)
					values.put(CustomerDao.COLUMN_NAME_ADDRESS, contactSortModel.getCaddress());
				if (contactSortModel.getCemail() != null)
					values.put(CustomerDao.COLUMN_NAME_EMAIL, contactSortModel.getCemail());
				if (contactSortModel.getCphone() != null)
					values.put(CustomerDao.COLUMN_NAME_PHONE, contactSortModel.getCphone());
				if (contactSortModel.getGender() != null)
					values.put(CustomerDao.COLUMN_NAME_GENDER, contactSortModel.getGender());
				if (contactSortModel.getCuspoint() != null)
					values.put(CustomerDao.COLUMN_NAME_CUSPOINT, contactSortModel.getCuspoint());
				if (contactSortModel.getRegtime() != null)
					values.put(CustomerDao.COLUMN_NAME_REGTIME, contactSortModel.getRegtime());
				if (contactSortModel.getMarry() != null)
					values.put(CustomerDao.COLUMN_NAME_MARRY, contactSortModel.getMarry());
				if (contactSortModel.getDistrict() != null)
					values.put(CustomerDao.COLUMN_NAME_DISTRICT, contactSortModel.getDistrict());
				if (contactSortModel.getFixedtelephone() != null)
					values.put(CustomerDao.COLUMN_NAME_FIXEDPHONE, contactSortModel.getFixedtelephone());
				if (contactSortModel.getIccardno() != null)
					values.put(CustomerDao.COLUMN_NAME_ICCARD_NO, contactSortModel.getIccardno());
				if (contactSortModel.getUnionshopsupid() != null)
					values.put(CustomerDao.COLUMN_NAME_UNIONSHOPSUPID, contactSortModel.getUnionshopsupid());
				db.replace(CustomerDao.TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	synchronized public List<ContactSortModel> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<ContactSortModel> contactSortModels = new ArrayList<ContactSortModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + CustomerDao.TABLE_NAME /* + " desc" */, null);
			if (cursor.moveToFirst()) {
				do {
					contactSortModels.add(getContact(cursor));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return contactSortModels;
	}

	/**
	 * 通过生日获取好友list 第一个参数为select语句；
	 * 第二个参数为select语句中占位符参数的值，如果select语句没有使用占位符，该参数可以设置为null。
	 * 带占位符参数的select语句使用例子如下： Cursor cursor = db.rawQuery(
	 * "select * from person where name like ? and age=?", new String[]{"%传智%",
	 * "4"});
	 * 
	 * @return
	 */
	synchronized public List<ContactSortModel> getContactList(String feteDay) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		List<ContactSortModel> contactSortModels = new ArrayList<ContactSortModel>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + CustomerDao.TABLE_NAME + " where " + CustomerDao.COLUMN_NAME_CODEPIC + " = ?",
					new String[] { feteDay });
			if (cursor.moveToFirst()) {
				do {
					contactSortModels.add(getContact(cursor));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return contactSortModels;
	}

	/**
	 * 通过条件获取好友list
	 * 
	 * @return
	 */
//	synchronized public List<ContactSortModel> getContactList(QueryContactBean queryContactBean) {
//		SQLiteDatabase db = dbHelper.getReadableDatabase();
//		String selectSql = "select * from " + CustomerDao.TABLE_NAME + " where ";
//		String where = null;
//		if (queryContactBean.getGender() != null) {
//			selectSql = selectSql + CustomerDao.COLUMN_NAME_GENDER + "=?";
//			where = queryContactBean.getGender();
//		}
//		if (queryContactBean.getHaveChildren() != null) {
//			if (selectSql.contains("?")) {
//				selectSql = selectSql + " and " + CustomerDao.COLUMN_NAME_FIXEDPHONE + "=?";
//				where = where + "," + queryContactBean.getHaveChildren();
//			} else {
//				selectSql = selectSql + CustomerDao.COLUMN_NAME_FIXEDPHONE + "=?";
//				where = queryContactBean.getHaveChildren();
//			}
//		}
//		if (queryContactBean.getMaritalStatus() != null) {
//			if (selectSql.contains("?")) {
//				selectSql = selectSql + " and " + CustomerDao.COLUMN_NAME_MARRY + "=?";
//				where = where + "," + queryContactBean.getMaritalStatus();
//			} else {
//				selectSql = selectSql + CustomerDao.COLUMN_NAME_MARRY + "=?";
//				where = where + queryContactBean.getMaritalStatus();
//			}
//		}
//		if (queryContactBean.getAgeMax() != null) {
//			if (selectSql.contains("?")) {
//			    selectSql = selectSql + " and " + CustomerDao.COLUMN_NAME_AGE + "<=?";
//			    where = where + "," + queryContactBean.getAgeMax();
//			} else {
//				selectSql = selectSql + CustomerDao.COLUMN_NAME_AGE + "<=?";
//				where = where + queryContactBean.getAgeMax();
//			}
//		}
//		if (queryContactBean.getAgeMin() != null) {
//			if (selectSql.contains("?")) {
//			    selectSql = selectSql + " and " + CustomerDao.COLUMN_NAME_AGE + ">=?";
//			    where = where + "," + queryContactBean.getAgeMin();
//			} else {
//				selectSql = selectSql + CustomerDao.COLUMN_NAME_AGE + ">=?";
//				where = where + queryContactBean.getAgeMin();
//			}
//		}
//		String[] whereArgs = null;
//		if (where.contains(",")) {
//			String[] split = where.split(",");
//			switch (split.length) {
//			case 2:
//				whereArgs = new String[]{split[0], split[1]};
//				break;
//			case 3:
//				whereArgs = new String[]{split[0], split[1], split[2]};
//				break;
//			case 4:
//				whereArgs = new String[]{split[0], split[1], split[2], split[3]};
//				break;
//			case 5:
//				whereArgs = new String[]{split[0], split[1], split[2], split[3], split[4]};
//				break;
//			default:
//				break;
//			}
//		} else {
//			whereArgs = new String[]{where};
//		}
//		LogUtils.d("lyp", selectSql + where);
//		List<ContactSortModel> contactSortModels = new ArrayList<ContactSortModel>();
//		if (db.isOpen()) {
//			Cursor cursor = db.rawQuery(selectSql, whereArgs);
//			if (cursor.moveToFirst()) {
//				do {
//					contactSortModels.add(getContact(cursor));
//				} while (cursor.moveToNext());
//			}
//			cursor.close();
//		}
//		return contactSortModels;
//	}

	private ContactSortModel getContact(Cursor cursor) {
		String id = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_ID));
		String name = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_NAME));
		String nick = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_NICK));
		String avater = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_AVATAR));
		String address = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_ADDRESS));
		String birthday = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_BIRTHDAY));
		String codepic = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_CODEPIC));
		String email = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_EMAIL));
		String gender = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_GENDER));
		String phone = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_PHONE));
		String cuspoint = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_CUSPOINT));
		String regtime = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_REGTIME));
		String marry = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_MARRY));
		String district = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_DISTRICT));
		String fixedphone = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_FIXEDPHONE));
		String iccardNo = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_ICCARD_NO));
		String unionshopsupid = cursor.getString(cursor.getColumnIndex(CustomerDao.COLUMN_NAME_UNIONSHOPSUPID));
		ContactSortModel contactSortModel = new ContactSortModel();
		contactSortModel.setId(id);
		contactSortModel.setName(name);
		contactSortModel.setNickname(nick);
		contactSortModel.setAvater(avater);
		contactSortModel.setCaddress(address);
		contactSortModel.setBirthday(birthday);
		contactSortModel.setCodepic(codepic);
		contactSortModel.setCemail(email);
		contactSortModel.setGender(gender);
		contactSortModel.setCphone(phone);
		contactSortModel.setCuspoint(cuspoint);
		contactSortModel.setRegtime(regtime);
		contactSortModel.setMarry(marry);
		contactSortModel.setDistrict(district);
		contactSortModel.setFixedtelephone(fixedphone);
		contactSortModel.setIccardno(iccardNo);
		contactSortModel.setUnionshopsupid(unionshopsupid);
		return contactSortModel;
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	synchronized public ContactSortModel getContact(String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContactSortModel contactSortModel = null;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"select * from " + CustomerDao.TABLE_NAME + " where " + CustomerDao.COLUMN_NAME_ID + " = ?",
					new String[] { id });
			if (cursor.moveToFirst()) {
				contactSortModel = getContact(cursor);
			}
			cursor.close();
		}
		return contactSortModel;
	}

	/**
	 * 删除一个客户
	 * 
	 * @param id
	 */
	synchronized public void deleteContact(String id) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(CustomerDao.TABLE_NAME, CustomerDao.COLUMN_NAME_ID + " = ?", new String[] { id });
		}
	}

	/**
	 * 保存客户
	 * 
	 * @param contact
	 */
	synchronized public void saveContact(ContactSortModel contactSortModel) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			ContentValues values = new ContentValues();
			values.put(CustomerDao.COLUMN_NAME_ID, contactSortModel.getId());
			if (contactSortModel.getName() != null)
				values.put(CustomerDao.COLUMN_NAME_NAME, contactSortModel.getName());
			if (contactSortModel.getNickname() != null)
				values.put(CustomerDao.COLUMN_NAME_NICK, contactSortModel.getNickname());
			if (contactSortModel.getAvater() != null)
				values.put(CustomerDao.COLUMN_NAME_AVATAR, contactSortModel.getAvater());
			if (contactSortModel.getBirthday() != null)
				values.put(CustomerDao.COLUMN_NAME_BIRTHDAY, contactSortModel.getBirthday());
			if (contactSortModel.getCodepic() != null)
				values.put(CustomerDao.COLUMN_NAME_CODEPIC, contactSortModel.getCodepic());
			if (contactSortModel.getCaddress() != null)
				values.put(CustomerDao.COLUMN_NAME_ADDRESS, contactSortModel.getCaddress());
			if (contactSortModel.getCemail() != null)
				values.put(CustomerDao.COLUMN_NAME_EMAIL, contactSortModel.getCemail());
			if (contactSortModel.getCphone() != null)
				values.put(CustomerDao.COLUMN_NAME_PHONE, contactSortModel.getCphone());
			if (contactSortModel.getGender() != null)
				values.put(CustomerDao.COLUMN_NAME_GENDER, contactSortModel.getGender());
			if (contactSortModel.getCuspoint() != null)
				values.put(CustomerDao.COLUMN_NAME_CUSPOINT, contactSortModel.getCuspoint());
			if (contactSortModel.getRegtime() != null)
				values.put(CustomerDao.COLUMN_NAME_REGTIME, contactSortModel.getRegtime());
			if (contactSortModel.getMarry() != null)
				values.put(CustomerDao.COLUMN_NAME_MARRY, contactSortModel.getMarry());
			if (contactSortModel.getDistrict() != null)
				values.put(CustomerDao.COLUMN_NAME_DISTRICT, contactSortModel.getDistrict());
			if (contactSortModel.getFixedtelephone() != null)
				values.put(CustomerDao.COLUMN_NAME_FIXEDPHONE, contactSortModel.getFixedtelephone());
			if (contactSortModel.getIccardno() != null)
				values.put(CustomerDao.COLUMN_NAME_ICCARD_NO, contactSortModel.getIccardno());
			if (contactSortModel.getUnionshopsupid() != null)
				values.put(CustomerDao.COLUMN_NAME_UNIONSHOPSUPID, contactSortModel.getUnionshopsupid());
			db.replace(CustomerDao.TABLE_NAME, null, values);
		}
	}

	synchronized public void closeDB() {
		if (dbHelper != null) {
			dbHelper.closeDB();
		}
	}
}

package com.lyp.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static String DATABASE_NAME = "member_system.db";
	private static final int DATABASE_VERSION=4;
	private static DBHelper instance;
	private static final String CUSTOMER_TABLE_CREATE = "CREATE TABLE "
			+ CustomerDao.TABLE_NAME + " ("
			+ CustomerDao.COLUMN_NAME_NAME + " TEXT, "
			+ CustomerDao.COLUMN_NAME_NICK + " TEXT, "
			+ CustomerDao.COLUMN_NAME_BIRTHDAY + " TEXT, "
			+ CustomerDao.COLUMN_NAME_CODEPIC + " TEXT, "
			+ CustomerDao.COLUMN_NAME_AVATAR + " TEXT, "
			+ CustomerDao.COLUMN_NAME_GENDER + " TEXT, "
			+ CustomerDao.COLUMN_NAME_REGTIME + " TEXT, "
			+ CustomerDao.COLUMN_NAME_CUSPOINT + " TEXT, "
			+ CustomerDao.COLUMN_NAME_PHONE + " TEXT, "
			+ CustomerDao.COLUMN_NAME_ADDRESS + " TEXT, "
			+ CustomerDao.COLUMN_NAME_EMAIL + " TEXT, "
			+ CustomerDao.COLUMN_NAME_MARRY + " TEXT, "
			+ CustomerDao.COLUMN_NAME_DISTRICT + " TEXT, "
			+ CustomerDao.COLUMN_NAME_FIXEDPHONE + " TEXT, "
			+ CustomerDao.COLUMN_NAME_ICCARD_NO + " TEXT, "
			+ CustomerDao.COLUMN_NAME_UNIONSHOPSUPID + " TEXT, "
			+ CustomerDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context.getApplicationContext());
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CUSTOMER_TABLE_CREATE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		if (oldVersion < 2) {
//		    db.execSQL("ALTER TABLE " + CustomerDao.TABLE_NAME + 
//				" ADD COLUMN " +CustomerDao.COLUMN_NAME_DISTRICT +" TEXT");
//		    db.execSQL("ALTER TABLE " + CustomerDao.TABLE_NAME + 
//					" ADD COLUMN " +CustomerDao.COLUMN_NAME_FIXEDPHONE +" TEXT");
//		}
	}

	public void closeDB() {
	    if (instance != null) {
	        try {
	            SQLiteDatabase db = instance.getWritableDatabase();
	            db.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        instance = null;
	    }
	}

}

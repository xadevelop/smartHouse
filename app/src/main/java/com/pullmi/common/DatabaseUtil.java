package com.pullmi.common;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pullmi.entity.Alarm;
import com.pullmi.entity.UnitNode;
import com.pullmi.entity.WareDev;

public class DatabaseUtil {

	private static DatabaseUtil databaseUtil;

	public static final String DATABASE_NAME = "etong.db";

	private static final int DATABASE_VERSION = 1;

	private static Context mCtx;

	private static final String TABLE_NAME_UNIT_NODE = "my_node";
	private static final String TABLE_NAME_DEV_INFO = "dev_info";// 表名
	private static final String TABLE_ALRM_REC_INFO = "alrm_rec";// 表名
	private static final String DEV_INFO_DYH = "dev_dyh";// 单元号字段
	private static final String DEV_INFO_ROOM = "dev_room";// 房号字段
	private static final String DEV_INFO_UID = "dev_uID";// 房号字段

	private DatabaseHelper dbHelper;
	private SQLiteDatabase mDb;
	private static int mMutex = 0;

	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String dbName, int dbVersion) {
			super(context, dbName, null, dbVersion);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}

	private DatabaseUtil(Context ctx) {
		this.mCtx = ctx;
	}

	static public DatabaseUtil getInstance(Context context) {
		if (databaseUtil == null) {
			mCtx = context;
			databaseUtil = new DatabaseUtil(mCtx);
		}
		return databaseUtil;
	}

	public DatabaseUtil open() throws SQLException {
		while (mMutex == 1)
			;
		mMutex = 1;
		dbHelper = new DatabaseHelper(mCtx, DATABASE_NAME, DATABASE_VERSION);
		mDb = dbHelper.getWritableDatabase();
		return this;
	}

	public SQLiteDatabase getDb() {
		return mDb;
	}

	public void close() {
		if (mDb != null) {
			mDb.close();
			dbHelper.close();
			mDb = null;
			mMutex = 0;
		}
	}

	public void createDevNumTb() {
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_DEV_INFO + " ("
				+ "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + DEV_INFO_DYH
				+ " TEXT," + DEV_INFO_ROOM + " TEXT," + DEV_INFO_UID + " TEXT"
				+ ")";
		try {
			open();
			mDb.execSQL(sql);
		} finally {
			close();
		}
	}

	public void createNodeTb() {
		/*
		 * "devid", "devName", "canCpuId", "roomName", "devType", "devCtrlType",
		 */
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_UNIT_NODE
				+ " (" + "_id" + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "devid" + " INTEGER," + "devName" + " TEXT," + "canCpuId"
				+ " TEXT," + "roomName" + " TEXT," + "devType" + " TEXT,"
				+ "devCtrlType" + " TEXT" + ")";
		Log.e("e/", ">>>>>>>>>>>>>>createNodeTb=" + sql);
		try {
			open();
			mDb.execSQL(sql);
		} finally {
			close();
		}
	}

	public boolean getDevInfo_exist() {
		boolean ret = false;
		String sql = " select * from " + TABLE_NAME_DEV_INFO;
		Cursor cursor = null;
		createDevNumTb();
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = true;
				}
			}
		} finally {
			close();
		}
		return ret;
	}

	public void setDevInfo_dyn_room(String dyh, String room) {
		String sql = null;
		createDevNumTb();

		if (getDevInfo_exist()) {
			sql = "update " + TABLE_NAME_DEV_INFO + " set " + DEV_INFO_ROOM
					+ "='" + room + "'," + DEV_INFO_DYH + "='" + dyh + "'";
		} else {
			sql = "insert into " + TABLE_NAME_DEV_INFO + "(" + DEV_INFO_ROOM
					+ "," + DEV_INFO_DYH + ")" + " values(" + room + "," + dyh
					+ ")";
		}
		Log.e("err ", ">>>>>>>>>>>: " + sql);
		try {
			open();
			mDb.execSQL(sql);
		} finally {
			close();
		}
	}

	public String getDevInfo_room() {
		String ret = "0808";
		String sql = " select * from " + TABLE_NAME_DEV_INFO;
		Cursor cursor = null;
		createDevNumTb();
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = cursor
							.getString(cursor.getColumnIndex(DEV_INFO_ROOM));
				}
			}
		} finally {
			close();
		}
		Log.e("get db room", ">>>>>>>>>>>: " + ret);
		return ret;
	}

	public String getDevInfo_dyh() {
		String ret = "000101";
		String sql = " select * from " + TABLE_NAME_DEV_INFO;
		Cursor cursor = null;
		createDevNumTb();
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = cursor.getString(cursor.getColumnIndex(DEV_INFO_DYH));
				}
			}
		} finally {
			close();
		}
		Log.e("get db dyh", ">>>>>>>>>>>: " + ret);
		return ret;
	}

	public void delTbByName(String tbName) {
		String sql = "delete from " + tbName;
		try {
			open();
			mDb.execSQL(sql);
		} finally {
			close();
		}
	}

	public void clearUnitNode() {
		/*
		 * if (databaseUtil.tabIsExist(TABLE_NAME_UNIT_NODE)) {
		 * delTbByName(TABLE_NAME_UNIT_NODE); }
		 */
		delTbByName(TABLE_NAME_UNIT_NODE);
		// createNodeTb();
	}

	/**
	 * 判断某张表是否存在
	 * 
	 * @param tabName
	 *            表名
	 * @return
	 */
	public boolean tabIsExist(String tabName) {
		boolean result = false;
		if (tabName == null) {
			return false;
		}
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = dbHelper.getReadableDatabase();// 此this是继承SQLiteOpenHelper类得到的
			String sql = " select count(*) as c from sqlite_master where type ='table' and name = "
					+ "'+ " + tabName.trim() + "'";
			// String sql;
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToNext()) {
				int count = cursor.getInt(0);
				if (count > 0) {
					result = true;
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}

	public String queryUnitnodeNameByIp(String ip) {
		String name = null;
		Cursor cursor = null;
		createNodeTb();
		try {
			open();
			cursor = mDb.query(TABLE_NAME_UNIT_NODE, new String[] { "name" },
					"ip_addr=?", new String[] { ip }, null, null, null);
			while (cursor.moveToNext()) {
				name = cursor.getString(cursor.getColumnIndex("name"));
			}

		} finally {
			close();
		}
		return name;
	}

	private int queryUnitNodeByID(int id) {
		String sql = " select * from " + TABLE_NAME_UNIT_NODE
				+ " where devid = '" + id + "'";
		Cursor cursor = null;
		int ret = 0;
		// Log.e("err ", ">>>>>>>>>>>: " + sql );
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = cursor.getCount();
				}
				cursor.close();
			}
		} finally {
			close();
		}
		return ret;
	}

	public List<UnitNode> queryUnitNodeByRoomName(String roomName) {
		String sql = " select * from " + TABLE_NAME_UNIT_NODE
				+ " where roomName = '" + roomName + "'";
		Cursor cursor = null;
		List<UnitNode> nodes = new ArrayList<UnitNode>();
		// Log.e("err ", ">>>>>>>>>>>: " + sql );
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					UnitNode node = new UnitNode();
					node.id = cursor.getInt(cursor.getColumnIndex("devid"));
					node.devName = cursor.getString(cursor
							.getColumnIndex("devName"));
					node.canCpuId = cursor.getString(cursor
							.getColumnIndex("canCpuId"));
					node.roomName = cursor.getString(cursor
							.getColumnIndex("roomName"));
					node.devType = cursor.getInt(cursor
							.getColumnIndex("devType"));
					node.devCtrlType = cursor.getInt(cursor
							.getColumnIndex("devCtrlType"));
					nodes.add(node);
				}
				cursor.close();
			}
		} finally {
			close();
		}
		return nodes;
	}

	public long insertUnitNode(WareDev dev) {

		ContentValues values = new ContentValues();
		values.put("devid", dev.devId);
		try {
			values.put("devName", new String(dev.devName, "GB2312"));
			values.put("roomName", new String(dev.roomName, "GB2312"));
			values.put("canCpuId", new String(dev.canCpuId));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		values.put("devType", dev.devType);
		values.put("devCtrlType", dev.devCtrlType);

		try {
			open();
			return mDb.insert(TABLE_NAME_UNIT_NODE, null, values);
		} finally {
			close();
		}
	}

	public long updateUnitNode(WareDev olddDev, WareDev newDev) {

		int ret = 0;
		Cursor cursor = null;
		String sql = null;

		try {
			sql = " delete from " + TABLE_NAME_UNIT_NODE + " where roomName = '"
					+ new String(olddDev.roomName, "GB2312") + "'"
					+ " and canCpuId='" + new String(olddDev.canCpuId)
					+ "' and devName='" + new String(olddDev.devName, "GB2312");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					ret = cursor.getCount();
				}
				cursor.close();
			}
		} finally {
			close();
		}

		insertUnitNode(newDev);
		return ret;
	}

	public List<UnitNode> queryAllUnitNodesByType() {
		List<UnitNode> nodes = new ArrayList<UnitNode>();
		Cursor cursor = null;
		// createNodeTb();
		try {
			open();
			/*
			 * "devid", "devName", "canCpuId", "roomName", "devType",
			 * "devCtrlType",
			 */

			cursor = mDb.query(TABLE_NAME_UNIT_NODE, new String[] { "devid",
					"devName", "canCpuId", "roomName", "devType",
					"devCtrlType", }, null, null, "devType", null, "_id asc");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					UnitNode node = new UnitNode();
					node.id = cursor.getInt(cursor.getColumnIndex("devid"));
					node.devName = cursor.getString(cursor
							.getColumnIndex("devName"));
					node.canCpuId = cursor.getString(cursor
							.getColumnIndex("canCpuId"));
					node.roomName = cursor.getString(cursor
							.getColumnIndex("roomName"));
					node.devType = cursor.getInt(cursor
							.getColumnIndex("devType"));
					node.devCtrlType = cursor.getInt(cursor
							.getColumnIndex("devCtrlType"));
					nodes.add(node);
				}
			}

			// mDb_tmp.close();
			// dbH_tmp.close();
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
		return nodes;
	}

	public List<UnitNode> queryAllUnitNodesByRoom() {
		List<UnitNode> nodes = new ArrayList<UnitNode>();
		Cursor cursor = null;
		// createNodeTb();
		try {
			open();
			/*
			 * "devid", "devName", "canCpuId", "roomName", "devType",
			 * "devCtrlType",
			 */

			cursor = mDb.query(TABLE_NAME_UNIT_NODE, new String[] { "devid",
					"devName", "canCpuId", "roomName", "devType",
					"devCtrlType", }, null, null, "roomName", null, "_id asc");
			if (cursor != null) {
				while (cursor.moveToNext()) {
					UnitNode node = new UnitNode();
					node.id = cursor.getInt(cursor.getColumnIndex("devid"));
					node.devName = cursor.getString(cursor
							.getColumnIndex("devName"));
					node.canCpuId = cursor.getString(cursor
							.getColumnIndex("canCpuId"));
					node.roomName = cursor.getString(cursor
							.getColumnIndex("roomName"));
					node.devType = cursor.getInt(cursor
							.getColumnIndex("devType"));
					node.devCtrlType = cursor.getInt(cursor
							.getColumnIndex("devCtrlType"));
					nodes.add(node);
				}
			}

			// mDb_tmp.close();
			// dbH_tmp.close();
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
		return nodes;
	}

	public long insertAlarm(Alarm alarm) {
		ContentValues values = new ContentValues();
		values.put("alarm_date", alarm.alarmDate);
		values.put("alarm_type", alarm.type);
		values.put("alarm_content", alarm.content);
		values.put("unit_node", alarm.from);
		values.put("alarm_sn", alarm.sn);

		try {
			open();
			return mDb.insert(TABLE_ALRM_REC_INFO, null, values);
		} finally {
			close();
		}
	}

	public List<Alarm> queryAllAlarms() {
		List<Alarm> alarms = new ArrayList<Alarm>();

		createAlarmTb();
		String sql = "" + " select " + " id, " + " alarm_date, "
				+ " alarm_type, " + " alarm_content, " + " unit_node "
				+ " from " + TABLE_ALRM_REC_INFO + " order by id desc";

		Cursor cursor = null;
		try {
			open();
			cursor = mDb.rawQuery(sql, null);
			if (cursor != null) {
				while (cursor.moveToNext()) {
					Alarm alarm = new Alarm();
					alarm.id = cursor.getInt(cursor.getColumnIndex("id"));
					alarm.alarmDate = cursor.getLong(cursor
							.getColumnIndex("alarm_date"));
					alarm.type = cursor.getInt(cursor
							.getColumnIndex("alarm_type"));
					alarm.content = cursor.getString(cursor
							.getColumnIndex("alarm_content"));
					alarm.from = cursor.getString(cursor
							.getColumnIndex("unit_node"));
					alarms.add(alarm);
					// Log.e("err ", ">>>>>>>>>>>>>>>>>>>>>>>>>>>> alrm: " +
					// alarm.content );
				}
			}
		} finally {
			close();
			if (cursor != null) {
				cursor.close();
			}
		}
		return alarms;
	}

	public void deleteAllAlarm() {
		delTbByName(TABLE_ALRM_REC_INFO);
		createAlarmTb();
	}

	public void createAlarmTb() {
		String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_ALRM_REC_INFO + " ("
				+ "id" + " INTEGER PRIMARY KEY AUTOINCREMENT," + "alarm_date"
				+ " LONG," + "alarm_type" + " INTEGER," + "alarm_content"
				+ " TEXT," + "unit_node" + " TEXT," + "dev_inRoom" + " TEXT,"
				+ "alarm_sn" + " INTEGER" + ")";
		try {
			open();
			mDb.execSQL(sql);
		} finally {
			close();
		}
	}
}

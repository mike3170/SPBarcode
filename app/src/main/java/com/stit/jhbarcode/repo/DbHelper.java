package com.stit.jhbarcode.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Singleton
 * getInstance to this instance
 * getDB to get Database
 */
public class DbHelper extends SQLiteOpenHelper {
  private static final String TAG = DbHelper.class.getSimpleName();

  private static final int SCHEMA_VERSION = 1;

  private static final String DB_NAME = "mydb.db";

  private final Context context;

  private static DbHelper instance;  // singleton design pattern
  private static SQLiteDatabase db;

  public synchronized static DbHelper getInstance(Context ctx) {
//    開發時為了欄位忘記開或table忘記開，這兩段可以將資料庫刪除，避免版本號太多
//    File mydb = ctx.getDatabasePath(DB_NAME);
//    Boolean isDel = mydb.delete();
    if (instance == null) {
      instance = new DbHelper(ctx.getApplicationContext());
    }
    return instance;
  }

  private DbHelper(Context context) {
    super(context, DB_NAME, null, SCHEMA_VERSION);

    this.context = context;

    // This will happen in onConfigure for API >= 16
    // if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
    // SQLiteDatabase db = getWritableDatabase();
    // db.enableWriteAheadLogging();
    // db.execSQL("PRAGMA foreign_keys = ON;");
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    System.out.println("sqlite on create");
    String sql = null;

    // 條碼
    sql = "create table main_data ("
        + " _id integer primary key autoincrement, "
        + " proc_emp text, "
        + " kind text, "
        + " bar_code text, "
        + " locate   text, "
        + " scw_job_no text, "
        + " item_no integer, "
        + " isrt_type text, "
        + " reason_code text, "
        + " class_no text, "
        + " pass_yn text, "
        + " scan_date text)";
    db.execSQL(sql);

    sql = "create index main_data_idx1 on main_data(kind, bar_code)";
    db.execSQL(sql);

    sql = "create index main_data_idx2 on main_data(kind, scw_job_no, item_no)";
    db.execSQL(sql);


    // ------------------------------------------
    // cod_mast
    sql = "create table cod_mast ("
        + " kind      text, "
        + " code_no   text, "
        + " code_name text, "
        + " constraint amrs_pk primary key (kind, code_no) "
        + ")";
    db.execSQL(sql);

    //insertArms(db, "100", "100-甲骨文");
    //insertArms(db, "200", "200-Oracle");
    //insertArms(db, "300", "300-Sitit");
    //insertArms(db, "500", "500-Android");
    //insertArms(db, "600", "600-Asus");

    // ------------------------------------------
    // bas_loc
    System.out.println("sqlite create");
    sql = "create table bas_loc ("
            + " dep_no    text, "
            + " loc_no    text, "
            + " constraint loc_pk primary key (dep_no, loc_no) "
            + ")";
    db.execSQL(sql);

    sql = "create table proc_mast ("
            + " proc_no    text, "
            + " proc_name    text, "
            + " constraint proc_pk primary key (proc_no, proc_name) "
            + ")";
    db.execSQL(sql);
  }

  private static void insertArms(SQLiteDatabase db, String code, String name) {
    ContentValues values = new ContentValues();
    values.put("code", code);
    values.put("name", name);
    db.insert("amrs", null, values);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db,
                        int oldVersion,
                        int newVersion) {
    System.out.println("sqlite upgrade");
    System.out.println("onUpgrade: oldversion:"+oldVersion+"newversion:"+newVersion);
    String sql = null;
    // for (int i = (oldVersion + 1); i <= newVersion; i++) {
    //   applySqlFile(db, i);
    //  }
    // bas_loc
    sql = "create table bas_loc ("
            + " dep_no    text, "
            + " loc_no    text, "
            + " constraint amrs_pk primary key (dep_no, loc_no) "
            + ")";
    System.out.println("sqlite onupgrade");
    db.execSQL(sql);

    sql = "create table proc_mast ("
            + " proc_no    text, "
            + " proc_name    text, "
            + " constraint proc_pk primary key (proc_no, proc_name) "
            + ")";
    db.execSQL(sql);
  }

  // helper method
  public synchronized SQLiteDatabase getDb() {
    if (db == null) {
      db = super.getWritableDatabase();
    } else if (! db.isOpen()) {
      db = super.getWritableDatabase();
    }

    return db;
  }

  @Override
  public SQLiteDatabase getWritableDatabase() {
    throw new UnsupportedOperationException("請使用 getDb()");
  }

  @Override
  public SQLiteDatabase getReadableDatabase() {
    throw new UnsupportedOperationException("請使用 getDb()");
  }

  private void applySqlFile(SQLiteDatabase db, int version) {
    BufferedReader reader = null;

    try {
      String filename = String.format("%s.%d.sql", DB_NAME, version);
      final InputStream inputStream = context.getAssets().open(filename);
      reader = new BufferedReader(new InputStreamReader(inputStream));

      final StringBuilder statement = new StringBuilder();

      for (String line; (line = reader.readLine()) != null; ) {
        //if (BuildConfig.DEBUG) {
        //  Log.d(TAG, "Reading line -> " + line);
        //}

        // Ignore empty lines
        if (!TextUtils.isEmpty(line) && !line.startsWith("--")) {
          statement.append(line.trim());
        }

        if (line.endsWith(";")) {
          //if (BuildConfig.DEBUG) {
          //  Log.d(TAG, "Running statement " + statement);
          //}

          db.execSQL(statement.toString());
          statement.setLength(0);
        }
      }

    } catch (IOException e) {
      Log.e(TAG, "Could not apply SQL file", e);
    } finally {
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          Log.w(TAG, "Could not close reader", e);
        }
      }
    }
  }


}  // end class

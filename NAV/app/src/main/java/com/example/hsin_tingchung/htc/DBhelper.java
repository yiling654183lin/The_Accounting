package com.example.hsin_tingchung.htc;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Hello";
    private static final int DATABASE_VERSION = 1;

    public DBhelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS Now (N_UID " +
                "integer primary key autoincrement, " +
                "time text no null, " +
                "O real no null, " +
                "F real no null, " +
                "TEN real no null, " +
                "FT real no null, " +
                "H real no null, " +
                "FH real no null, " +
                "T real no null, " +
                "total real no null, " +
                "Done real no null, " +
                "save integer, " +
                "second text, " +
                "location text, " +
                "FOREIGN KEY(save) REFERENCES Account(UID) " +
                ")");

        db.execSQL("CREATE TABLE IF NOT EXISTS Budget (B_UID " +
                "integer primary key autoincrement, " +
                "name text no null, " +
                "period real no null, " +
                "amount real no null " +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS BudgetItem (BI_UID " +
                "integer primary key autoincrement, " +
                "amount real no null, " +
                "S_date text no null, " +
                "E_date text no null, " +
                "B_UID integer no null, " +
                "total integer, " +
                "FOREIGN KEY(B_UID) REFERENCES Budget(B_UID) " +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS Account (UID " +
                "integer primary key autoincrement, " +
                "Time text no null, " +
                "Money integer no null, " +
                "Memo text" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS Category (C_UID " +
                "integer primary key autoincrement, " +
                "C_Name text no null" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS Tag (T_UID " +
                "integer primary key autoincrement, " +
                "C_UID integer, " +
                "T_name text no null," +
                "FOREIGN KEY(C_UID) REFERENCES Category(C_UID)" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS A_tag (AT_UID " +
                "integer primary key autoincrement, " +
                "A_UID integer no null," +
                "T_UID integer no null," +
                "FOREIGN KEY(A_UID) REFERENCES Account(UID)," +
                "FOREIGN KEY(T_UID) REFERENCES Tag(T_UID)" +
                ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS B_tag (BT_UID " +
                "integer primary key autoincrement, " +
                "BI_UID integer no null," +
                "T_UID integer no null," +
                "FOREIGN KEY(BI_UID) REFERENCES BudgetItem(BI_UID)," +
                "FOREIGN KEY(T_UID) REFERENCES Tag(T_UID)" +
                ")");

        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '食')");
        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '衣')");
        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '住')");
        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '行')");
        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '育')");
        db.execSQL("INSERT OR IGNORE INTO Category (C_UID, C_Name) VALUES (null, '樂')");

        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 1, '早餐')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 1, '午餐')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 1, '晚餐')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 1, '消夜')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 1, '飲料')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 2, '年中慶')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 2, '周年慶')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 3, '房租')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 3, '水電')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 4, '大眾運輸')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 4, '燃料稅')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 5, '書')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 5, '展覽')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 6, '畢業旅行')");
        db.execSQL("INSERT OR IGNORE INTO Tag (T_UID, C_UID, T_Name) VALUES (null, 6, '家庭聚會')");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Now");
        db.execSQL("DROP TABLE IF EXISTS S_tag");
        db.execSQL("DROP TABLE IF EXISTS I_tag");
        db.execSQL("DROP TABLE IF EXISTS Tag");
        db.execSQL("DROP TABLE IF EXISTS Category");
        db.execSQL("DROP TABLE IF EXISTS Account");
        db.execSQL("DROP TABLE IF EXISTS Income");
        db.execSQL("DROP TABLE IF EXISTS BudgetItem");
        db.execSQL("DROP TABLE IF EXISTS Budget");
        onCreate(db);
    }
}

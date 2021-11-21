package com.sangsolutions.stock.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Adapter;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class DatabaseHelper extends SQLiteOpenHelper {

    final Context context;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Stock.db";
    private static final String TABLE_PRODUCT = "tbl_Product";
    private static final String TABLE_USER = "user";
    private static final String TABLE_CURRENT_LOGIN = "current_login";
    private static final String TABLE_WAREHOUSE = "tbl_warehouse";
    private static final String TABLE_STOCK_COUNT = "tbl_StockCount";


    //Product

    //PendingSO
    private static final String DOC_NO = "DocNo";
    private static final String DOC_DATE = "DocDate";
    private static final String HEADER_ID = "HeaderId";
    private static final String SI_NO = "SiNo";
    private static final String CUSTOMER = "Cusomer";//spelling is not right
    private static final String I_CUSTOMER_ID = "iCustomer";
    private static final String PRODUCT = "Product";
    private static final String QTY = "Qty";
    private static final String TEMP_QTY = "TempQty";

    //tbl_DeliveryNote
    private static final String I_STATUS = "iStatus";

    private static final String I_ID = "iId";
    private static final String S_LOGIN_NAME = "sLoginName";
    private static final String S_PASSWORD = "sPassword";

    //current_login
    private static  final String USER_ID = "uId";

    //stock count
    private static  final String S_VOUCHER_NO = "sVoucherNo";
    private static  final String D_DATE = "dDate";
    private static  final String I_WAREHOUSE = "iWarehouse";
    private static  final String I_PRODUCT = "iProduct";
    private static  final String F_QTY = "fQty";
    private static  final String S_UNIT = "sUnit";
    private static  final String I_USER = "iUser";
    private static  final String S_REMARKS = "sRemarks";
    private static  final String D_PROCESSED_DATE ="dProcessedDate";
    private static final String S_NARRATION = "sNarration";
    private static final String D_STOCK_COUNT_DATE ="dStockCountDate";

    //goods receipt header
    private static final String I_SUPPLIER = "iSupplier";
    private static final String S_PONO = "sPONo";

    //goods receipt body
    private static final String S_MINOR_REMARKS = "sMinorRemarks";
    private static final String S_DAMAGED_REMARKS = "sDamagedRemarks";
    private static final String F_MINOR_DAMAGE_QTY = "fMinorDamageQty";
    private static final String F_DAMAGED_QTY = "fDamagedQty";
    private static final String F_PO_QTY = "fPOQty";
    private static final String S_MINOR_ATTACHMENT = "sMinorAttachment";
    private static final String S_DAMAGED_ATTACHMENT = "sDamagedAttachment";
    private static final String I_MINOR_TYPE = "iMinorId";
    private static final String I_DAMAGED_TYPE = "iDamagedId";

    //goods receipt damage type
    private static final String S_NAME  = "sName";

    //delivery note header
    private static final String S_SALESMAN = "sSalesman";
    private static final String S_CONTACT_PERSON = "sContactPerson";
    private static final String S_SO_NOS = "sSONos";
    private static final String I_CUSTOMER = "iCustomerRef";
    private static final String S_DATE = "sDate";


    //delivery note body
    private static final String S_ITEM_CODE = "iItemCode";
    private static final String S_DESCRIPTION = "sDescription";
    private static final String S_ATTACHMENT = "sAttachment";
    private static final String S_SONo = "sSONo";
    private static final String F_SO_QTY = "sSOQty";
    private static final String S_SONO = "sSONo";

    //GRN without po header
    private static final String S_REF_NO = "sRefNo";


    //User
    private static final String S_MENU_IDS = "sMenuIDs";




    //create table Product
    private static final String CREATE_TABLE_PRODUCT = "create table if not exists  " + TABLE_PRODUCT + " (" +
            "" + Product.I_ID + " INTEGER PRIMARY KEY ," +
            "" + Product.PRODUCT + " VARCHAR(65) DEFAULT null  ," +
            "" + Product.CODE + "  VARCHAR(50) DEFAULT null ," +
            "" + Product.BARCODE + "  VARCHAR(30) DEFAULT null ," +
            "" + Product.UNIT + " VARCHAR(20) DEFAULT null " +
            ")";

    private static final String CREATE_CURRENT_LOGIN = "create table if not exists  " + TABLE_CURRENT_LOGIN + " (" +
            "" + I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" + USER_ID + " INTEGER DEFAULT null," +
            "" + S_MENU_IDS + " TEXT DEFAULT null" +
            ")";


    //create table User
    private static final String CREATE_TABLE_USER = "create table if not exists  " + TABLE_USER + " (" +
            "" + I_ID + " INTEGER DEFAULT 0, " +
            "" + S_LOGIN_NAME + " TEXT DEFAULT null," +
            "" + S_PASSWORD + " TEXT DEFAULT null," +
            "" + S_MENU_IDS + " TEXT DEFAULT null" +
            ");";


    //create table warehouse
    private static final String CREATE_TABLE_WAREHOUSE = "create table if not exists  " + TABLE_WAREHOUSE + " (" +
            "" + Warehouse.I_ID + " TEXT DEFAULT null," +
            "" + Warehouse.WAREHOUSE + " TEXT(60) DEFAULT null" + ");";


    //create table StockCount
    private static final String CREATE_TABLE_STOCK_COUNT = "create table if not exists " + TABLE_STOCK_COUNT + " (" +
            "" + S_VOUCHER_NO + " TEXT(30) DEFAULT null ," +
            "" + D_DATE + "  TEXT(10) DEFAULT null," +
            "" + D_STOCK_COUNT_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_USER + " INTEGER DEFAULT 0,"+
            "" + I_WAREHOUSE + "  INTEGER DEFAULT 0," +
            "" + I_PRODUCT + "  INTEGER DEFAULT 0," +
            "" + F_QTY + "  TEXT(10) DEFAULT null," +
            "" + S_UNIT + "  TEXT(10) DEFAULT null," +
            "" + S_NARRATION + "  TEXT(50) DEFAULT null," +
            "" + S_REMARKS + "  TEXT(50) DEFAULT null," +
            "" + D_PROCESSED_DATE + "  TEXT(10) DEFAULT null," +
            "" + I_STATUS + "  TEXT(10) DEFAULT null" +
            ")";


    private SQLiteDatabase db;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        db.execSQL(CREATE_TABLE_PRODUCT);

        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_CURRENT_LOGIN);
        db.execSQL(CREATE_TABLE_WAREHOUSE);
        db.execSQL(CREATE_TABLE_STOCK_COUNT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; ++i) {
            String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
            Log.d("databasehelper", "Looking for migration file: " + migrationName);
//            readAndExecuteSQLScript(db, context, migrationName);
        }
        onCreate(db);
    }




//Tools for sqlite exicution






/////////////////////////////////////////////////////




//user
    public boolean InsertUsers(User u){
        this.db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(I_ID,u.getsId());
        cv.put(S_LOGIN_NAME, u.getsLoginName());
        cv.put(S_PASSWORD, u.getsPassword());
        cv.put(S_MENU_IDS,u.getsMenuIDs());
        float status = db.insert(TABLE_USER, null, cv);
        return status != -1;
    }

    public boolean DeleteUser() {
        this.db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_USER);
        return true;
    }

    public String GetLoginUser(){
        this.db = getReadableDatabase();
        Cursor cursor =  db.rawQuery("SELECT u.sLoginName username from user u " +
                "INNER join current_login c " +
                "on u.iId = c.uId",null);
        if(cursor!=null){
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex("username"));
        }
        return "";
    }


    public boolean InsertProduct(List<Product> productsList){
        this.db = getWritableDatabase();
        float status = -1;

        db.execSQL("delete from "+ TABLE_PRODUCT);

        for (int i = 0; i < productsList.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(Product.I_ID,productsList.get(i).getMasterId());
            cv.put(Product.PRODUCT, productsList.get(i).getName());
            cv.put(Product.CODE, productsList.get(i).getCode());
            cv.put(Product.BARCODE, productsList.get(i).getBarcode());
            cv.put(Product.UNIT, productsList.get(i).getUnit());
            status = db.insert(TABLE_PRODUCT, null, cv);
        }



        return status != -1;
    }

    public boolean InsertWareHose(List<Warehouse> warehouseList) {
        this.db = getWritableDatabase();
        float status = -1;
        db.execSQL("delete from "+ TABLE_WAREHOUSE);
        for (int i = 0; i < warehouseList.size(); i++) {
            ContentValues cv = new ContentValues();

            cv.put(Warehouse.WAREHOUSE,warehouseList.get(i).getName() );
            cv.put(Warehouse.WAREHOUSE,warehouseList.get(i).getName());
            status = db.insert(TABLE_WAREHOUSE, null, cv);
        }
        return status != -1;

    }

    public boolean GetUser() {

        this.db = getReadableDatabase();
        boolean status = false;
        Cursor cursor = db.rawQuery("select * from " + TABLE_USER, null);
        if (cursor.getCount() > 0) {
            status = true;
        }
        cursor.close();
        return status;
    }

    public Cursor loginUser(User u) {
        this.db = getReadableDatabase();

        String username = u.getsLoginName();
        String password = u.getsPassword();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from " + TABLE_USER + " where " +
                        S_LOGIN_NAME + " = ? and " + S_PASSWORD + " = ?", new String[]{username, password});
            cursor.moveToFirst();

        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return cursor;
    }

    public boolean InsertCurrentLoginUser(User u) {
        this.db = getReadableDatabase();
        this.db = getWritableDatabase();

        Cursor cursor = db.rawQuery("select " + I_ID +","+S_MENU_IDS+" from " + TABLE_USER + " where " + S_LOGIN_NAME + "='" + u.getsLoginName() + "' and " + S_PASSWORD + "='" + u.getsPassword() + "'", null);
        ContentValues cv = new ContentValues();
        if (cursor.moveToFirst()) {

            cv.put(USER_ID, cursor.getInt(cursor.getColumnIndex(I_ID)));
            cv.put(S_MENU_IDS,cursor.getString(cursor.getColumnIndex(S_MENU_IDS)));
        }
        float status = db.insert(TABLE_CURRENT_LOGIN, null, cv);

        return status != -1;
    }

    public boolean GetLoginStatus() {

        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CURRENT_LOGIN + "", null);
        return cursor.getCount() > 0;
    }

    public Cursor GetWarehouse() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from "+TABLE_WAREHOUSE,null);
        if(cursor.moveToFirst()){
            return cursor;
        }else {
            return null;
        }
    }

    public Cursor GetProductInfo(String Keyword){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_PRODUCT + " where " + Product.CODE
                + " like ? or " + Product.PRODUCT + " like ? group by " + Product.PRODUCT + " limit 10 ",
                new String[]{Keyword + "%", Keyword + "%"});
        if (cursor.moveToFirst()) {


            return cursor;
        }else {
            return  null;
        }
    }

    public boolean DeleteCurrentUser(){
        this.db = getWritableDatabase();
        float status = db.delete(TABLE_CURRENT_LOGIN,null,null);
        return status!=-1;
    }

    public String GetProductUnit(String Barcode){
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT "+Product.UNIT+" FROM "+TABLE_PRODUCT+" WHERE "+Product.BARCODE+" = ? ",new String[]{Barcode});
        if (cursor.moveToFirst()) {
            return cursor.getString(cursor.getColumnIndex(Product.UNIT));
        }else {
            return  "";
        }
    }
}

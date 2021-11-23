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

import com.sangsolutions.stock.Adapter.BodyAdapter.StockBody;
import com.sangsolutions.stock.Adapter.BodyAdapter.StockHeader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class DatabaseHelper extends SQLiteOpenHelper {

    final Context context;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Stock.db";
    private static final String TABLE_PRODUCT = "t_m_Product";
    private static final String TABLE_USER = "t_s_User";
    private static final String TABLE_CURRENT_LOGIN = "t_s_current_login";
    private static final String TABLE_WAREHOUSE = "t_m_warehouse";
    private static final String TABLE_STOCK_COUNT_HEADER = "t_t_StockCountHeader";
    private static final String TABLE_STOCK_COUNT_BODY = "t_t_StockCountBody";

    private static final String I_ID = "iId";
    private static final String S_LOGIN_NAME = "sLoginName";
    private static final String S_PASSWORD = "sPassword";

    //current_login
    private static  final String USER_ID = "uId";

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
    private static final String CREATE_TABLE_STOCK_COUNT_HEADER = "create table if not exists " + TABLE_STOCK_COUNT_HEADER + " (" +
            "" + StockHeader.I_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "" +  StockHeader.S_VOUCHER_NO + " VARCHAR(50) DEFAULT null ," +
            "" +  StockHeader.D_DATE + "  VARCHAR(20) DEFAULT null ," +
            "" +  StockHeader.D_STOCK_COUNT_DATE + "  TEXT(10) DEFAULT null," +
            "" +  StockHeader.I_WAREHOUSE + "  INTEGER DEFAULT 0," +
            "" +  StockHeader.S_NARRATION + "  VARCHAR(100) DEFAULT null ," +
            "" +  StockHeader.D_PROCESSED_DATE + "  VARCHAR(30) DEFAULT null ," +
            "" +  StockHeader.I_STATUS + "  TEXT(10) DEFAULT null" +
            ")";
    private static final String CREATE_TABLE_STOCK_COUNT_BODY = "create table if not exists " + TABLE_STOCK_COUNT_BODY + " (" +
            "" + StockBody.I_ID + " INTEGER DEFAULT 0, " +
            "" + StockBody.I_PRODUCT + "  INTEGER DEFAULT 0," +
            "" + StockBody.PRODUCT + "  VARCHAR(100) DEFAULT null ," +
            "" + StockBody.BARCODE + "  VARCHAR(50) DEFAULT null ," +
            "" + StockBody.F_QTY + " VARCHAR(20) DEFAULT null ," +
            "" + StockBody.S_UNIT + "  VARCHAR(10) DEFAULT null ," +
            "" + StockBody.S_REMARKS + "  VARCHAR(100) DEFAULT null " +
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
        db.execSQL(CREATE_TABLE_STOCK_COUNT_BODY);
        db.execSQL(CREATE_TABLE_STOCK_COUNT_HEADER);
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

            cv.put(Warehouse.I_ID,warehouseList.get(i).getMasterId() );
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

    public boolean insertStockData(StockHeader stockHeader, List<StockBody> bodyPartList) {
        this.db = getReadableDatabase();
        this.db = getWritableDatabase();
        this.db.beginTransaction();
        float success = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put(StockHeader.D_DATE, stockHeader.getdDate());
            cv.put(StockHeader.D_PROCESSED_DATE, stockHeader.getdProcessedDate());
            cv.put(StockHeader.D_STOCK_COUNT_DATE, stockHeader.getdStockCountDate());
            cv.put(StockHeader.I_WAREHOUSE, stockHeader.getiWarehouse());
            cv.put(StockHeader.S_NARRATION, stockHeader.getsNarration());
            cv.put(StockHeader.S_VOUCHER_NO, stockHeader.getsVoucherNo());


            long iTransId = db.insert(TABLE_STOCK_COUNT_HEADER, null, cv);
            Log.d("stockHeaderId", iTransId + "");
            if (iTransId != -1) {
                for (int i = 0; i < bodyPartList.size(); i++) {
                    ContentValues cvBody = new ContentValues();
                    cvBody.put(StockBody.I_ID, iTransId);
                    cvBody.put(StockBody.I_PRODUCT, bodyPartList.get(i).getiProduct());
                    cvBody.put(StockBody.PRODUCT, bodyPartList.get(i).getName());
                    cvBody.put(StockBody.BARCODE, bodyPartList.get(i).getBarcode());
                    cvBody.put(StockBody.F_QTY, bodyPartList.get(i).getQty());
                    cvBody.put(StockBody.S_UNIT, bodyPartList.get(i).getUnit());
                    cvBody.put(StockBody.S_REMARKS, bodyPartList.get(i).getsRemarks());


                    float status = db.insert(TABLE_STOCK_COUNT_BODY, null, cvBody);
                    Log.d("salesBodyId", status + "");
                }
            }
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            success=-1;
            Log.d("salesBodyIdExce", e.getMessage() + "");
        } finally {
            this.db.endTransaction();
        }
        db.close();
        return success != -1;
    }

    public Cursor GetStockCountHeader() {

        this.db = getReadableDatabase();
        Cursor cursor;
            cursor = db.rawQuery("SELECT * FROM "+TABLE_STOCK_COUNT_HEADER,null);

        if(cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public String GetWarehouseById(String id) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + Warehouse.WAREHOUSE + " from " + TABLE_WAREHOUSE + " where " + Warehouse.I_ID + " = ?", new String[]{id});

        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        } else {
            return "";
        }
    }

    public boolean DeleteStockwithId(String iId) {
        this.db = getWritableDatabase();
        this.db = getReadableDatabase();
        Log.d("tbl_StockCount_Body",iId);


        List<String >idString= Arrays.asList(iId.split(","));


        for (int i=0;i<idString.size();i++){
            Log.d("idString",idString.get(i)+"");
            long delete=db.delete(TABLE_STOCK_COUNT_HEADER,StockHeader.I_ID+"=?",new String[]{idString.get(i)});
            long deleteBody=db.delete(TABLE_STOCK_COUNT_BODY,StockBody.I_ID+"=?",new String[]{idString.get(i)});
        }


//        Log.d("deletedelete","delete from "+TABLE_STOCK_COUNT_HEADER+" where "+StockHeader.I_ID+" in("+iId+")");
//        Cursor cursor = db.rawQuery("delete from "+TABLE_STOCK_COUNT_HEADER+" where "+StockHeader.I_ID+" in(28)",null);
//        Cursor cursorbody = db.rawQuery("delete from " + TABLE_STOCK_COUNT_BODY +  " where " + StockBody.I_ID +" in(28)",null);

        return true;
    }

    public Cursor GetHeaderData(int iId) {

        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STOCK_COUNT_HEADER + " where " + StockHeader.I_ID + " = ? ", new String[]{String.valueOf(iId)});
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }

    }

    public Cursor GetBodyData(int iId) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_STOCK_COUNT_BODY+" where "+ StockBody.I_ID +" = ? ", new String[]{String.valueOf(iId)});
        if (cursor.moveToFirst()) {
            return cursor;
        } else {
            return null;
        }
    }

    public boolean updateStockData(StockHeader stockHeader, List<StockBody> bodyPartList, int iId) {
        this.db = getReadableDatabase();
        this.db = getWritableDatabase();
        this.db.beginTransaction();
        float success = 0;
        try {
            ContentValues cv = new ContentValues();
            cv.put(StockHeader.D_DATE, stockHeader.getdDate());
            cv.put(StockHeader.D_STOCK_COUNT_DATE, stockHeader.getdStockCountDate());
            cv.put(StockHeader.I_WAREHOUSE, stockHeader.getiWarehouse());
            cv.put(StockHeader.S_NARRATION, stockHeader.getsNarration());
            cv.put(StockHeader.S_VOUCHER_NO, stockHeader.getsVoucherNo());

            long iTransId= db.update(TABLE_STOCK_COUNT_HEADER, cv, StockHeader.I_ID + "=?",
                    new String[]{String.valueOf(iId)});

            float delete = db.delete(TABLE_STOCK_COUNT_BODY, StockBody.I_ID + " =  ? ",
                    new String[]{String.valueOf(iId)});
            Log.d("stockHeaderId", iTransId + " "+delete);
            if (iTransId != -1) {
                for (int i = 0; i < bodyPartList.size(); i++) {
                    ContentValues cvBody = new ContentValues();
                    cvBody.put(StockBody.I_ID, iId);
                    cvBody.put(StockBody.I_PRODUCT, bodyPartList.get(i).getiProduct());
                    cvBody.put(StockBody.PRODUCT, bodyPartList.get(i).getName());
                    cvBody.put(StockBody.BARCODE, bodyPartList.get(i).getBarcode());
                    cvBody.put(StockBody.F_QTY, bodyPartList.get(i).getQty());
                    cvBody.put(StockBody.S_UNIT, bodyPartList.get(i).getUnit());
                    cvBody.put(StockBody.S_REMARKS, bodyPartList.get(i).getsRemarks());


                    float status = db.insert(TABLE_STOCK_COUNT_BODY, null, cvBody);
                    Log.d("salesBodyId", status + "");
                }
            }
            this.db.setTransactionSuccessful();
        } catch (Exception e) {
            success=-1;
            Log.d("salesBodyIdExce", e.getMessage() + "");
        } finally {
            this.db.endTransaction();
        }
        db.close();
        return success != -1;
    }

    public Cursor GetProductInfoByBarcode(String barcode) {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select "+Product.I_ID+","+Product.PRODUCT+" from "+TABLE_PRODUCT+" where "+Product.BARCODE+" = ? ",new String[]{barcode});
        if (cursor.moveToFirst()) {


            return cursor;
        }else {
            return  null;
        }
    }

    public String GetUserId() {
        this.db = getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_CURRENT_LOGIN, null);
        if (cursor.moveToFirst()) {
            String UserId = cursor.getString(cursor.getColumnIndex(USER_ID));
            cursor.close();
            return UserId;
        }
        else {
            cursor.close();
            return null;
        }
    }


}

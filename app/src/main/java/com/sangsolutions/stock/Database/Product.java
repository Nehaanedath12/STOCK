package com.sangsolutions.stock.Database;

public class Product {


    private String Name,Code,Barcode,Unit;
    int MasterId;

    public Product() {
    }

    public Product(String name, String code, String barcode, String unit, int masterId) {
        Name = name;
        Code = code;
        Barcode = barcode;
        Unit = unit;
        MasterId = masterId;
    }

    public int getMasterId() {
        return MasterId;
    }

    public void setMasterId(int masterId) {
        MasterId = masterId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public static final String I_ID = "MasterId";
    public static final String PRODUCT = "Name";
    public static final String CODE = "Code";
    public static final String BARCODE = "Barcode";
    public static final String UNIT = "Unit";


}

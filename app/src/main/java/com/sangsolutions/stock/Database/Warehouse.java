package com.sangsolutions.stock.Database;

public class Warehouse {

    String MasterId,Name;


    public String getMasterId() {
        return MasterId;
    }

    public void setMasterId(String masterId) {
        MasterId = masterId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public static final String I_ID = "MasterId";
    public static final String WAREHOUSE = "Name";
}

package com.sangsolutions.stock.Database;

public class User {
    String sId,sLoginName,sPassword,sMenuIDs;


    public String getsMenuIDs() {
        return sMenuIDs;
    }

    public void setsMenuIDs(String sMenuIDs) {
        this.sMenuIDs = sMenuIDs;
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getsLoginName() {
        return sLoginName;
    }

    public void setsLoginName(String sLoginName) {
        this.sLoginName = sLoginName;
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }
}

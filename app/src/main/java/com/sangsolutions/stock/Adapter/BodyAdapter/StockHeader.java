package com.sangsolutions.stock.Adapter.BodyAdapter;

public class StockHeader {


    String sVoucherNo,dDate,dProcessedDate,sNarration,dStockCountDate;
    int iWarehouse;

    public StockHeader(String sVoucherNo, String dDate, String dProcessedDate, String sNarration,
                       String dStockCountDate, int iWarehouse) {
        this.sVoucherNo = sVoucherNo;
        this.dDate = dDate;
        this.dProcessedDate = dProcessedDate;
        this.sNarration = sNarration;
        this.dStockCountDate = dStockCountDate;
        this.iWarehouse = iWarehouse;
    }

    public String getsVoucherNo() {
        return sVoucherNo;
    }

    public String getdDate() {
        return dDate;
    }

    public String getdProcessedDate() {
        return dProcessedDate;
    }

    public String getsNarration() {
        return sNarration;
    }

    public String getdStockCountDate() {
        return dStockCountDate;
    }

    public int getiWarehouse() {
        return iWarehouse;
    }

    public static  final String I_ID = "iId";
    public static  final String S_VOUCHER_NO = "sVoucherNo";
    public static  final String D_DATE = "dDate";
    public static  final String I_WAREHOUSE = "iWarehouse";
    public static  final String I_USER = "iUser";
    public static  final String D_PROCESSED_DATE ="dProcessedDate";
    public static final String S_NARRATION = "sNarration";
    public static final String D_STOCK_COUNT_DATE ="dStockCountDate";
    public static final String I_STATUS = "iStatus";
}

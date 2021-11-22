package com.sangsolutions.stock.Adapter.StockHistoryAdapter;

public class StockCountList {
    String VNo,Date,TotalQty,Warehouse,WarehouseId;
    int iId;


    public StockCountList(String VNo, String date, String totalQty, String warehouse, String WarehouseId,int iId) {
        this.VNo = VNo;
        this.Date = date;
        this.TotalQty = totalQty;
        this.Warehouse = warehouse;
        this.WarehouseId = WarehouseId;
        this.iId=iId;
    }

    public int getiId() {
        return iId;
    }

    public String getVNo() {
        return VNo;
    }

    public void setVNo(String VNo) {
        this.VNo = VNo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTotalQty() {
        return TotalQty;
    }

    public void setTotalQty(String totalQty) {
        TotalQty = totalQty;
    }

    public String getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }

    public String getWarehouseId() {
        return WarehouseId;
    }

    public void setWarehouseId(String warehouseId) {
        WarehouseId = warehouseId;
    }
}

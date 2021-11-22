package com.sangsolutions.stock.Adapter.BodyAdapter;

@SuppressWarnings("ALL")
public class StockBody {


    String Name,Code,Qty,Unit,iProduct,sRemarks,barcode;

    public StockBody() {
    }

    public StockBody(String name, String code, String qty, String unit, String iProduct, String sRemarks, String barcode) {
        this.Name = name;
        this.Code = code;
        this.Qty = qty;
        this.Unit = unit;
        this.iProduct = iProduct;
        this.sRemarks = sRemarks;
        this.barcode=barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
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

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getiProduct() {
        return iProduct;
    }

    public void setiProduct(String iProduct) {
        this.iProduct = iProduct;
    }

    public String getsRemarks() {
        return sRemarks;
    }

    public void setsRemarks(String sRemarks) {
        this.sRemarks = sRemarks;
    }

    public static final String I_ID = "iId";
    public static final String I_PRODUCT = "iProduct";
    public static final String PRODUCT = "sProduct";
    public static final String BARCODE = "barcode";
    public static final String F_QTY = "fQty";
    public static final String S_UNIT = "sUnit";
    public static final String S_REMARKS = "sRemarks";
}

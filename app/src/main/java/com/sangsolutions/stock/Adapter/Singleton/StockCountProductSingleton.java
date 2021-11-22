package com.sangsolutions.stock.Adapter.Singleton;

import com.sangsolutions.stock.Adapter.BodyAdapter.StockBody;

import java.util.ArrayList;
import java.util.List;

public class StockCountProductSingleton {
    private static StockCountProductSingleton stockCountProductSingleton;
    public List<StockBody> list = new ArrayList<>();

    private StockCountProductSingleton(){}

    public static StockCountProductSingleton getInstance(){
        if(stockCountProductSingleton ==null){
            stockCountProductSingleton = new StockCountProductSingleton();
        }
        return stockCountProductSingleton;
    }

    public List<StockBody> getList(){return list;}
    public void setList(List<StockBody> list){
        this.list = list;
    }
    public void clearList(){list.clear();}
}

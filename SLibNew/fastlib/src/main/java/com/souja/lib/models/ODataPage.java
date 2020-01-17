package com.souja.lib.models;

import java.io.Serializable;

/**
 * 服务器分页Model
 * Created by Yangdz on 2016/8/19 0019.
 */
public class ODataPage implements Serializable {

    private int totalPages = 0;
    private int curPage = 1;
    private int pageSize = 0;
    private int total = 0;
    private int extTotal = 0;
    private double totalValue = 0;

    public int getExtTotal() {
        return extTotal;
    }

    public void setExtTotal(int extTotal) {
        this.extTotal = extTotal;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }
}

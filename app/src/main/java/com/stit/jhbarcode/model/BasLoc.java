package com.stit.jhbarcode.model;

public class BasLoc {
    private long id;
    private String depNo;
    private String locNo;

    public String getDepNo() {
        return depNo;
    }

    public void setDepNo(String depNo) {
        this.depNo = depNo;
    }

    public String getLocNo() {
        return locNo;
    }

    public void setLocNo(String locNo) {
        this.locNo = locNo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}

package com.minkbox.model;

public class Category {
    public int category_id;
    public String category_name;
    private int server_categoryid;


    boolean chkBox;

    public int getServer_categoryid() {
        return server_categoryid;
    }

    public void setServer_categoryid(int server_categoryid) {
        this.server_categoryid = server_categoryid;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }


    public boolean isChkBox() {
        return chkBox;
    }

    public void setChkBox(boolean chkBox) {
        this.chkBox = chkBox;
    }

}
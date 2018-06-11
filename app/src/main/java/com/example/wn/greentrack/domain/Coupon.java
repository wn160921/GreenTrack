package com.example.wn.greentrack.domain;

public class Coupon {
    private String uuid;
    private String owner;
    private String from;
    private String show;
    private int money;
    private int limit;
    private int numberRest;
    private int isProvider;   //0假1真


    public int getIsProvider() {
        return isProvider;
    }

    public void setIsProvider(int isProvider) {
        this.isProvider = isProvider;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getNumberRest() {
        return numberRest;
    }

    public void setNumberRest(int numberRest) {
        this.numberRest = numberRest;
    }
}

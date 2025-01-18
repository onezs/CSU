package com.example.didi.beans;



public class Order {
    private int orderId;
    private String startPoint;
    private String endPoint;
    private String ownerId;
    private String ownerName;
    private String phone;
    private float carriage;
    private float reward;
    private String time;
    // 可能还有更多字段


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public float getReward() {
        return reward;
    }

    public void setReward(float reward) {
        this.reward = reward;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public float getCarriage() {
        return carriage;
    }

    public void setCarriage(float carriage) {
        this.carriage = carriage;
    }
}

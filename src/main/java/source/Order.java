package source;

import java.util.Arrays;

public class Order {

    public static enum TYPE  {
        BUY,
        SELL
    }

    private String userID;

    private Double quantity;

    private Double price;

    private TYPE orderType;

    public Order() {
    }

    public Order(String userID, Double quantity, Double price, TYPE orderType) {
        this.userID = userID;
        this.quantity = quantity;
        this.price = price;
        this.orderType = orderType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public TYPE getOrderType() {
        return orderType;
    }

    public void setOrderType(TYPE orderType) {
        this.orderType = orderType;
    }

    public String toString() {
        String type = orderType == TYPE.BUY ? "BUY" : "SELL";
        return String.join(" ; ", Arrays.asList(userID, quantity + " KGs", "£ " + price, type));
    }

    public boolean equals(Order otherOrder){
        return otherOrder.toString().equals(this.toString());
    }
}

package com.firstproject.cooook.vo;

import java.sql.Date;

import lombok.Data;
@Data
public class OrderVO {
    private int orderId;
    private int userId;
    private int productId;
    private int quantity;
    private double totalPrice;
    private Date orderDate;
    private String status;
    private String shippingAddr;
    private String paymentMethod;
    private int staffId;
}

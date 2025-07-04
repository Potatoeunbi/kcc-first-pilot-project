package com.firstproject.cooook.vo;

import java.sql.Date;

import lombok.Data;
@Data
public class OrderVO {
    private int orderId;
    private int staffId;
    private String staffName;
    private int menuId;
    private String menuName;
    private int quantity;
    private int totalPrice;
    private Date createdAt;
    private Date deletedAt;
}

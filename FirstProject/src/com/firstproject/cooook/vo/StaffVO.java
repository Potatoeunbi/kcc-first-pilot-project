package com.firstproject.cooook.vo;

import java.sql.Date;

import lombok.Data;
@Data
public class StaffVO {
    private int staffId;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private int roleId;     // roles 테이블 FK
    private String isActive;
    private Date createdAt;
}

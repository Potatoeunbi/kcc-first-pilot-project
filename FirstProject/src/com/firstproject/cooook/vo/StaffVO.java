package com.firstproject.cooook.vo;

import java.sql.Date;

import lombok.Data;
@Data
public class StaffVO {
    private int staffId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private int roleId;     // roles 테이블 FK
    private String roleName;    // roles 테이블 JOIN 결과용
    private Date createdAt;
    private Date deletedAt;
}

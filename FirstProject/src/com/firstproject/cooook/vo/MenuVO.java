package com.firstproject.cooook.vo;
import java.util.List;

import lombok.Data;

@Data
public class MenuVO {
    private int menuId;
    private String menuName;
    private int price;
    private String roleName; 
    private List<Integer> categoryIds;  

}

package com.firstproject.cooook.vo;

import lombok.Data;

@Data
public class UpdateMenuVO {
    private int menuId;
    private String menuName;
    private int price;
    private String categoryName;
    private Integer parentCategoryId; 
    private int categoryId;       
}

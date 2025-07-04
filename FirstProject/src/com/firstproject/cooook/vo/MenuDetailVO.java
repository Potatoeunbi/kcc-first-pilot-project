package com.firstproject.cooook.vo;

import lombok.Data;

@Data
public class MenuDetailVO {
	
	private int menuId;
	private String categoryName;
	private String menuName;
    private int quantityUsed;
	private String ingredientName;
}

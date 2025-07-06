package com.firstproject.cooook.vo;

import lombok.Data;

@Data
public class RecipeVO {
	private int recipeId;
	private int menuId;
	private int categoryId;
	private double quantity;
	private String menuName;
	private String unit;         // "g"
	private String description;  // "국산 돼지고기"
	private String ingredientName; // JOIN된 CATEGORY_NAME (출력용)
}

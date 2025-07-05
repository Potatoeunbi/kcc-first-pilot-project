package com.firstproject.cooook.vo;

import lombok.Data;

@Data
public class RecipeVO {
    private int recipeId;
    private int menuId;
    private int categoryId;     // 재료로 사용하는 카테고리
    private double quantity;
    private String unit;
    private String description;
}

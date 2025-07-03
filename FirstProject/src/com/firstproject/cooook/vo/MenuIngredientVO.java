package com.firstproject.cooook.vo;

import lombok.Data;

@Data
public class MenuIngredientVO {
    private int menuId;
    private int ingredientId;
    private int quantityUsed;
    private IngredientVO ingredient;

}

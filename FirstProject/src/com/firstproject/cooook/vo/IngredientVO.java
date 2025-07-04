package com.firstproject.cooook.vo;
import lombok.Data;

@Data
public class IngredientVO {
    private int ingredientId;
    private String ingredientName;
    private String ingredientType;
    private int stockQty;
}

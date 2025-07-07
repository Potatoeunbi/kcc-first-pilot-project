package com.firstproject.cooook.vo;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CategoryVO {
	private int categoryId;
	private String categoryName;
	private Integer parentId;
	private List<CategoryVO> child = new ArrayList<>();
	private int level;
}

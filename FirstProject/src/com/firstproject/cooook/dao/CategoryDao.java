package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.CategoryVO;

public class CategoryDao {
	public List<CategoryVO> selectCategory() {
		List<CategoryVO> categories = new ArrayList<>();
		
		String sql = """
				SELECT 
				    category_id									AS categoryId,
				    category_name || '(' || category_ID || ')'	AS categoryName,
				    parent_id									AS parentId
				FROM category
				ORDER BY category_id				
				""";
		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				CategoryVO category = new CategoryVO();
				
				category.setCategoryId(resultSet.getInt("categoryId"));
				category.setCategoryName(resultSet.getString("categoryName"));
				category.setParentId(resultSet.getObject("parentId", Integer.class));
				
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return createTreeCategory(categories);
	}
	
	public List<CategoryVO> selectCategory(int categoryId) {
		List<CategoryVO> categories = new ArrayList<>();
		
		String sql = """
			     SELECT 
			     	category_id									AS categoryId,
			    	category_name || '(' || category_ID || ')'	AS categoryName,
			    	parent_id									AS parentId
			     FROM category
			     START WITH category_id = ?
			     CONNECT BY PRIOR category_id = parent_id				
			""";
		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, categoryId);
			
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				CategoryVO category = new CategoryVO();
				
				category.setCategoryId(resultSet.getInt("categoryId"));
				category.setCategoryName(resultSet.getString("categoryName"));
				category.setParentId(resultSet.getObject("parentId", Integer.class));
				
				categories.add(category);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);		
		}				
		return createTreeCategory(categories);
	}
	
	public void insertCategory(CategoryVO category) {
		String sql = """
				INSERT INTO category VALUES (
				    CATEGORY_SEQ.NEXTVAL, 
				    ?, 
				    (SELECT category_id 
				     FROM category 
				     WHERE category_id = ?)
				)
				""";		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setString(1, category.getCategoryName());
			statement.setInt(2, category.getParentId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);			
		}
	}

	public void updateCategory(CategoryVO category, int updateType) {
		String sql = "";
		if (updateType == 0) {
			sql = """
			UPDATE category
			SET category_name = ?
			WHERE category_id = ?
			""";
		} else {
			sql = """
			UPDATE category
			SET parent_id = ?
			WHERE category_id = ?
			""";
		}
		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			if (updateType == 0) {
				statement.setString(1, category.getCategoryName());				
			} else {
				statement.setInt(1, category.getParentId());
			}
			statement.setInt(2, category.getCategoryId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);			
		}
	}

	public void deleteCategory(int categoryId) {
		String sql = """
				DELETE FROM category
				WHERE category_id IN (
				    SELECT category_id FROM category
				    START WITH category_id = ?
				    CONNECT BY PRIOR category_id = parent_id
				)
				""";
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setInt(1, categoryId);			
			statement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();			
			throw new RuntimeException(e);			
		}
	}

	
	private List<CategoryVO> createTreeCategory(List<CategoryVO> categories) {
		Map<Integer, CategoryVO> categoryMap = new HashMap<>();
		List<CategoryVO> roots = new ArrayList<>();
		
		for (CategoryVO category : categories) {
			categoryMap.put(category.getCategoryId(), category);
		}
		
		for (CategoryVO category : categories) {
			Integer parentId = category.getParentId();
			
			if (parentId == null || parentId == 0) {
				roots.add(category);
				continue;
			} 

	        CategoryVO parent = categoryMap.get(parentId);

	        if (parent != null) {
	            parent.getChild().add(category);
	        } else {
	            roots.add(category);
	        }
		}
		
		return roots;
	}
}

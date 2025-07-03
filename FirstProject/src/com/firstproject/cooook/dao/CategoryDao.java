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
	DBUtil dataSource = new DBUtil();

	public List<CategoryVO> selectCategory() {
		List<CategoryVO> categories = new ArrayList<>();
		Connection connection = null;
		
		try {
			connection = DBUtil.getConnection();
			String sql = """
					SELECT 
					    category_id		AS categoryId,
					    category_name || '(' || category_ID || ')'	AS categoryName,
					    parent_id		AS parentId
					FROM category
					ORDER BY category_id				
					""";
			PreparedStatement statement = connection.prepareStatement(sql);			
			ResultSet resultSet = statement.executeQuery();

			while (resultSet.next()) {
				CategoryVO category = new CategoryVO();
				
				category.setCategoryId(resultSet.getInt("categoryId"));
				category.setCategoryName(resultSet.getString("categoryName"));
				category.setParentId(resultSet.getObject("parentId", Integer.class));
				
				categories.add(category);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		} finally {
			DBUtil.close(connection);
		}		
		
		return createTreeCategory(categories);
	}
	
	public List<CategoryVO> selectCategory(int categoryId) {
		List<CategoryVO> categories = new ArrayList<>();
		Connection connection = null;
		
		try {
			connection = DBUtil.getConnection();
			String sql = """
					     SELECT 
					     	category_id									AS categoryId,
					    	category_name || '(' || category_ID || ')'	AS categoryName,
					    	parent_id									AS parentId
					     FROM category
					     START WITH category_id = ?
					     CONNECT BY PRIOR category_id = parent_id				
					""";
			PreparedStatement statement = connection.prepareStatement(sql);
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
			throw new RuntimeException(e);			
		} finally {
			DBUtil.close(connection);
		}		
		
		return createTreeCategory(categories);
	}
	
	public void insertCategory(CategoryVO category) {
		Connection connection = null;
		
		try {
			connection = DBUtil.getConnection();
			
			String sql = """
					INSERT INTO category VALUES (
					    CATEGORY_SEQ.NEXTVAL, 
					    ?, 
					    (SELECT category_id 
					     FROM category 
					     WHERE category_id = ?)
					)
					""";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, category.getCategoryName());
			statement.setInt(2, category.getParentId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		} finally {
			DBUtil.close(connection);
		}
	}

	public void updateCategory(CategoryVO category, int updateType) {
		Connection connection = null;
		
		try {
			connection = DBUtil.getConnection();
			
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
			
			PreparedStatement statement = connection.prepareStatement(sql);
			if (updateType == 0) {
				statement.setString(1, category.getCategoryName());				
			} else {
				statement.setInt(1, category.getParentId());
			}
			statement.setInt(2, category.getCategoryId());
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		} finally {
			DBUtil.close(connection);
		}
	}

	public void deleteCategory(int categoryId) {
		Connection connection = null;
		
		try {
			connection = DBUtil.getConnection();
			
			String sql = """
					DELETE FROM category
					WHERE category_id IN (
					    SELECT category_id FROM category
					    START WITH category_id = ?
					    CONNECT BY PRIOR category_id = parent_id
					)
					""";
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setInt(1, categoryId);
			
			statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		} finally {
			DBUtil.close(connection);
		}
	}

	
	private List<CategoryVO> createTreeCategory(List<CategoryVO> categories) {
		Map<Integer, CategoryVO> map = new HashMap<>();
		List<CategoryVO> roots = new ArrayList<>();
		
		for (CategoryVO category : categories) {
			map.put(category.getCategoryId(), category);
		}
		
		for (CategoryVO category : categories) {
			if (category.getParentId() == null || category.getParentId() == 0) {
				roots.add(category);
			} else {				
				map.get(category.getParentId()).getChild().add(category);
			}
		}
		
		return roots;
	}
}

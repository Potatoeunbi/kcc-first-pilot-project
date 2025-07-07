package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.CategoryVO;

public class CategoryDao {
	public List<CategoryVO> selectCategory() {
		List<CategoryVO> categories = new ArrayList<>();
		
		String sql = """
				SELECT 
					category_id 	AS categoryId, 
					category_name 	AS categoryName,
					parent_id 		AS parentId, 
					level
				FROM category
				START WITH parent_id IS NULL
				CONNECT BY PRIOR category_id = parent_id
				ORDER SIBLINGS BY category_id				
				""";
		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery()) {
			
			while (resultSet.next()) {
				CategoryVO category = new CategoryVO();
				
				category.setCategoryId(resultSet.getInt("categoryId"));
				category.setCategoryName(resultSet.getString("categoryName"));
				category.setParentId(resultSet.getObject("parentId", Integer.class));
				category.setLevel(resultSet.getInt("level"));
				
				categories.add(category);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return categories;
	}

	public List<CategoryVO> selectChildCategory(int categoryId) {
		List<CategoryVO> categories = new ArrayList<>();
		
		String sql = """
				SELECT 
					category_id 	AS categoryId, 
					category_name 	AS categoryName,
					parent_id 		AS parentId, 
					level
				FROM category
				START WITH category_id = ?
				CONNECT BY PRIOR category_id = parent_id
				ORDER SIBLINGS BY category_id					
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
				category.setLevel(resultSet.getInt("level"));
				
				categories.add(category);
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);		
		}
		return categories;
	}
	
	public CategoryVO selectCategoryById(int categoryId) {
		String sql = """
				SELECT 
					category_id 	AS categoryId,
					category_name 	AS categoryName,
					parent_id 		AS parentId
				FROM category
				WHERE category_id = ?
				""";
		try (Connection connection = DBUtil.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)){
			
			statement.setInt(1, categoryId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				CategoryVO category = new CategoryVO();

				category.setCategoryId(resultSet.getInt("categoryId"));
				category.setCategoryName(resultSet.getString("categoryName"));
				category.setParentId(resultSet.getObject("parentID", Integer.class));

				return category;
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public int insertCategory(CategoryVO category) {
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
			
			return statement.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		}
	}

	public boolean updateCategory(CategoryVO category, int updateType) {
		String sql = "UPDATE category SET ";
		sql += updateType == 0 ? "category_name = ? " : "parent_id = ?";
		sql += " WHERE category_id = ?";
		
		try (Connection connection = DBUtil.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql)) {
			if (updateType == 0) {
				statement.setString(1, category.getCategoryName());				
			} else {
				statement.setInt(1, category.getParentId());
			}
			statement.setInt(2, category.getCategoryId());
			
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		}
	}

	public boolean deleteCategory(int categoryId) {
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
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);			
		}
	}

	
	public List<CategoryVO> selectLeafCategories() {
	    List<CategoryVO> list = new ArrayList<>();
	    String sql = """
	        SELECT c.category_id, c.category_name, c.parent_id
	        FROM category c
	        WHERE NOT EXISTS (
	            SELECT 1 FROM category c2 WHERE c2.parent_id = c.category_id
	        )
	    """;

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql);
	         ResultSet rs = pstmt.executeQuery()) {

	        while (rs.next()) {
	            CategoryVO c = new CategoryVO();
	            c.setCategoryId(rs.getInt("category_id"));
	            c.setCategoryName(rs.getString("category_name"));
	            c.setParentId(rs.getInt("parent_id"));
	            list.add(c);
	        }

	    } catch (SQLException e) {
	        System.out.println("❌ 하위 카테고리 조회 실패: " + e.getMessage());
	    }

	    return list;
	}

	public List<CategoryVO> selectCategoryFlat() {
	    List<CategoryVO> flat = new ArrayList<>();
	    flatten(selectCategory(), flat);
	    return flat;
	}

	private void flatten(List<CategoryVO> source, List<CategoryVO> flat) {
	    for (CategoryVO c : source) {
	        flat.add(c);
	        if (c.getChild() != null && !c.getChild().isEmpty()) {
	            flatten(c.getChild(), flat);
	        }
	    }
	}        
}

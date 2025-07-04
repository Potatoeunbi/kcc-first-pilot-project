package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;  
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.IngredientVO;
import com.firstproject.cooook.vo.MenuIngredientVO;
import com.firstproject.cooook.vo.MenuVO;

public class MenuDao {
    
    DBUtil du = new DBUtil();
    
    public List<MenuVO> getCategorySearchMenu(int categoryId) {
        List<MenuVO> list = new ArrayList<>();
        Connection con = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT menu_id, menu_name, category_id, price FROM menu WHERE category_id = ? order by category_id";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("카테고리 id" + categoryId);
            while (rs.next()) {
                MenuVO mvo = new MenuVO();
                mvo.setMenuID(rs.getInt("menu_id"));
                mvo.setMenuName(rs.getString("menu_name"));
                mvo.setCategoryId(rs.getInt("category_id"));
                mvo.setPrice(rs.getInt("price"));
                list.add(mvo);
            }

        } catch (SQLException e) {
            System.out.println("메뉴 조회 오류: " + e.getMessage());
        } finally {
            DBUtil.close(con);
        }

        return list;
    }

    
    public List<MenuVO> getStringSearchMenu(String keyword) {
        List<MenuVO> list = new ArrayList<>();
        Connection con = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT MENU_ID, MENU_NAME, CATEGORY_ID, PRICE FROM MENU WHERE MENU_NAME LIKE ?";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, "%" + keyword + "%" );

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MenuVO mvo = new MenuVO();
                mvo.setMenuID(rs.getInt("MENU_ID"));
                mvo.setMenuName(rs.getString("MENU_NAME"));
                mvo.setCategoryId(rs.getInt("CATEGORY_ID"));
                mvo.setPrice(rs.getInt("price"));
                list.add(mvo);
            }

        } catch (SQLException e) {
            System.out.println("이름 검색 오류: " + e.getMessage());
        } finally {
            DBUtil.close(con);
        }

        return list;
    }
    
    public List<MenuIngredientVO> getAllMenuDetail(int menu_id){
        List<MenuIngredientVO> list = new ArrayList<>();
        Connection con = null;
        
    	try {
            con = DBUtil.getConnection();
            String sql = "SELECT m.menu_id AS menu_id, m.menu_name, c.category_name,"
                    + " i.ingredient_id AS ingredient_id, i.ingredient_name, i.ingredient_type,"
                    + " mi.quantity_used "
                    + " FROM Menu m "
                    + " JOIN category c ON m.category_id = c.category_id "
                    + " JOIN menu_ingredient mi ON m.menu_id = mi.menu_id "
                    + " JOIN ingredient i ON mi.ingredient_id = i.ingredient_id "
                    + " WHERE m.menu_id = ?";

            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, menu_id);
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()) {
        		IngredientVO ivo = new IngredientVO(); 
        		ivo.setIngredientId(rs.getInt("Ingredient_id"));
        		ivo.setIngredientName(rs.getString("ingredient_name"));
            	MenuIngredientVO mvo = new MenuIngredientVO();
        		mvo.setMenuId(rs.getInt("menu_id"));
        		mvo.setQuantityUsed(rs.getInt("quantity_used"));
        		mvo.setIngredientId(rs.getInt("ingredient_id"));
        		mvo.setIngredient(ivo);  
        		list.add(mvo);
//        		System.out.println("메뉴 ID: " + mvo.getMenuId() + 
//        				", 재료명: " + ivo.getIngredientName() + 
//        				", 수량: " + mvo.getQuantityUsed());
            }
            
    	}catch(SQLException e){
            System.out.println("상세 정보 조회 오류: " + e.getMessage());
    	}finally {
    		
    	}
    	return list;
    }
    	
    public void insertMenu(MenuVO menu) {
        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = DBUtil.getConnection();

            String sql = "INSERT INTO menu (menu_name, category_id, price) VALUES (?, ?, ?)";
            pstmt = con.prepareStatement(sql);

            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getCategoryId());
            pstmt.setInt(3, menu.getPrice());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(con, pstmt, null);
        }
    }

    
} 

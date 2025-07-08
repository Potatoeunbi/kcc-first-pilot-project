package com.firstproject.cooook.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.firstproject.cooook.vo.Menu;
import com.firstproject.cooook.db.DBUtil;

public class MenuRepository {    
    public List<Menu> getAllMenus() {
        List<Menu> menus = new ArrayList<>();
        
        String sql = "SELECT menu_id AS menuId, menu_name AS menuName FROM menu";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Menu menu = new Menu();
                menu.setMenuId(rs.getInt("menuId"));
                menu.setMenuName(rs.getString("menuName"));
                menus.add(menu);
            }
        } catch (SQLException e) {
            System.err.println("❌ 메뉴 조회 중 오류: " + e.getMessage());
        }
        return menus;
    }
    
    public Menu getMenuById(int id) {
        String sql = "SELECT menu_id AS menuId, menu_name AS menuName FROM menu WHERE menu_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Menu menu = new Menu();
                menu.setMenuId(rs.getInt("menuId"));
                menu.setMenuName(rs.getString("menuName"));
                return menu;
            }
        } catch (SQLException e) {
            System.err.println("❌ 메뉴 조회 중 오류: " + e.getMessage());
        }
        return null;
    }
    
    public int insertMenu(Menu menu) {
        String sql = "INSERT INTO menu VALUES (MENU_SEQ.NEXTVAL, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, 0);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ 메뉴 등록 중 오류: " + e.getMessage());
        }
        return -1;
    }
    
    public boolean updateMenu(Menu menu) {
        String sql = "UPDATE menu SET menu_name = ? WHERE menu_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, menu.getMenuName());
            pstmt.setInt(2, menu.getMenuId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ 메뉴 수정 중 오류: " + e.getMessage());
        }
        return false;
    }
    
    public boolean deleteMenu(int menuId) {
        String deleteMenuCategorySql = "DELETE FROM menu_category WHERE menu_id = ?";
        String deleteMenuSql = "DELETE FROM menu WHERE menu_id = ?";

        try (Connection connection = DBUtil.getConnection()) {
            connection.setAutoCommit(false);

            try (PreparedStatement menuCategoryStatement = connection.prepareStatement(deleteMenuCategorySql);
                PreparedStatement menuStatement = connection.prepareStatement(deleteMenuSql)) {

                menuCategoryStatement.setInt(1, menuId);
                menuCategoryStatement.executeUpdate();

                menuStatement.setInt(1, menuId);
                int affectedRows = menuStatement.executeUpdate();

                connection.commit();
                return affectedRows > 0;

            } catch (SQLException e) {
                connection.rollback();
                System.err.println("❌ 트랜잭션 롤백됨: " + e.getMessage());
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("❌ DB 연결 오류: " + e.getMessage());
        }

        return false;
    }
    
    public List<Menu> searchMenusByName(String keyword) {
        List<Menu> menus = new ArrayList<>();
        
        String sql = "SELECT menu_id AS menuId, menu_name AS menuName FROM menu WHERE menu_name LIKE ? ORDER BY menu_name";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + keyword + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Menu menu = new Menu();
                menu.setMenuId(rs.getInt("menuId"));
                menu.setMenuName(rs.getString("menuName"));
                menus.add(menu);
            }
        } catch (SQLException e) {
            System.err.println("❌ 메뉴 검색 중 오류: " + e.getMessage());
        }
        return menus;
    }
} 
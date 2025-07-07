
package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.firstproject.cooook.vo.CategoryVO;
import com.firstproject.cooook.vo.Menu;
import com.firstproject.cooook.vo.MenuCategory;
import com.firstproject.cooook.db.DBUtil;

public class MenuCategoryDAO {
    
    // 기존 복잡한 쿼리를 단순한 쿼리들로 분리
    public List<MenuCategory> getAllMenuCategories() {
        return buildIntegratedTree();
    }
    
    // 1. 메뉴별 직접 연결된 카테고리 조회 (단순 쿼리)
    public Map<Integer, List<Integer>> getMenuCategoryMap() {
        Map<Integer, List<Integer>> menuCategoryMap = new HashMap<>();
        
        String sql = "SELECT menu_id, category_id FROM menu_category";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                int menuId = rs.getInt("menu_id");
                int categoryId = rs.getInt("category_id");
                
                menuCategoryMap.computeIfAbsent(menuId, k -> new ArrayList<>())
                              .add(categoryId);
            }
        } catch (SQLException e) {
            System.err.println("❌ 메뉴-카테고리 연결 조회 중 오류: " + e.getMessage());
        }
        return menuCategoryMap;
    }
    
    // 2. 미분류 메뉴 조회 (단순 쿼리)
    public List<Menu> getUncategorizedMenus() {
        List<Menu> uncategorizedMenus = new ArrayList<>();
        
        String sql = """
            SELECT m.menu_id, m.menu_name 
            FROM menu m
            WHERE NOT EXISTS (
                SELECT 1 FROM menu_category mcr 
                WHERE mcr.menu_id = m.menu_id
            )
            ORDER BY m.menu_name
            """;
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                Menu menu = new Menu();
                menu.setMenuId(rs.getInt("menu_id"));
                menu.setMenuName(rs.getString("menu_name"));
                uncategorizedMenus.add(menu);
            }
        } catch (SQLException e) {
            System.err.println("❌ 미분류 메뉴 조회 중 오류: " + e.getMessage());
        }
        return uncategorizedMenus;
    }
    
    // 3. 로직으로 통합 트리 구성
    public List<MenuCategory> buildIntegratedTree() {
        List<MenuCategory> treeItems = new ArrayList<>();
        
        // 카테고리 트리 가져오기
        CategoryDao categoryDAO = new CategoryDao();
        List<CategoryVO> categories = categoryDAO.selectCategory();
        
        // 메뉴별 카테고리 연결 맵 가져오기
        Map<Integer, List<Integer>> menuCategoryMap = getMenuCategoryMap();
        
        // 미분류 메뉴 가져오기
        List<Menu> uncategorizedMenus = getUncategorizedMenus();
        
        // 카테고리별로 메뉴 그룹핑
        Map<Integer, List<Menu>> categoryMenuMap = new HashMap<>();
        MenuRepository menuRepository = new MenuRepository();
        
        for (Map.Entry<Integer, List<Integer>> entry : menuCategoryMap.entrySet()) {
            int menuId = entry.getKey();
            List<Integer> categoryIds = entry.getValue();
            
            Menu menu = menuRepository.getMenuById(menuId);
            if (menu != null) {
                for (int categoryId : categoryIds) {
                    categoryMenuMap.computeIfAbsent(categoryId, k -> new ArrayList<>())
                                  .add(menu);
                }
            }
        }
        
        // 트리 구성 (정렬 포함)
        for (CategoryVO category : categories) {
            // 카테고리 추가
            MenuCategory categoryItem = new MenuCategory();
            categoryItem.setName(category.getCategoryName() + " (" + category.getCategoryId() + ")");
            categoryItem.setType("CATEGORY");
            categoryItem.setLevel(category.getLevel());
            treeItems.add(categoryItem);
            
            // 해당 카테고리의 메뉴들 추가 (정렬)
            List<Menu> menus = categoryMenuMap.get(category.getCategoryId());
            if (menus != null) {
                // 메뉴 이름으로 정렬
                menus.sort((m1, m2) -> m1.getMenuName().compareTo(m2.getMenuName()));
                
                for (Menu menu : menus) {
                    MenuCategory menuItem = new MenuCategory();
                    menuItem.setName(menu.getMenuName() + " (" + menu.getMenuId() + ")");
                    menuItem.setType("MENU");
                    menuItem.setLevel(category.getLevel() + 1);
                    treeItems.add(menuItem);
                }
            }
        }
        
        // 미분류 메뉴 추가 (정렬)
        if (!uncategorizedMenus.isEmpty()) {
            // 미분류 메뉴들 이름으로 정렬
            uncategorizedMenus.sort((m1, m2) -> m1.getMenuName().compareTo(m2.getMenuName()));
            
            // 미분류 헤더
            MenuCategory headerItem = new MenuCategory();
            headerItem.setName("미분류 메뉴");
            headerItem.setType("HEADER");
            headerItem.setLevel(1);
            treeItems.add(headerItem);
            
            // 미분류 메뉴들
            for (Menu menu : uncategorizedMenus) {
                MenuCategory menuItem = new MenuCategory();
                menuItem.setName(menu.getMenuName() + " (" + menu.getMenuId() + ")");
                menuItem.setType("UNCATEGORIZED");
                menuItem.setLevel(2);
                treeItems.add(menuItem);
            }
        }
        
        return treeItems;
    }

    public int insertMenuCategory(MenuCategory menuCategory) {
        String sql = "INSERT INTO MENU_CATEGORY (MENU_ID, CATEGORY_ID) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuCategory.getMenuId());
            pstmt.setInt(2, menuCategory.getCategoryId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ 메뉴-카테고리 연결 등록 중 오류: " + e.getMessage());
            return 0;
        }
    }

    public int deleteMenuCategory(int menuId, int categoryId) {
        String sql = "DELETE FROM MENU_CATEGORY WHERE MENU_ID = ? AND CATEGORY_ID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);
            pstmt.setInt(2, categoryId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ 메뉴-카테고리 연결 삭제 중 오류: " + e.getMessage());
            return 0;
        }
    }   

    public MenuCategory getMenuCategoryById(int menuId, int categoryId) {
        String sql = "SELECT * FROM MENU_CATEGORY WHERE MENU_ID = ? AND CATEGORY_ID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);
            pstmt.setInt(2, categoryId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                MenuCategory menuCategory = new MenuCategory();
                menuCategory.setMenuId(rs.getInt("MENU_ID"));
                menuCategory.setCategoryId(rs.getInt("CATEGORY_ID"));
                return menuCategory;
            }
        } catch (SQLException e) {
            System.err.println("❌ 메뉴-카테고리 연결 조회 중 오류: " + e.getMessage());
        }   
        return null;
    }

    public int updateMenuCategory(MenuCategory menuCategory, int newCategoryId) {
        String sql = "UPDATE MENU_CATEGORY SET CATEGORY_ID = ? WHERE MENU_ID = ? AND CATEGORY_ID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newCategoryId);
            pstmt.setInt(2, menuCategory.getMenuId());
            pstmt.setInt(3, menuCategory.getCategoryId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ 메뉴-카테고리 연결 수정 중 오류: " + e.getMessage());
            return 0;
        }
    }
}

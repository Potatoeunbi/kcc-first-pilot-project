package com.firstproject.cooook.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.MenuVO;
import com.firstproject.cooook.vo.UpdateMenuVO;

public class MenuDao {

    public List<MenuVO> getMenuByCategoryId(int categoryId) {
        List<MenuVO> list = new ArrayList<>();
        String sql = """
            SELECT m.menu_id, m.menu_name, m.price
            FROM menu m
            JOIN menu_category mc ON m.menu_id = mc.menu_id
            WHERE mc.category_id = ?
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setInt(1, categoryId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuVO m = new MenuVO();
                    m.setMenuId(rs.getInt("menu_id"));
                    m.setMenuName(rs.getString("menu_name"));
                    m.setPrice(rs.getInt("price"));
                    list.add(m);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("🔴 메뉴 가져오기 실패: " + e.getMessage());
        }

        return list;
    }

    public List<MenuVO> getStringSearchMenu(String keyword) {
        List<MenuVO> list = new ArrayList<>();
        String sql = """
            SELECT menu_id, menu_name, price
            FROM menu
            WHERE LOWER(menu_name) LIKE ?
            ORDER BY menu_name
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {

            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MenuVO vo = new MenuVO();
                    vo.setMenuId(rs.getInt("menu_id"));
                    vo.setMenuName(rs.getString("menu_name"));
                    vo.setPrice(rs.getInt("price"));
                    list.add(vo);
                }
            }

        } catch (SQLException e) {
            System.out.println("🔴 메뉴 키워드 검색 오류: " + e.getMessage());
            throw new RuntimeException(e);
        }

        return list;
    }

    public void insertMenu(MenuVO menu) {
        String insertMenuSql = "INSERT INTO menu (menu_id, menu_name, price) VALUES (MENU_SEQ.NEXTVAL, ?, ?)";
        String insertMapSql = "INSERT INTO menu_category (menu_id, category_id) VALUES (?, ?)";

        try (Connection con = DBUtil.getConnection()) {
            con.setAutoCommit(false);

            int newMenuId = 0;

            try (PreparedStatement pstmt = con.prepareStatement(insertMenuSql, new String[]{"menu_id"})) {
                pstmt.setString(1, menu.getMenuName());
                pstmt.setInt(2, menu.getPrice());
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) newMenuId = rs.getInt(1);
                }
            }

            try (PreparedStatement pstmt = con.prepareStatement(insertMapSql)) {
                for (int categoryId : menu.getCategoryIds()) {
                    pstmt.setInt(1, newMenuId);
                    pstmt.setInt(2, categoryId);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            con.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 메뉴 등록 실패: " + e.getMessage());
        }
    }

    public void deleteMenu(int menuId) {
        String deleteCategoryMap = "DELETE FROM MENU_CATEGORY WHERE MENU_ID = ?";
        String deleteMenu = "DELETE FROM MENU WHERE MENU_ID = ?";

        try (Connection con = DBUtil.getConnection()) {

            try (PreparedStatement pstmt = con.prepareStatement(deleteCategoryMap)) {
                pstmt.setInt(1, menuId);
                pstmt.executeUpdate();
            }

            try (PreparedStatement pstmt = con.prepareStatement(deleteMenu)) {
                pstmt.setInt(1, menuId);
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    System.out.println("✅ 메뉴 삭제 완료");
                } else {
                    System.out.println("❌ 해당 메뉴 ID가 존재하지 않습니다.");
                }
            }

        } catch (SQLException e) {
            System.out.println("메뉴 삭제 중 오류: " + e.getMessage());
        }
    }

    public void deleteMenuCategoryByMenuId(int menuId) {
        String sql = "DELETE FROM MENU_CATEGORY WHERE MENU_ID = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, menuId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("❌ 메뉴-카테고리 삭제 실패: " + e.getMessage());
        }
    }

    public void insertMenuCategories(int menuId, List<Integer> categoryIds) {
        String sql = "INSERT INTO MENU_CATEGORY (MENU_ID, CATEGORY_ID) VALUES (?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int categoryId : categoryIds) {
                pstmt.setInt(1, menuId);
                pstmt.setInt(2, categoryId);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.out.println("❌ 메뉴-카테고리 등록 실패: " + e.getMessage());
        }
    }

    public UpdateMenuVO selectMenuDetailById(int menuId) {
        UpdateMenuVO vo = null;
        String sql = """
            SELECT M.MENU_ID, M.MENU_NAME, M.PRICE
            FROM MENU M
            WHERE M.MENU_ID = ?
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, menuId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    vo = new UpdateMenuVO();
                    vo.setMenuId(rs.getInt("MENU_ID"));
                    vo.setMenuName(rs.getString("MENU_NAME"));
                    vo.setPrice(rs.getInt("PRICE"));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ 메뉴 상세 조회 오류: " + e.getMessage());
        }

        return vo;
    }

    public int updateMenuByVO(UpdateMenuVO vo) {
        int updated = 0;

        try (Connection conn = DBUtil.getConnection()) {

            if (vo.getMenuName() != null) {
                String sql = "UPDATE MENU SET MENU_NAME = ? WHERE MENU_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setString(1, vo.getMenuName());
                    pstmt.setInt(2, vo.getMenuId());
                    updated += pstmt.executeUpdate();
                }
            }

            if (vo.getPrice() != 0) {
                String sql = "UPDATE MENU SET PRICE = ? WHERE MENU_ID = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, vo.getPrice());
                    pstmt.setInt(2, vo.getMenuId());
                    updated += pstmt.executeUpdate();
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ 메뉴 정보 수정 오류: " + e.getMessage());
        }

        return updated;
    }
    
 // 레시피가 있는 메뉴만 조회
    public List<MenuVO> selectAllMenu() {
        List<MenuVO> list = new ArrayList<>();
        String sql = """
            SELECT menu_id, menu_name
            FROM menu
            WHERE EXISTS (
                SELECT 1 FROM recipe r WHERE r.menu_id = menu.menu_id
            )
            ORDER BY menu_id
        """;

        try (Connection con = DBUtil.getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MenuVO vo = new MenuVO();
                vo.setMenuId(rs.getInt("menu_id"));
                vo.setMenuName(rs.getString("menu_name"));
                list.add(vo);
            }

        } catch (SQLException e) {
            System.out.println("❌ 레시피 있는 메뉴 조회 오류: " + e.getMessage());
        }

        return list;
    }


    //레시피가 없는 메뉴 출력용 메서드 레시피 등록때 사용
    public List<MenuVO> getMenusWithoutRecipe() {
        List<MenuVO> list = new ArrayList<>();
        String sql = """
            SELECT m.menu_id, m.menu_name
            FROM menu m
            WHERE NOT EXISTS (
                SELECT 1 FROM recipe r WHERE r.menu_id = m.menu_id
            )
            ORDER BY m.menu_id
        """;

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                MenuVO vo = new MenuVO();
                vo.setMenuId(rs.getInt("menu_id"));
                vo.setMenuName(rs.getString("menu_name"));
                list.add(vo);
            }

        } catch (SQLException e) {
            System.out.println("❌ 레시피 없는 메뉴 목록 조회 오류: " + e.getMessage());
        }

        return list;
    }
    
}

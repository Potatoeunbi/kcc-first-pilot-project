package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            MenuVO m = new MenuVO();
	            m.setMenuId(rs.getInt("menu_id"));
	            m.setMenuName(rs.getString("menu_name"));
	            m.setPrice(rs.getInt("price"));
	            list.add(m);
	        }
	    } catch (SQLException e) {
	        throw new RuntimeException("🔴 메뉴 가져오기 실패: " + e.getMessage());
	    }

	    return list;
	}


	public List<MenuVO> getStringSearchMenu(String keyword) {
	    List<MenuVO> list = new ArrayList<>();
	    String sql = """
	        SELECT 
	            m.menu_id      AS menuId,
	            m.menu_name    AS menuName,
	            m.price        AS price
	        FROM menu m
	        WHERE LOWER(m.menu_name) LIKE ?
	        ORDER BY m.menu_name
	    """;

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement pstmt = con.prepareStatement(sql)) {

	        pstmt.setString(1, "%" + keyword.toLowerCase() + "%");
	        ResultSet rs = pstmt.executeQuery();

	        while (rs.next()) {
	            MenuVO vo = new MenuVO();
	            vo.setMenuId(rs.getInt("menuId"));
	            vo.setMenuName(rs.getString("menuName"));
	            vo.setPrice(rs.getInt("price"));
	            list.add(vo);
	        }

	    } catch (SQLException e) {
	        System.out.println("🔴 메뉴 키워드 검색 오류: " + e.getMessage());
	        throw new RuntimeException(e);
	    }

	    return list;
	}


	public void insertMenu(MenuVO menu) {
	    Connection con = null;
	    PreparedStatement pstmt = null;
	    ResultSet generatedKeys = null;

	    try {
	        con = DBUtil.getConnection();
	        con.setAutoCommit(false); 

	        String sql = "INSERT INTO menu (menu_id, menu_name, price) VALUES (MENU_SEQ.NEXTVAL, ?, ?)";
	        pstmt = con.prepareStatement(sql, new String[] {"menu_id"});
	        pstmt.setString(1, menu.getMenuName());
	        pstmt.setInt(2, menu.getPrice());
	        pstmt.executeUpdate();

	        generatedKeys = pstmt.getGeneratedKeys();
	        int newMenuId = 0;
	        if (generatedKeys.next()) {
	            newMenuId = generatedKeys.getInt(1);
	        }

	        sql = "INSERT INTO menu_category (menu_id, category_id) VALUES (?, ?)";
	        pstmt = con.prepareStatement(sql);
	        for (int categoryId : menu.getCategoryIds()) {
	            pstmt.setInt(1, newMenuId);
	            pstmt.setInt(2, categoryId);
	            pstmt.addBatch();
	        }
	        pstmt.executeBatch();

	        con.commit();
	    } catch (SQLException e) {
	        try {
	            if (con != null) con.rollback();
	        } catch (SQLException ex) {
	            ex.printStackTrace();
	        }
	        e.printStackTrace();
	        throw new RuntimeException(e);
	    } finally {
	        DBUtil.close(generatedKeys);
	        DBUtil.close(pstmt);
	        DBUtil.close(con);
	    }
	}

	public void deleteMenu(int menuId) {
	    Connection con = null;
	    PreparedStatement pstmt = null;

	    try {
	        con = DBUtil.getConnection();

	        String deleteCategoryMap = "DELETE FROM MENU_CATEGORY WHERE MENU_ID = ?";
	        pstmt = con.prepareStatement(deleteCategoryMap);
	        pstmt.setInt(1, menuId);
	        pstmt.executeUpdate();
	        pstmt.close();

	        String deleteMenu = "DELETE FROM MENU WHERE MENU_ID = ?";
	        pstmt = con.prepareStatement(deleteMenu);
	        pstmt.setInt(1, menuId);
	        int result = pstmt.executeUpdate();

	        if (result > 0) {
	            System.out.println("✅ 메뉴 삭제 완료");
	        } else {
	            System.out.println("❌ 해당 메뉴 ID가 존재하지 않습니다.");
	        }
	    } catch (SQLException e) {
	        System.out.println("메뉴 삭제 중 오류: " + e.getMessage());
	    } finally {
	        DBUtil.close(con, pstmt, null);
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
	        SELECT M.MENU_ID, M.MENU_NAME, M.PRICE, C.CATEGORY_ID, C.CATEGORY_NAME
	        FROM MENU M
	        JOIN MENU_CATEGORY MC ON M.MENU_ID = MC.MENU_ID
	        JOIN CATEGORY C ON MC.CATEGORY_ID = C.CATEGORY_ID
	        WHERE M.MENU_ID = ?
	    """;

	    try (Connection conn = DBUtil.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, menuId);
	        ResultSet rs = pstmt.executeQuery();

	        if (rs.next()) {
	            vo = new UpdateMenuVO();
	            vo.setMenuId(rs.getInt("MENU_ID"));
	            vo.setMenuName(rs.getString("MENU_NAME"));
	            vo.setPrice(rs.getInt("PRICE"));
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


	
}

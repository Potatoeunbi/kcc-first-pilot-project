package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;  
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.MenuVO;

public class MenuDao {
    
    DBUtil du = new DBUtil();
    
    public List<MenuVO> getCategorySearchMenu(int categoryId) {
        List<MenuVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT menu_id, menu_name, category_id, price FROM menu WHERE category_id = ? order by category_id";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, categoryId);
            rs = stmt.executeQuery();
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
            DBUtil.close(con, stmt, rs);
        }

        return list;
    }

    
    public List<MenuVO> getStringSearchMenu(String keyword) {
        List<MenuVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT MENU_ID, MENU_NAME, CATEGORY_ID, PRICE FROM MENU WHERE MENU_NAME LIKE ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%" );

            rs = stmt.executeQuery();

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
            DBUtil.close(con, stmt, rs);
        }

        return list;
    }

    	
    
} 

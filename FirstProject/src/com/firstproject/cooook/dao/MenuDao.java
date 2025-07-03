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
    
    public List<MenuVO> getAllMenu(int categoryId) {
        List<MenuVO> mList = new ArrayList<>();
        Connection con = null;
        try {
            con = du.getConnection();

            String sql = "SELECT menu_name, menu_id, category_id FROM menu WHERE category_id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MenuVO mvo = new MenuVO();
                mvo.setCategoryId(rs.getInt("category_id"));
                mvo.setMenuID(rs.getInt("menu_id"));
                mvo.setMenuName(rs.getString("menu_name"));
                mList.add(mvo);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            du.close(con);
        }

        return mList;
    }

} 

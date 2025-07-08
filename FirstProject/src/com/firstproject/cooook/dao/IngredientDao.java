package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.IngredientVO;

public class IngredientDao {
	  public List<IngredientVO> selectAll() {
	        List<IngredientVO> list = new ArrayList<>();

	        String sql = "SELECT ingredient_id, name, unit_default FROM ingredient ORDER BY ingredient_id";

	        try (
	            Connection conn = DBUtil.getConnection();
	            PreparedStatement pstmt = conn.prepareStatement(sql);
	            ResultSet rs = pstmt.executeQuery();
	        ) {
	            while (rs.next()) {
	                IngredientVO vo = new IngredientVO();
	                vo.setIngredientId(rs.getInt("ingredient_id"));
	                vo.setIngredientName(rs.getString("name"));
	                vo.setUnitDefault(rs.getString("unit_default"));
	                list.add(vo);
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }

	        return list;
	    }
}

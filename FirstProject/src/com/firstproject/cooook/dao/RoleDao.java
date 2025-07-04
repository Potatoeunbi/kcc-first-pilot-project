package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.RoleVO;

public class RoleDao {

    private static final String tableName = "roles";

    // 역할 추가
    public void insertRole(RoleVO role) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBUtil.getConnection();
            String sql = "INSERT INTO " + tableName + " (role_name, description) VALUES (?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    // 역할 수정
    public void updateRole(RoleVO role) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBUtil.getConnection();
            List<String> setClauses = new ArrayList<>();
			List<Object> params = new ArrayList<>();
			
			String roleName = role.getRoleName();
			String description = role.getDescription();
			
			if(roleName != null) {
				setClauses.add("role_name = ?");
				params.add(roleName);
			}
			
			if(description != null) {
				setClauses.add("description = ?");
				params.add(description);
			}
			
			
			String sql = "UPDATE " + tableName + " SET " + String.join(", ", setClauses) + " WHERE role_id = ?";
			params.add(role.getRoleId());
			
			stmt = con.prepareStatement(sql);
		    for (int i = 0; i < params.size(); i++) {
		        stmt.setObject(i + 1, params.get(i));
		    }

			stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    public int deleteRole(int roleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBUtil.getConnection();
            String sql = "DELETE FROM " + tableName + " WHERE role_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, roleId);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    // 역할 전체 조회
    public List<RoleVO> getAllRoles() {
        List<RoleVO> roleList = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = DBUtil.getConnection();
            String sql = "SELECT role_id, role_name, description FROM " + tableName;
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RoleVO role = new RoleVO();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                roleList.add(role);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(rs);
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
        return roleList;
    }
    
    public RoleVO getRoleById(int roleId) {
        RoleVO role = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT role_id, role_name, description FROM roles WHERE role_id = ? order by role_id";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, roleId);
            rs = stmt.executeQuery();

            if (rs.next()) {
                role = new RoleVO();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(rs);
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
        return role;
    }
}
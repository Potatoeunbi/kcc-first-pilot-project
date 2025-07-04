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
            String sql = "UPDATE " + tableName + " SET role_name = ?, description = ? WHERE role_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.setInt(3, role.getRoleId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    public void deleteRole(int roleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBUtil.getConnection();
            String sql = "DELETE FROM " + tableName + " WHERE role_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, roleId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("삭제되지 않았습니다. role_id를 확인하세요.");
            } else {
                System.out.println("삭제가 완료되었습니다.");
            }
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
}
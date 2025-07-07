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
    private static final String subTableName = "role_feature";

    // 역할 추가
    public void insertRole(Connection con, RoleVO role) {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO " + tableName + " (role_name, description) VALUES (?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, role.getRoleName());
            stmt.setString(2, role.getDescription());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
        }
    }

    // 역할 수정
    public void updateRole(Connection con, RoleVO role) {
        PreparedStatement stmt = null;
        try {
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
			
			if (setClauses.isEmpty()) {
	            System.out.println("❗ 수정할 항목이 없습니다.");
	            return;
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
        }
    }

    public int softDeleteRole(int roleId) {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = DBUtil.getConnection();
            String sql = "UPDATE "+tableName+" SET deleted_at = sysdate WHERE role_id = ?";
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
    
    public int deleteRoleFeature(Connection con, int roleId) {
        PreparedStatement stmt = null;
        try {
            String sql = "DELETE FROM " + subTableName + " WHERE role_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, roleId);
            return stmt.executeUpdate(); // 삭제된 행 수 반환
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
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
            String sql = "SELECT r.role_id, r.role_name, r.description, "
            		+ " LISTAGG(f.feature_code, ', ') WITHIN GROUP (ORDER BY f.feature_code) AS features "
            		+ "FROM " + tableName + " r "
            		+ " left join role_feature f on f.role_id = r.role_id"
            		+ " where r.deleted_at is null"
            		+ " GROUP BY r.role_id, r.role_name, r.description"
            		+ " order by r.role_id";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                RoleVO role = new RoleVO();
                role.setRoleId(rs.getInt("role_id"));
                role.setRoleName(rs.getString("role_name"));
                role.setDescription(rs.getString("description"));
                role.setFeatureCodes(rs.getString("features"));
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
    
    
    public List<String> getFeaturesByRoleId(int roleId) {
        List<String> features = new ArrayList<>();
        String sql = "SELECT feature_code FROM "+subTableName+" WHERE role_id = ? order by display_order";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, roleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    features.add(rs.getString("feature_code"));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ 권한 기능 조회 실패: " + e.getMessage());
        }

        return features;
    }
    
    public void insertRoleFeature(Connection con, int role_id, String feature_code, int display_order) {
        PreparedStatement stmt = null;
        try {
            String sql = "INSERT INTO " + subTableName + " (feature_code, display_order, role_id) VALUES (?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setString(1, feature_code);
            stmt.setInt(2, display_order);
            stmt.setInt(3, role_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
        }
    }
    
    public int selectNextRoleSeq() {
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            con = DBUtil.getConnection();
            String sql = "SELECT max(role_id)+1 FROM roles";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new RuntimeException("시퀀스 NEXTVAL을 가져오지 못했습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(rs);
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

}
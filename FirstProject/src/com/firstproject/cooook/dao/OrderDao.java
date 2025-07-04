package com.firstproject.cooook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.firstproject.cooook.db.DBUtil;
import com.firstproject.cooook.vo.OrderVO;

public class OrderDao {
    private static final String tableName = "orders";

    // 주문 추가
    public void insertOrder(OrderVO order) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtil.getConnection();
            String sql = "INSERT INTO " + tableName + " (user_id, product_id, quantity, total_price, order_date, status, shipping_addr, payment_method, staff_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, order.getUserId());
            stmt.setInt(2, order.getProductId());
            stmt.setInt(3, order.getQuantity());
            stmt.setDouble(4, order.getTotalPrice());
            stmt.setDate(5, new java.sql.Date(order.getOrderDate().getTime()));
            stmt.setString(6, order.getStatus());
            stmt.setString(7, order.getShippingAddr());
            stmt.setString(8, order.getPaymentMethod());
            stmt.setInt(9, order.getStaffId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    // 주문 수정 (null 아닌 필드만)
    public void updateOrder(OrderVO order) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtil.getConnection();

            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            if (order.getUserId() != 0) {
                setClauses.add("user_id = ?");
                params.add(order.getUserId());
            }
            if (order.getProductId() != 0) {
                setClauses.add("product_id = ?");
                params.add(order.getProductId());
            }
            if (order.getQuantity() != 0) {
                setClauses.add("quantity = ?");
                params.add(order.getQuantity());
            }
            if (order.getTotalPrice() != 0) {
                setClauses.add("total_price = ?");
                params.add(order.getTotalPrice());
            }
            if (order.getOrderDate() != null) {
                setClauses.add("order_date = ?");
                params.add(new java.sql.Date(order.getOrderDate().getTime()));
            }
            if (order.getStatus() != null) {
                setClauses.add("status = ?");
                params.add(order.getStatus());
            }
            if (order.getShippingAddr() != null) {
                setClauses.add("shipping_addr = ?");
                params.add(order.getShippingAddr());
            }
            if (order.getPaymentMethod() != null) {
                setClauses.add("payment_method = ?");
                params.add(order.getPaymentMethod());
            }
            if (order.getStaffId() != 0) {
                setClauses.add("staff_id = ?");
                params.add(order.getStaffId());
            }

            if (setClauses.isEmpty()) {
                System.out.println("수정할 항목이 없습니다.");
                return;
            }

            String sql = "UPDATE " + tableName + " SET " + String.join(", ", setClauses) + " WHERE order_id = ?";
            params.add(order.getOrderId());

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

    // 논리 삭제
    public void softDeleteOrder(int orderId) {
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = DBUtil.getConnection();
            String sql = "UPDATE " + tableName + " SET deleted_at = sysdate WHERE order_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, orderId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.out.println("주문 삭제 실패: ID를 확인하세요.");
            } else {
                System.out.println("주문이 정상적으로 삭제 처리되었습니다.");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(stmt);
            DBUtil.close(con);
        }
    }

    // 전체 주문 목록 조회 (deleted_at 제외)
    public List<OrderVO> getAllOrders() {
        List<OrderVO> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DBUtil.getConnection();
            String sql = "SELECT * FROM " + tableName + " WHERE deleted_at IS NULL";
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setProductId(rs.getInt("product_id"));
                order.setQuantity(rs.getInt("quantity"));
                order.setTotalPrice(rs.getDouble("total_price"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setStatus(rs.getString("status"));
                order.setShippingAddr(rs.getString("shipping_addr"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setStaffId(rs.getInt("staff_id"));
                list.add(order);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            DBUtil.close(rs);
            DBUtil.close(stmt);
            DBUtil.close(con);
        }

        return list;
    }
}
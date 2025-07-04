package com.firstproject.cooook.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String DRIVER_CLASS_NAME = "oracle.jdbc.driver.OracleDriver";
    private static final String URL = "jdbc:oracle:thin:@localhost:1522:xe";
    private static final String USERNAME = "hr";
    private static final String PASSWORD = "hr";

    static {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            System.out.println("드라이버 로드 실패: " + e.getMessage());
        }
    }

    // DB 연결 반환
    public static Connection getConnection() {
        Connection con = null;
        try {
            con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("DB 연결 실패: " + e.getMessage());
        }
        return con;
    }

    // 리소스 닫기 - 오버로딩
    public static void close(Connection con) {
        try {
            if (con != null) con.close();
        } catch (SQLException e) {
            // 무시
        }
    }

    public static void close(PreparedStatement ps) {
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            // 무시
        }
    }

    public static void close(ResultSet rs) {
        try {
            if (rs != null) rs.close();
        } catch (SQLException e) {
            // 무시
        }
    }

    // 한 번에 다 닫는 메서드
    public static void close(Connection con, PreparedStatement ps, ResultSet rs) {
        close(rs);
        close(ps);
        close(con);
    }
}

package zzl.dao;

import java.sql.*;

public class MySql {
    private final static String DATA_URL = "jdbc:mysql://localhost:3306/didi?useUnicode=true&allowPublicKeyRetrieval=true&characterEncoding=utf-8&serverTimezone=UTC&transactionIsolation=READ_COMMITTED";
    private final static String DATABASE_NAME = "root";
    private final static String DATABASE_PWD = "123456";
    private final static String JDBC_NAME = "com.mysql.cj.jdbc.Driver";
    private PreparedStatement psm;
    private Connection conn;
    private ResultSet resultSet;

    public Connection getConnection() {

        try {
            Class.forName(JDBC_NAME);//加载数据库驱动
            conn = DriverManager.getConnection(DATA_URL, DATABASE_NAME, DATABASE_PWD);
            return conn;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return conn;

    }

    /**
     * 查找数据
     *
     * @param sql
     * @param objects
     * @return
     */
    public ResultSet getData(String sql, Object... objects) {
        try {
            getConnection();
            psm = conn.prepareStatement(sql);
            for (int i = 0, len = objects.length; i < len; i++) {
                psm.setObject(i + 1, objects[i]);
            }
            resultSet = psm.executeQuery();
            return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateBalance(int id, float price) {
        System.out.println(id + " " + price);
        return updateData("UPDATE user SET balance = COALESCE(balance, 0) + ? WHERE iduser = ?", price, id) == 1;
    }

    /**
     * 更新数据 插入 删除 修改
     *
     * @param sql
     * @param objects
     * @return
     */
    public int updateData(String sql, Object... objects) {
        PreparedStatement psm = null;
        Connection connection = null;
        try {
            connection = getConnection();
            assert connection != null;
            psm = connection.prepareStatement(sql);
            for (int i = 0, len = objects.length; i < len; i++) {
                psm.setObject(i + 1, objects[i]);
            }
            return psm.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void closeAllConnection() {
        try {
            if (resultSet != null) {
                resultSet.close();
                resultSet = null;
            }
            if (psm != null) {
                psm.close();
                psm = null;
            }
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

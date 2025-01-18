package zzl;

import zzl.beans.json.PathInfoBean;
import zzl.dao.MySql;
import zzl.servlet.PathServlet;

import java.sql.ResultSet;
import java.sql.SQLException;

public class test {
    public static void main(String[] args) throws SQLException {
        MySql mySql = new MySql();
        // 连接 path 表和 user 表来获取司机信息
        ResultSet resultSet = mySql.getData(
                "SELECT u.iduser, u.nickname, u.phone, p.location, p.carriage " +
                        "FROM path p " +
                        "JOIN user u ON p.iduser = u.iduser " +
                        "ORDER BY p.iduser, p.orderNum");
        while (resultSet.next()) {
            PathInfoBean pathInfoBean = new PathInfoBean();
            pathInfoBean.setId(resultSet.getInt("iduser"));
            pathInfoBean.setNickName(resultSet.getString("nickname"));
            pathInfoBean.setPhone(resultSet.getString("phone"));
            pathInfoBean.setPrice(resultSet.getFloat("carriage")); // 运费单价
            pathInfoBean.setLocation(resultSet.getString("location"));
            System.out.println(pathInfoBean.getLocation());
        }

    }
}

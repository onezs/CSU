package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.json.PathBean;
import zzl.beans.json.PathInfoBean;
import zzl.beans.json.Result;
import zzl.dao.MySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AllDriversLocationServlet", urlPatterns = {"/allDriversLocation"})
public class AllDriversLocationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MySql mySql = new MySql();
        Result<List<PathInfoBean>> result = new Result<>();
        List<PathInfoBean> list = new ArrayList<>();
        try {
            // 连接 path 表和 user 表来获取司机信息
            ResultSet resultSet = mySql.getData(
                    "SELECT u.iduser, u.nickname, u.phone, p.location, p.destination, p.carriage " +
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
                pathInfoBean.setDestination(resultSet.getString("destination"));
                // 可以选择添加其他字段
                list.add(pathInfoBean);
            }
            result.setStatus("ok");
            result.setData(list);
        } catch (SQLException e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("Database query failed");
        } finally {
            mySql.closeAllConnection();
        }

        Gson gson = new Gson();
        Utils.send(response, gson.toJson(result));
    }
}

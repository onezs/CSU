package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.json.Order;
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

@WebServlet(name = "FetchPublishedOrdersServlet", urlPatterns = {"/fetchPublishedOrders"})
public class FetchPublishedOrdersServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MySql mySql = new MySql();
        Result<List<Order>> result = new Result<>();
        List<Order> list = new ArrayList<>();

        try {
            // 连接 orders 表和 user 表来获取订单信息及用户昵称和电话
            ResultSet resultSet = mySql.getData(
                    "SELECT o.*, u.nickname, u.phone FROM orders o " +
                            "JOIN user u ON o.ownerid = u.iduser " +
                            "WHERE o.put_order = 1 and o.deleted = 0");

            while (resultSet.next()) {
                Order order = new Order();
                order.setOwnerId(resultSet.getString("ownerid"));
                order.setStartPoint(resultSet.getString("start"));
                order.setEndPoint(resultSet.getString("end"));
                order.setOrderId(resultSet.getInt("idorder"));
                order.setCarriage(resultSet.getFloat("carriage"));
                order.setOwnerName(resultSet.getString("nickname")); // 用户昵称
                order.setPhone(resultSet.getString("phone"));       // 用户电话
                order.setTime(resultSet.getString("time"));
                // 可以选择添加其他字段
                list.add(order);
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

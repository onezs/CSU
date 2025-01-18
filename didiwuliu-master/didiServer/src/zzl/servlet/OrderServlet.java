package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.User;
import zzl.beans.json.OrderBean;
import zzl.beans.json.Result;
import zzl.beans.json.UserInfo;
import zzl.dao.MySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/order"})
public class OrderServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = Utils.getJsonFromStream(request.getInputStream());
        Result<Boolean> result = new Result<>();
        Gson gson = new Gson();

        User user = (User) request.getSession().getAttribute("user");
        if (!json.isEmpty() && user != null) {
            OrderBean orderBean = gson.fromJson(json, OrderBean.class);
            result.setStatus("ok");
            orderBean.setOwnerID(user.getUserID());
            if (isBalanceEnough(user.getUserID(), orderBean.getPrice())) {
                if (pay(orderBean)) {
                    result.setData(true);
                } else {
                    result.setData(false);
                }
            } else {
                result.setData(false);
                result.setMsg("余额不足");
            }
        } else {
            result.setStatus("error");
            result.setMsg("请求失败");
        }
        Utils.send(response, gson.toJson(result));
    }

    /**
     * ????
     *
     * @param order
     * @return
     */
    private boolean pay(OrderBean order) {
        MySql mySql = new MySql();
        List<UserInfo> list = new ArrayList<>();
        try {
            Connection conn = mySql.getConnection();
            //???????
            conn.setAutoCommit(false);
            //????
            mySql.updateBalance(order.getOwnerID(), -order.getPrice());
            //???
            mySql.updateBalance(order.getDriverID(), order.getPrice());
            //?????????
            Statement stat = conn.createStatement();
            String sql = "select * from orders";
            ResultSet rs = stat.executeQuery(sql);
            int size = 1;
            while (rs.next()) {
                size++;
            }
            order.setOrderID(size);


            LocalDateTime currentDateTime = LocalDateTime.now();

            // 定义用于显示日期和时间的格式
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            // 使用定义的格式化程序格式化当前日期和时间
            String formattedDateTime = currentDateTime.format(formatter);

            mySql.updateData("insert into orders (ownerid, driverid, price, start, end,idorder,time) values (?,?,?,?,?,?,?)",
                    order.getOwnerID(),
                    order.getDriverID(),
                    order.getPrice(),
                    order.getStart(),
                    order.getEnd(),
                    order.getOrderID(),
                    formattedDateTime
            );
            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mySql.closeAllConnection();
        }
        return false;
    }

    /**
     * ????????????
     *
     * @param id
     * @param price
     * @return
     */
    private boolean isBalanceEnough(int id, float price) {
        MySql mySql = new MySql();
        List<UserInfo> list = new ArrayList<>();
        try {

            ResultSet rs = mySql.getData("select balance from user where iduser=?", id);
            if (rs.next()) {
                float balance = rs.getFloat("balance");
                return balance >= price;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            mySql.closeAllConnection();
        }
        return false;
    }
}

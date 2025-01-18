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
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "PublishOrderServlet", urlPatterns = {"/publishOrder"})
public class PublishOrderServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 读取请求体中的 JSON 数据
        StringBuilder jsonBuilder = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        String json = jsonBuilder.toString();
        Gson gson = new Gson();
        Order order = gson.fromJson(json, Order.class);
        MySql mySql = new MySql();
        Result<Boolean> result = new Result<>();

        try {
            // 构建 SQL 插入语句
            String insertSql = "INSERT INTO orders (ownerid, start, end, carriage, reward, put_order, time) VALUES (?, ?, ?, ?, ?, 1, ?)";
            int rowsAffected = mySql.updateData(insertSql, order.getOwnerId(), order.getStartPoint(), order.getEndPoint(), order.getCarriage(), order.getReward(), order.getTime());

            if (rowsAffected == 1) {
                result.setStatus("ok");
                result.setData(true);
            } else {
                result.setStatus("error");
                result.setMsg("Failed to publish order");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("Database operation failed");
        } finally {
            mySql.closeAllConnection();
        }

        Utils.send(response, gson.toJson(result));
    }
}

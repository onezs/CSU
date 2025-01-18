package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.json.Result;
import zzl.dao.MySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "DeleteOrderServlet", urlPatterns = {"/deleteOrder"})
public class DeleteOrderServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        MySql mySql = new MySql();
        Result<Boolean> result = new Result<>();

        try {
            // 从请求参数中获取订单ID
            String orderIdStr = request.getParameter("orderId");
            if (orderIdStr != null && !orderIdStr.isEmpty()) {
                int orderId = Integer.parseInt(orderIdStr);

                // 执行更新订单的操作，将deleted字段修改为1
                String updateSql = "UPDATE orders SET deleted = 1 WHERE idorder = " + orderId;
                int rowsAffected = mySql.updateData(updateSql);

                if (rowsAffected == 1) {
                    result.setStatus("ok");
                    result.setData(true);
                } else {
                    result.setStatus("error");
                    result.setMsg("Failed to update order");
                }
            } else {
                result.setStatus("error");
                result.setMsg("Order ID is missing in the request");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("Invalid order ID format");
        } finally {
            mySql.closeAllConnection();
        }

        Gson gson = new Gson();
        Utils.send(response, gson.toJson(result));
    }
}

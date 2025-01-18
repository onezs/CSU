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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(name = "AcceptOrderServlet", urlPatterns = {"/acceptOrder"})
public class AcceptOrderServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        MySql mySql = new MySql();
        Result<Boolean> result = new Result<>();

        try {
            String orderIdStr = request.getParameter("orderId");
            String driverId = request.getParameter("driverId");

            if (orderIdStr != null && driverId != null) {
                int orderId = Integer.parseInt(orderIdStr);

                // 获取订单信息
                String orderQuery = "SELECT * FROM orders WHERE idorder = " + orderId;
                ResultSet orderResultSet = mySql.getData(orderQuery);

                // 获取司机路径信息
                String pathQuery = "SELECT * FROM path WHERE iduser = " + driverId;
                ResultSet pathResultSet = mySql.getData(pathQuery);

                if (orderResultSet.next() && pathResultSet.next()) {
                    String pathStart = pathResultSet.getString("location");
                    String pathEnd = pathResultSet.getString("destination");


                    String orderStart = orderResultSet.getString("start");
                    String orderEnd = orderResultSet.getString("end");
                    /*float pathPrice = pathResultSet.getFloat("carriage");
                    float orderWeight = orderResultSet.getFloat("carriage");
                    float orderReward = orderResultSet.getFloat("reward");*/

                    // 检查路径是否符合订单
                    boolean isPathValid = (orderStart.equals(pathStart) && orderEnd.equals(pathEnd)) ||
                            (orderStart.equals(pathEnd) && orderEnd.equals(pathStart));

                    // 比较路径是否匹配
                    if (isPathValid) {

                        // 计算价格
                        float price = orderResultSet.getFloat("carriage") * pathResultSet.getFloat("carriage");
                        float total = price + orderResultSet.getFloat("reward");
                        String ownerId = orderResultSet.getString("ownerid");

                        // 更新订单信息
                        LocalDateTime currentDateTime = LocalDateTime.now();
                        // 定义用于显示日期和时间的格式
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        // 使用定义的格式化程序格式化当前日期和时间
                        String formattedDateTime = currentDateTime.format(formatter);
                        String updateOrder = "UPDATE orders SET driverid = ?, price = ?, time = ?, put_order = 0 WHERE idorder = ?";
                        mySql.updateData(updateOrder, driverId, price, formattedDateTime, orderId);

                        // 更新司机余额
                        String updateBalance = "UPDATE user SET balance = COALESCE(balance, 0) + ? WHERE iduser = ?";
                        mySql.updateData(updateBalance, total, driverId);

                        // 减去司机从货主余额中获得的金额
                        String updateOwnerBalance = "UPDATE user SET balance = COALESCE(balance, 0) - ? WHERE iduser = ?";
                        mySql.updateData(updateOwnerBalance, total, ownerId);


                        result.setStatus("ok");
                        result.setData(true);
                    } else {
                        result.setStatus("ok");
                        result.setData(false);
                        result.setMsg("路线不符合");
                    }
                } else {
                    result.setStatus("error");
                    result.setMsg("您还没有添加路线");
                }
            } else {
                result.setStatus("error");
                result.setMsg("Missing orderId or ownerId");
            }
        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("Error processing request");
        } finally {
            mySql.closeAllConnection();
        }

        Gson gson = new Gson();
        Utils.send(response, gson.toJson(result));
    }
}

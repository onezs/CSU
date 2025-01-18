package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.User;
import zzl.beans.json.Result;
import zzl.dao.MySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.ResultSet;

@WebServlet(name = "ChangePasswordServlet", urlPatterns = {"/changepassword"})
public class ChangePasswordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySql mySql = new MySql();
        User user = (User) request.getSession().getAttribute("user");
        Result<Boolean> result = new Result<>();
        try {
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");

            result.setStatus("error");
            result.setData(false);

            if (user != null && oldPassword != null && newPassword != null) {
                // 验证旧密码是否正确
                ResultSet rs = mySql.getData("SELECT pwd FROM user WHERE iduser = ?", user.getUserID());
                if (rs != null && rs.next() && rs.getString("pwd").equals(oldPassword)) {
                    // 旧密码正确，更新为新密码
                    int updateCount = mySql.updateData("UPDATE user SET pwd = ? WHERE iduser = ?", newPassword, user.getUserID());
                    if (updateCount > 0) {
                        result.setStatus("ok");
                        result.setData(true);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("error");
        } finally {
            mySql.closeAllConnection();
        }
        Gson gson = new Gson();
        Utils.send(response, gson.toJson(result));
    }
}

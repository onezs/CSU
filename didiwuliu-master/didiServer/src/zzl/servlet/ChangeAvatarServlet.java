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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Base64;

@WebServlet(name = "ChangeAvatarServlet", urlPatterns = {"/changeavatar"})
public class ChangeAvatarServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MySql mySql = new MySql();
        User user = (User) request.getSession().getAttribute("user");
        Result<Boolean> result = new Result<>();
        try {
            String avatarBase64 = request.getParameter("avatarBase64");

            result.setStatus("error");
            result.setData(false);

            if (user != null && avatarBase64 != null && !avatarBase64.isEmpty()) {
                // 将 Base64 字符串转换为二进制数据
                byte[] avatarBytes = Base64.getMimeDecoder().decode(avatarBase64);

                // 更新用户头像
                String sql = "UPDATE user SET image = ? WHERE iduser = ?";
                try (PreparedStatement stmt = mySql.getConnection().prepareStatement(sql)) {
                    stmt.setBytes(1, avatarBytes);
                    stmt.setInt(2, user.getUserID());

                    int updateCount = stmt.executeUpdate();
                    if (updateCount > 0) {
                        result.setStatus("ok");
                        result.setData(true);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    result.setStatus("error");
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

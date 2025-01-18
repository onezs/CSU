package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.json.RegisterBeans;
import zzl.beans.json.Result;
import zzl.dao.MySql;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/register"})
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Result<Boolean> result = new Result<>();
        Gson gson = new Gson();
        try {
            String json = Utils.getJsonFromStream(request.getInputStream());
            RegisterBeans registerBeans = gson.fromJson(json, RegisterBeans.class);
            result.setStatus("ok");
            if (check(registerBeans.getPhone())) {
                result.setData(register(registerBeans));
            } else {
                result.setData(false);
                result.setMsg("手机号码已注册");
            }
        } catch (IOException e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("注册失败");
        }
        Utils.send(response, gson.toJson(result));
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        super.doGet(request, response);
    }

    /**
     * 检查手机号是否已注册
     *
     * @param phone
     * @return
     */
    private boolean check(String phone) {
        MySql sql = new MySql();

        try {
            ResultSet rs = sql.getData("select iduser from user where phone=?", phone);
            if (rs.next())
                return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            sql.closeAllConnection();
        }
        return true;
    }

    /**
     * 注册
     *
     * @param registerBeans
     * @return
     */
    private boolean register(RegisterBeans registerBeans) {
        MySql mySql = new MySql();

        try {
            PreparedStatement stmt = mySql.getConnection().prepareStatement("insert into user (phone, pwd, nickname, type, image, sex) values (?, ?, ?, ?, ?, ?)");
            stmt.setString(1, registerBeans.getPhone());
            stmt.setString(2, registerBeans.getPwd());
            stmt.setString(3, registerBeans.getNickName());
            stmt.setString(4, String.valueOf(registerBeans.getType()));

            if (registerBeans.getImageBase64() != null && !registerBeans.getImageBase64().isEmpty()) {
                // 将 Base64 字符串解码为二进制数据
                byte[] imageBytes = Base64.getMimeDecoder().decode(registerBeans.getImageBase64());
                stmt.setBytes(5, imageBytes);
            } else {
                stmt.setNull(5, java.sql.Types.BLOB);
            }
            stmt.setString(6,registerBeans.getGender());

            int result = stmt.executeUpdate();
            return result == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            mySql.closeAllConnection();
        }
    }
}

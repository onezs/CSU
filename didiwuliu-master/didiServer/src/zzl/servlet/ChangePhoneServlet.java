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

@WebServlet(name = "ChangePhoneServlet", urlPatterns = {"/changephone"})
public class ChangePhoneServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String userId = request.getParameter("userId");
        String newPhone = request.getParameter("newPhone");

        Result<Boolean> result = new Result<>();
        Gson gson = new Gson();

        try {
            MySql mySql = new MySql();
            int rowsUpdated = mySql.updateData("UPDATE user SET phone = ? WHERE iduser = ?", newPhone, userId);

            if (rowsUpdated > 0) {
                result.setStatus("ok");
                result.setData(true);
            } else {
                result.setStatus("error");
                result.setMsg("Failed to update phone number");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus("error");
            result.setMsg("Server error");
        }

        Utils.send(response, gson.toJson(result));
    }
}


package zzl.servlet;

import com.google.gson.Gson;
import zzl.Utils;
import zzl.beans.json.PathInfoBean;
import zzl.beans.json.Result;
import zzl.beans.json.SearchBean;
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

@WebServlet(name = "SearchServlet",urlPatterns = {"/search"})
public class SearchServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		String json = Utils.getJsonFromStream(request.getInputStream());
		Result<List<PathInfoBean>> result = new Result<>();
		Gson gson = new Gson();
		if (!json.isEmpty()) {
			SearchBean searchBean = gson.fromJson(json, SearchBean.class);
			List<PathInfoBean> list = search(searchBean);

			result.setStatus("ok");
			result.setData(list);

		} else {
			result.setStatus("error");
			result.setMsg("错误的访问参数");
		}
		Utils.send(response, gson.toJson(result));
	}




	/**
	 * 查询经过这两个地点的司机的信息和运费单价
	 * @param search
	 * @return
	 */

	private List<PathInfoBean> search(SearchBean search) {
		MySql mySql = new MySql();
		List<PathInfoBean> list = new ArrayList<>();
		try {
			// 修改 SQL 查询以考虑起点和终点
			ResultSet rs = mySql.getData(
					"SELECT user.iduser, nickname, phone, carriage, location, destination " +
							"FROM path " +
							"INNER JOIN user ON user.iduser = path.iduser " +
							"WHERE (location = ? AND destination = ?) OR " +
							"(destination = ? AND location = ?)",
					search.getStart(), search.getEnd(), search.getStart(), search.getEnd());

			while (rs.next()) {
				PathInfoBean pathInfo = new PathInfoBean();
				pathInfo.setPhone(rs.getString("phone"));
				pathInfo.setNickName(rs.getString("nickname"));
				pathInfo.setId(rs.getInt("user.iduser"));
				pathInfo.setPrice(rs.getFloat("carriage"));
				pathInfo.setLocation(rs.getString("location"));
				pathInfo.setDestination(rs.getString("destination"));
				list.add(pathInfo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			mySql.closeAllConnection();
		}
		return list;
	}
}

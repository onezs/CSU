<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html>
<head>
	<title>滴滴物流</title>
	<style type="text/css">
		body {
			font-family: Arial, sans-serif;
			background-color: #f4f4f4;
			margin: 0;
			padding: 0;
			color: #333;
		}
		.container {
			width: 80%;
			margin: auto;
			overflow: hidden;
		}
		header {
			background: #50b3a2;
			color: white;
			padding-top: 30px;
			min-height: 70px;
			border-bottom: #e8491d 3px solid;
		}
		header a {
			color: #ffffff;
			text-decoration: none;
			text-transform: uppercase;
			font-size: 16px;
		}
		header ul {
			padding: 0;
			list-style: none;
			text-align: center;
		}
		header li {
			display: inline;
			margin: 0 20px;
		}
		header #branding {
			float: left;
		}
		header #branding h1 {
			margin: 0;
		}
		header nav {
			float: right;
			margin-top: 10px;
		}
		header .highlight, header .current a {
			color: #e8491d;
			font-weight: bold;
		}
		header a:hover {
			color: #ffffff;
			font-weight: bold;
		}
		table {
			width: 100%;
			border-collapse: collapse;
			margin: 20px 0;
		}
		table, th, td {
			border: 1px solid #ddd;
		}
		th, td {
			padding: 8px;
			text-align: left;
		}
		th {
			background-color: #50b3a2;
			color: white;
		}
		tr:nth-child(even) {
			background-color: #f2f2f2;
		}
		tr:hover {
			background-color: #ddd;
		}
	</style>
</head>
<body>
<header>
	<div class="container">
		<div id="branding">
			<h1><span class="highlight">滴滴</span> 物流管理系统</h1>
		</div>
		<nav>
			<ul>
				<li class="current"><a href="index.jsp">首页</a></li>
				<li><a href="about.jsp">关于我们</a></li>
				<li><a href="services.jsp">服务</a></li>
			</ul>
		</nav>
	</div>
</header>

<div class="container">
	<%
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			String url = "jdbc:mysql://localhost:3306/didi";
			String user = "root";
			String password = "123456";

			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);

			// 展示Orders表
			String sql = "SELECT * FROM orders";
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			out.println("<h2>订单表:</h2>");
			out.println("<table border='1'><tr><th>订单ID</th><th>货主ID</th><th>司机ID</th><th>价格</th><th>起点</th><th>终点</th><th>订单状态</th><th>运费</th><th>是否撤销</th><th>奖励</th><th>时间</th></tr>");
			while (rs.next()) {
				out.println("<tr><td>" + rs.getInt("idorder") + "</td><td>" + rs.getString("ownerid") + "</td><td>" + rs.getString("driverid") + "</td><td>" + rs.getString("price") + "</td><td>" + rs.getString("start") + "</td><td>" + rs.getString("end") + "</td><td>" + (rs.getInt("put_order") == 0 ? "已接单" : "已发布") + "</td><td>" + rs.getFloat("carriage") + "</td><td>" + (rs.getInt("deleted") == 0 ? "否" : "是") + "</td><td>" + rs.getInt("reward") + "</td><td>" + rs.getTimestamp("time") + "</td></tr>");
			}
			out.println("</table>");

			// 展示Path表
			sql = "SELECT * FROM path";
			rs = stmt.executeQuery(sql);
			out.println("<h2>路线表:</h2>");
			out.println("<table border='1'><tr><th>起点</th><th>运费</th><th>用户ID</th><th>订单号</th><th>终点</th></tr>");
			while (rs.next()) {
				out.println("<tr><td>" + rs.getString("location") + "</td><td>" + rs.getString("carriage") + "</td><td>" + rs.getString("iduser") + "</td><td>" + rs.getInt("ordernum") + "</td><td>" + rs.getString("destination") + "</td></tr>");
			}
			out.println("</table>");

			// 展示User表
			sql = "SELECT * FROM user";
			rs = stmt.executeQuery(sql);
			out.println("<h2>用户表:</h2>");
			out.println("<table border='1'><tr><th>用户ID</th><th>密码</th><th>类型</th><th>昵称</th><th>余额</th><th>性别</th><th>电话</th></tr>");
			while (rs.next()) {
				out.println("<tr><td>" + rs.getInt("iduser") + "</td><td>" + rs.getString("pwd") + "</td><td>" + rs.getInt("type") + "</td><td>" + rs.getString("nickname") + "</td><td>" + rs.getString("balance") + "</td><td>" + rs.getString("sex") + "</td><td>" + rs.getString("phone") + "</td></tr>");
			}
			out.println("</table>");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			if (stmt != null) try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
			if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	%>
</div>
</body>
</html>

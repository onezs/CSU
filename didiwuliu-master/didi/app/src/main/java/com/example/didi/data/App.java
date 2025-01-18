package com.example.didi.data;



import android.os.Handler;

import com.example.didi.beans.UserInfoBean;


//保存用户信息，token，以及对应的Handler
public class App {
    static private UserInfoBean user;
    static private String token = "";
    static private Handler homeFragmentHandler; //首页的handler，用于刷新首页订单列表数据
    static private Handler userInfoFragmentHandler; //我的页面的handler，用于刷新用户基本信息
    static private Handler orderDetailHandler; //订单详情页面的handler，用于刷新订单详情数据
    static private Handler notificationFragmentHandler; //消息页面的handler，用于消息数据
    static private Handler dashboardFragmentHandler; //订单页面的handler，用于刷新订单列表数据

    public static UserInfoBean getUser() {
        return user;
    }

    public static void setUser(UserInfoBean user) {
        App.user = user;
    }

    public static String getToken() {
        return token;
    }

    public static void setToken(String token) {
        App.token = token;
    }

    public static Handler getHomeFragmentHandler() {
        return homeFragmentHandler;
    }

    public static void setHomeFragmentHandler(Handler homeFragmentHandler) {
        App.homeFragmentHandler = homeFragmentHandler;
    }

    public static Handler getUserInfoFragmentHandler() {
        return userInfoFragmentHandler;
    }

    public static void setUserInfoFragmentHandler(Handler userInfoFragmentHandler) {
        App.userInfoFragmentHandler = userInfoFragmentHandler;
    }

    public static Handler getOrderDetailHandler() {
        return orderDetailHandler;
    }

    public static void setOrderDetailHandler(Handler orderDetailHandler) {
        App.orderDetailHandler = orderDetailHandler;
    }

    public static Handler getNotificationFragmentHandler() {
        return notificationFragmentHandler;
    }

    public static void setNotificationFragmentHandler(Handler notificationFragmentHandler) {
        App.notificationFragmentHandler = notificationFragmentHandler;
    }

    public static Handler getDashboardFragmentHandler() {
        return dashboardFragmentHandler;
    }

    public static void setDashboardFragmentHandler(Handler dashboardFragmentHandler) {
        App.dashboardFragmentHandler = dashboardFragmentHandler;
    }
}


package com.example.didi.ui.contact_order;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.didi.beans.Order;
import com.example.didi.beans.PathInfoBean;
import com.example.didi.beans.SendBean;
import com.example.didi.data.DataShare;
import com.example.didi.data.Result;
import com.example.didi.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OrderViewModel extends ViewModel {
    private MutableLiveData<List<Order>> mOrders;
    private int mType;
    private int userID;

    public OrderViewModel() {
        mOrders = new MutableLiveData<>();
    }

    public LiveData<List<Order>> getOrders() {
        return mOrders;
    }

    /**
     * 从服务器获取发布的订单信息
     */
    public void fetchOrders() {
        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/fetchPublishedOrders") // 假设这是获取订单的URL
                .build();

        HttpUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                // 处理失败的情况
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Gson gson = new Gson();
                //List<Order> orders = gson.fromJson(json, new TypeToken<List<Order>>() {}.getType());

                SendBean<List<Order>> sendBean = gson.fromJson(json,
                        new TypeToken<SendBean<List<Order>>>() {}.getType());
                if (sendBean.getStatus().equals("ok")) {
                    List<Order> filteredOrders;
                    // 定义时间格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    mType = DataShare.getUser().getType();
                    userID = DataShare.getUser().getId();
                    if (mType == 0) {
                        // 过滤出 ownerdId 是当前登录用户的订单
                        filteredOrders = filterOrders(sendBean.getData(), userID);
                    } else {
                        // 不过滤，直接使用原始订单列表
                        filteredOrders = sendBean.getData();
                    }
                    // 对订单进行排序
                    Collections.sort(filteredOrders, new Comparator<Order>() {
                        @Override
                        public int compare(Order o1, Order o2) {
                            try {
                                Date date1 = dateFormat.parse(o1.getTime());
                                Date date2 = dateFormat.parse(o2.getTime());
                                return date2.compareTo(date1);
                            } catch (ParseException e) {
                                e.printStackTrace();
                                return 0;
                            }
                        }
                    });
                    mOrders.postValue(filteredOrders);
                }

            }
        });
    }
    private List<Order> filterOrders(List<Order> orders, int currentUserId) {
        List<Order> filteredOrders = new ArrayList<>();

        for (Order order : orders) {
            if (order.getOwnerId().equals(String.valueOf(currentUserId))) {
                // 只保留 ownerdId 是当前登录用户的订单
                filteredOrders.add(order);
            }
        }

        return filteredOrders;
    }



}


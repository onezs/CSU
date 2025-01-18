package com.example.didi.ui.contact_order;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didi.R;
import com.example.didi.beans.Order;
import com.example.didi.beans.SendBean;
import com.example.didi.utils.HttpUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private static List<Order> orderList;
    private int mType; // 添加成员变量 mType
    private int userID;




    public OrderAdapter(List<Order> orderList,int mType,int userID) {
        this.orderList = orderList;
        this.mType = mType;
        this.userID = userID;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.put_order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order,mType,userID);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void setOrders(List<Order> orders) {
        this.orderList = orders;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        // 定义视图组件
        private TextView tvOrderId, tvStart, tvEnd, tvOwner, tvPhone, tvWeight, tvOwnerid, tvTime;
        private Button btnAcceptOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化视图组件
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvStart = itemView.findViewById(R.id.tv_start);
            tvEnd = itemView.findViewById(R.id.tv_end);
            tvOwner = itemView.findViewById(R.id.tv_nick_name);
            tvOwnerid = itemView.findViewById(R.id.tv_account);
            tvPhone = itemView.findViewById(R.id.tv_phone);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvTime = itemView.findViewById(R.id.tv_order_time);
            btnAcceptOrder = itemView.findViewById(R.id.btn_accept_order);
        }

        public void bind(Order order, int mType, int userID) {
            // 绑定订单数据到视图组件
            tvOrderId.setText(String.valueOf(order.getOrderId()));
            tvStart.setText(order.getStartPoint());
            tvEnd.setText(order.getEndPoint());
            tvOwner.setText(order.getOwnerName());
            tvPhone.setText(order.getPhone());
            tvWeight.setText(String.valueOf(order.getCarriage()));
            tvOwnerid.setText(order.getOwnerId());
            tvTime.setText(order.getTime());
            // 根据订单状态设置按钮的可见性或行为
            if (mType == 0) { // 货主
                btnAcceptOrder.setText("删除订单");
                btnAcceptOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 实现删除订单的逻辑
                        int orderID = order.getOrderId();
                        deleteOrder(orderID);
                    }
                });
            } else if (mType == 1) { // 司机
                btnAcceptOrder.setText("接受订单");
                btnAcceptOrder.setBackgroundColor(Color.GREEN);
                btnAcceptOrder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 实现接受订单的逻辑
                        int orderID = order.getOrderId();
                        acceptOrder(orderID, String.valueOf(userID));
                    }
                });
            }
        }
        /**
         * 接受订单
         * @param orderId 订单ID
         */
        public void acceptOrder(int orderId,String driverId) {
            Request request = new Request.Builder()
                    .url(HttpUtils.BASE_URL + "/acceptOrder?orderId=" + orderId + "&driverId=" + driverId)
                    .build();

            HttpUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // 处理失败的情况
                    ((Activity) itemView.getContext()).runOnUiThread(() -> Toast.makeText(itemView.getContext(), "网络请求失败", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String json = response.body().string();
                    Gson gson = new Gson();
                    SendBean<Boolean> result = gson.fromJson(json, new TypeToken<SendBean<Boolean>>() {}.getType());

                    ((Activity) itemView.getContext()).runOnUiThread(() -> {
                        if (result.getStatus().equals("ok")) {
                            if (result.getData()) {
                                // 请求成功，订单已接受，更新视图
                                Toast.makeText(itemView.getContext(), "订单已接受", Toast.LENGTH_SHORT).show();
                                // 这里添加更新UI的代码，例如刷新列表
                                removeOrder(orderId);
                            } else {
                                // 请求成功，但服务器逻辑处理失败，例如路径不符
                                Toast.makeText(itemView.getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 请求失败
                            Toast.makeText(itemView.getContext(), result.getMsg(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
        /**
         * 删除订单
         * @param orderId 订单ID
         */
        public void deleteOrder(int orderId) {
            Request request = new Request.Builder()
                    .url(HttpUtils.BASE_URL + "/deleteOrder?orderId=" + orderId) // 假设这是删除订单的URL
                    .build();

            HttpUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    // 处理失败的情况
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String json = response.body().string();
                    // 处理服务器返回的响应
                    // 更新 LiveData 或其他逻辑
                    ((Activity) itemView.getContext()).runOnUiThread(() -> {
                            removeOrder(orderId);
                    });

                }
            });
        }
        private void removeOrder(int orderId) {
            for (int i = 0; i < orderList.size(); i++) {
                if (orderList.get(i).getOrderId() == orderId) {
                    orderList.remove(i);
                    notifyItemRemoved(i);
                    break;
                }
            }
        }


    }


}


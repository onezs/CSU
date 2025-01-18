package com.example.didi.ui.contact_order;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.didi.R;
import com.example.didi.beans.Order;
import com.example.didi.data.DataShare;
import com.example.didi.ui.home.HomeViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {
    private RecyclerView recyclerView;
    private OrderViewModel orderViewModel;
    private OrderAdapter adapter;
    private List<Order> orderList; // 这应该是你的订单数据列表
    private FloatingActionButton fabAddOrder;
    private int userID;
    private int mType;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);

        orderViewModel = ViewModelProviders.of(this).get(OrderViewModel.class);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化订单列表（示例）
        orderList = new ArrayList<>();
        mType = DataShare.getUser().getType();
        userID = DataShare.getUser().getId();

        adapter = new OrderAdapter(orderList,mType,userID);
        recyclerView.setAdapter(adapter);

        // 获取订单数据
        orderViewModel.fetchOrders();
        orderViewModel.getOrders().observe(getViewLifecycleOwner(), orders -> {
            // 更新 RecyclerView 的数据
            adapter.setOrders(orders);
            adapter.notifyDataSetChanged();
        });

        fabAddOrder = view.findViewById(R.id.fab_add_order);
        if (mType == 0) {
            fabAddOrder.setVisibility(View.VISIBLE);
            fabAddOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 启动发布订单的 Activity
                    startPublishOrderActivity();
                }
            });
        } else {
            // 如果 mType 不是 0（可能是司机），则隐藏悬浮按钮
            fabAddOrder.setVisibility(View.GONE);
        }
        return view;
    }

    private void startPublishOrderActivity() {
        Intent intent = new Intent(getContext(), PublishOrderActivity.class);
        startActivityForResult(intent, PublishOrderActivity.REQUEST_CODE_PUBLISH_ORDER);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PublishOrderActivity.REQUEST_CODE_PUBLISH_ORDER && resultCode == RESULT_OK) {
            // 刷新订单列表
            orderViewModel.fetchOrders();
        }
    }



}

package com.example.didi.ui.contact_order;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.didi.R;
import com.example.didi.beans.Order;
import com.example.didi.data.DataShare;
import com.example.didi.utils.HttpUtils;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PublishOrderActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_PUBLISH_ORDER = 1;
    private EditText editTextStartLocation;
    private EditText editTextEndLocation;
    private EditText editTextCarriage;
    private EditText editTextReward;
    private Button buttonSubmitOrder;
    private int userID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_order);

        editTextStartLocation = findViewById(R.id.editTextStartLocation);
        editTextEndLocation = findViewById(R.id.editTextEndLocation);
        editTextCarriage = findViewById(R.id.editTextCarriage);
        editTextReward = findViewById(R.id.editTextReward);
        buttonSubmitOrder = findViewById(R.id.buttonSubmitOrder);
        userID = DataShare.getUser().getId();

        buttonSubmitOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitOrder();
            }
        });
    }

    private void submitOrder() {
        String startLocation = editTextStartLocation.getText().toString();
        String endLocation = editTextEndLocation.getText().toString();
        String carriage = editTextCarriage.getText().toString();
        String reward = editTextReward.getText().toString();

        // 校验输入
        if (startLocation.isEmpty() || endLocation.isEmpty() || reward.isEmpty() || carriage.isEmpty()) {
            Toast.makeText(this, "所有字段都必须填写", Toast.LENGTH_SHORT).show();
            return;
        }

        publishOrderToServer(startLocation, endLocation, reward, carriage);
    }

    private void publishOrderToServer(String startLocation, String endLocation, String reward, String carriage) {
        Gson gson = new Gson();
        // 构造发送到服务器的数据对象
        Order orderData = new Order();
        orderData.setStartPoint(startLocation);
        orderData.setEndPoint(endLocation);
        orderData.setOwnerId(String.valueOf(userID));
        orderData.setCarriage(Float.parseFloat(carriage));
        orderData.setReward(Float.parseFloat(reward)); // 将字符串转换为浮点数
        LocalDateTime currentDateTime = null;
        DateTimeFormatter formatter = null;
        String formattedDateTime = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            formattedDateTime = currentDateTime.format(formatter);
        }

        orderData.setTime(formattedDateTime);
        String json = gson.toJson(orderData);

        RequestBody requestBody = RequestBody.create(json, HttpUtils.JSON);

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/publishOrder") // 假设这是发布订单的URL
                .post(requestBody)
                .build();

        HttpUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> Toast.makeText(PublishOrderActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String jsonResponse = response.body().string();
                runOnUiThread(() -> {
                    // 处理服务器返回的响应
                    // 例如显示一个Toast消息
                    Toast.makeText(PublishOrderActivity.this, "订单发布成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }
        });
    }

}

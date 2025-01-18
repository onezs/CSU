package com.example.didi.ui.driverdetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.didi.R;
import com.example.didi.beans.OrderBean;
import com.example.didi.beans.PathBean;
import com.example.didi.beans.SearchBean;
import com.example.didi.beans.SendBean;
import com.example.didi.data.DataShare;
import com.example.didi.utils.HttpUtils;
import com.example.didi.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DriverDetailActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";
    public static final String EXTRA_NICK_NAME = "nick_name";
    public static final String EXTRA_PHONE = "phone";
    public static final String EXTRA_LOCATION = "location";
    public static final String EXTRA_DESTINATION = "destination";
    public static final String EXTRA_CARRIAGE = "intent_carriage";
    public static final String EXTRA_JUMP = "jump";// 跳转
    private int mID;
    private String path;
    private String location;
    private String destination;
    private Handler mHandler = new Handler();
    private TextView mPathTv;
    private EditText weightEt;
    private TextView priceTv;
    private Button payBtn;
    private ProgressBar mProgressBar;

    private float price = 0;
    private float carriage = 0;//运费单价
    private PathBean[] mPathBeans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);
        TextView nickNameTv = findViewById(R.id.tv_nick_name);
        TextView phoneTv = findViewById(R.id.tv_phone);
        priceTv = findViewById(R.id.tv_price);
        weightEt = findViewById(R.id.et_weight);
        mPathTv = findViewById(R.id.tv_path);
        payBtn = findViewById(R.id.btn_pay);
        mProgressBar = findViewById(R.id.progressBar);

        //从intent读取传入数据
        Intent intent = getIntent();
        nickNameTv.setText(intent.getStringExtra(EXTRA_NICK_NAME));
        phoneTv.setText(intent.getStringExtra(EXTRA_PHONE));
        location = intent.getStringExtra(EXTRA_LOCATION);
        destination = intent.getStringExtra(EXTRA_DESTINATION);
        path = location + "--" + destination;
        mPathTv.setText(path);
        mID = intent.getIntExtra(EXTRA_ID, 0);
        carriage = Float.parseFloat(Objects.requireNonNull(intent.getStringExtra(EXTRA_CARRIAGE)));
        priceTv.setText(Utils.formatBalance(carriage));
        weightEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String weightStr = editable.toString();
                if (!TextUtils.isEmpty(weightStr)) {
                    int weight = Integer.parseInt(weightStr);
                    price = weight * carriage;
                    priceTv.setText(Utils.formatBalance(price));
                }
            }
        });
        //priceTv.setText(Utils.formatBalance(price));

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = new AlertDialog.Builder(DriverDetailActivity.this)
                        .setTitle("提示")
                        .setMessage("确定要付款吗?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                order();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).create();
                alertDialog.show();
            }
        });


        setActionBar();

    }


    private void setLoading(boolean loading) {
        if (loading) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
        payBtn.setEnabled(!loading);
    }

    //下订单
    private void order() {
        if (price < 0)
            return;
        setLoading(true);
        Gson gson = new Gson();

        OrderBean orderBean = new OrderBean();
        //司机id
        orderBean.setDriverID(mID);
        //运费
        orderBean.setPrice(price);
        SearchBean searchBean = DataShare.getSearchBean();
        //起点
        orderBean.setStart(location);
        //终点
        orderBean.setEnd(destination);

        String json = gson.toJson(orderBean);
        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/order")
                .post(RequestBody.create(json, HttpUtils.JSON))
                .build();
        HttpUtils.getOkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setLoading(false);
                        showToast("服务器不可用");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Log.d("search", json);
                SendBean<Boolean> sendBean = gson.fromJson(json
                        , new TypeToken<SendBean<Boolean>>() {
                        }.getType());
                if (sendBean.getStatus().equals("ok")) {
                    if (sendBean.getData() != null) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                setLoading(false);
                                if (sendBean.getData()) {
                                    showToast("付款成功");
                                    paySuccess();

                                } else {
                                    showToast("余额不足");
                                }
                            }
                        });

                    }
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            setLoading(false);
                            showToast(sendBean.getMsg());
                        }
                    });
                }
            }
        });
    }

    /**
     * 付款成功回调函数
     */
    private void paySuccess() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_JUMP, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void showToast(String str) {
        Toast.makeText(DriverDetailActivity.this, str, Toast.LENGTH_SHORT).show();
    }

    private void setActionBar() {

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}

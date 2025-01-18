package com.example.didi.ui.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.didi.R;
import com.example.didi.beans.SendBean;
import com.example.didi.beans.UserInfoBean;
import com.example.didi.data.DataShare;
import com.example.didi.data.LoginRepository;
import com.example.didi.ui.login.LoginActivity;
import com.example.didi.utils.HttpUtils;
import com.example.didi.utils.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserFragment extends Fragment {
    private ImageView imageViewAvatar;
    private static final int PICK_IMAGE_REQUEST = 1;
    private UserViewModel mUserViewModel;
    private Handler mHandler = new Handler();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mUserViewModel =
                ViewModelProviders.of(this).get(UserViewModel.class);
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        final TextView tvAccount = root.findViewById(R.id.tv_account);
        final TextView tvNickName = root.findViewById(R.id.tv_nick_name);
        final TextView tvPhone = root.findViewById(R.id.tv_phone);
        final TextView tvSex = root.findViewById(R.id.tv_sex);
        imageViewAvatar = root.findViewById(R.id.imageViewAvatar);
        imageViewAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });
        TextView tvBalance = root.findViewById(R.id.tv_balance);
        Button button = root.findViewById(R.id.btn_logout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (LoginRepository.getInstance().logout(getActivity())) {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    Activity activity = getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            });
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), "注销失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        button = root.findViewById(R.id.btn_add_balance);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = getLayoutInflater().inflate(R.layout.dialog_add_balance, null);
                EditText editText = v.findViewById(R.id.editText);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("充值");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(getContext(), PayDemoActivity.class);
                        intent.putExtra("orderID", generateOrderNumber());
                        intent.putExtra("money", editText.getText().toString());
                        intent.putExtra("description",getTime());
                        startActivity(intent);
                        addBalance(editText.getText().toString());
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setView(v);
                AlertDialog alertDialog = builder
                        .create();

                alertDialog.show();

            }
        });
        Button changePasswordButton = root.findViewById(R.id.btn_change_password);
        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangePasswordDialog();
            }
        });
        Button changeNicknameButton = root.findViewById(R.id.btn_change_nickname);
        changeNicknameButton.setOnClickListener(v -> showChangeNicknameDialog());

        Button changePhoneButton = root.findViewById(R.id.btn_change_phone);
        changePhoneButton.setOnClickListener(v -> showChangePhoneDialog());

        mUserViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<UserInfoBean>() {
            @Override
            public void onChanged(UserInfoBean user) {
                tvAccount.setText(String.valueOf(user.getId()));
                tvNickName.setText(user.getNickName());
                tvPhone.setText(user.getPhone());
                tvSex.setText(user.getSex());
                tvBalance.setText(Utils.formatBalance(user.getBalance()));
                if (user.getAvatarBase64() != null) {
                    Bitmap avatarBitmap = decodeBase64ToBitmap(user.getAvatarBase64());
                    imageViewAvatar.setImageBitmap(avatarBitmap);
                }
            }
        });

        updateView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(HttpUtils.updateUserInfoFromInternet())
                {
                    updateView();
                }

            }
        }).start();
        return root;
    }
    private void showChangeNicknameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改昵称");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newNickname = input.getText().toString();
            changeNickname(newNickname);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }
    private void showChangePhoneDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("修改手机号");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        builder.setView(input);

        builder.setPositiveButton("确定", (dialog, which) -> {
            String newPhone = input.getText().toString();
            changePhone(newPhone);
        });
        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void changeNickname(String newNickname) {
        OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
        Gson gson = new Gson();

        String userId = getCurrentUserId();

        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("newNickname", newNickname)
                .build();

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/changenickname")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(() -> Toast.makeText(getContext(), "昵称更新失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                SendBean<Boolean> result = gson.fromJson(json, new TypeToken<SendBean<Boolean>>(){}.getType());
                mHandler.post(() -> {
                    if (result.getStatus().equals("ok") && result.getData()) {
                        Toast.makeText(getContext(), "昵称更新成功", Toast.LENGTH_SHORT).show();
                        updateLocalUserInfo(newNickname, null);
                        updateView(); // 更新视图
                    } else {
                        Toast.makeText(getContext(), "昵称更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void changePhone(String newPhone) {
        OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
        Gson gson = new Gson();

        String userId = getCurrentUserId();

        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("newPhone", newPhone)
                .build();

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/changephone")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(() -> Toast.makeText(getContext(), "手机号更新失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                SendBean<Boolean> result = gson.fromJson(json, new TypeToken<SendBean<Boolean>>(){}.getType());
                mHandler.post(() -> {
                    if (result.getStatus().equals("ok") && result.getData()) {
                        Toast.makeText(getContext(), "手机号更新成功", Toast.LENGTH_SHORT).show();
                        updateLocalUserInfo(null, newPhone);
                        updateView(); // 更新视图
                        performLogout();
                    } else {
                        Toast.makeText(getContext(), "手机号更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void showChangePasswordDialog() {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_change_password, null);
        EditText etOldPassword = dialogView.findViewById(R.id.et_old_password);
        EditText etNewPassword = dialogView.findViewById(R.id.et_new_password);
        etNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (!Utils.isPasswordValid(editable.toString())) {
                    etNewPassword.setError(getString(R.string.invalid_password));
                }
            }
        });
        new AlertDialog.Builder(getContext())
                .setTitle("修改密码")
                .setView(dialogView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String oldPassword = etOldPassword.getText().toString();
                        String newPassword = etNewPassword.getText().toString();
                        changePassword(oldPassword, newPassword);
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();
    }
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                imageViewAvatar.setImageBitmap(bitmap);
                uploadImageToServer(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImageToServer(Bitmap bitmap) {
        String imageBase64 = convertBitmapToBase64(bitmap);
        OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
        Gson gson = new Gson();

        String userId = getCurrentUserId();

        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("avatarBase64", imageBase64)
                .build();

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/changeavatar")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(() -> Toast.makeText(getContext(), "头像上传失败", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                SendBean<Boolean> result = gson.fromJson(json, new TypeToken<SendBean<Boolean>>(){}.getType());
                mHandler.post(() -> {
                    if (result.getStatus().equals("ok") && result.getData()) {
                        Toast.makeText(getContext(), "头像更新成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "头像更新失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private Bitmap decodeBase64ToBitmap(String base64Str) {
        byte[] decodedBytes = Base64.decode(base64Str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private void changePassword(String oldPassword, String newPassword) {
        OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
        Gson gson = new Gson();

        String userId = getCurrentUserId();
        RequestBody requestBody = new FormBody.Builder()
                .add("userId", userId)
                .add("oldPassword", oldPassword)
                .add("newPassword", newPassword)
                .build();

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/changepassword")
                .post(requestBody)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(() -> Toast.makeText(getContext(), "旧密码输入有误", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                SendBean<Boolean> result = gson.fromJson(json, new TypeToken<SendBean<Boolean>>(){}.getType());
                mHandler.post(() -> {
                    if (result.getStatus().equals("ok") && result.getData()) {
                        Toast.makeText(getContext(), "密码更改成功", Toast.LENGTH_SHORT).show();
                        // 密码更改成功后，执行注销操作并跳转到登录界面
                        performLogout();
                    } else {
                        Toast.makeText(getContext(), "旧密码输入有误", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void performLogout() {
        new Thread(() -> {
            if (LoginRepository.getInstance().logout(getActivity())) {
                mHandler.post(() -> {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    Activity activity = getActivity();
                    if (activity != null) {
                        activity.finish();
                    }
                });
            } else {
                mHandler.post(() -> Toast.makeText(getActivity(), "注销失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private String getCurrentUserId() {
        UserInfoBean currentUser = DataShare.getUser();
        if (currentUser != null) {
            return String.valueOf(currentUser.getId());
        } else {
            // 用户未登录或信息不可用
            return null;
        }
    }

    private void addBalance(String balance) {
        if (TextUtils.isEmpty(balance))
            return;
        OkHttpClient okHttpClient = HttpUtils.getOkHttpClient();
        Gson gson = new Gson();

        Request request = new Request.Builder()
                .url(HttpUtils.BASE_URL + "/addbalance?money=" + balance)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String json = response.body().string();
                Log.d("update", json);
                if (!json.isEmpty()) {
                    SendBean<Boolean> result = gson.fromJson(json
                            , new TypeToken<SendBean<Boolean>>() {
                            }.getType());
                    if (result.getStatus().equals("ok")) {
                        if (result.getData()) {
                            if(HttpUtils.updateUserInfoFromInternet())
                            {
                                updateView();
                            }
                        } else {
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "充值失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    }
                }
            }

        });
    }

    private void updateView() {
        mUserViewModel.setUser(DataShare.getUser());
    }
    private void updateLocalUserInfo(String newNickname, String newPhone) {
        UserInfoBean currentUser = DataShare.getUser();
        if (currentUser != null) {
            if (newNickname != null) {
                currentUser.setNickName(newNickname);
            }
            if (newPhone != null) {
                currentUser.setPhone(newPhone);
            }
            DataShare.setUser(currentUser);
        }
    }

    public static String generateOrderNumber() {
        // 使用当前时间戳生成订单号的一部分
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String timestamp = dateFormat.format(new Date());

        // 使用随机数生成订单号的一部分
        Random random = new Random();
        int randomInt = random.nextInt(1000); // 生成一个三位数的随机数

        // 拼接时间戳和随机数
        String orderNumber =  "#" + String.format("%03d", randomInt) + timestamp;

        return orderNumber;
    }

    public static String getTime(){
        Date currentTime = new Date();
        // 使用SimpleDateFormat格式化时间
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedTime = dateFormat.format(currentTime);
        return formattedTime;
    }


}
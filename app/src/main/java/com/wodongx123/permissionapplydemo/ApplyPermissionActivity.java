package com.wodongx123.permissionapplydemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * @description： 权限申请Activity
 */
public class ApplyPermissionActivity extends AppCompatActivity {

    // intent的KEY值
    private static final String PERMISSIONS = "permissions";
    // 回调的接口
    private static PermissionRequestCallback mCallback;
    // 请求码
    private static int REQUESTCODE = 1;

    private String[] permissionArr;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionArr = getIntent().getStringArrayExtra(PERMISSIONS);

        // 防报错编码，有问题就return
        if (permissionArr == null || mCallback == null){
            finish();
            return;
        }

        // 如果权限全部申请过了，就不再申请，直接返回申请成功
        if (hasPermissionRequest(this, permissionArr)) {
            mCallback.permissionSuccess();
            finish();
            return;
        }

        // 申请权限
        ActivityCompat.requestPermissions(this, permissionArr, REQUESTCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 进行权限回调的处理

        // 权限全部申请成功的回调
        if (hasPermissionRequest(this, permissions)) {
            mCallback.permissionSuccess();
            finish();
            return;
        }

        // 有至少一个权限被永久拒绝了
        if (shouldShowRequestPermissionRationale(this, permissions)) {
            mCallback.permissionNoRequest();
            finish();
            return;
        }

        // 用户拒绝了权限
        mCallback.permissionDeny();
        finish();

    }


    /**
     * 判断所有权限是否已经申请成功，如果有至少一个没有申请成功，返回false
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissionRequest(Context context, String... permissions){
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED)
                return false;
        }
        return true;
    }


    /**
     * 判断权限中是否有被永久拒绝（不再询问）的权限，如果至少有一个被永久拒绝的权限，返回true
     * @param context
     * @param permissions
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity context, String... permissions){
        for (String permission : permissions) {
            // 当一个权限被不再询问之后，if中的内容会返回false
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission))
                return true;
        }
        return false;
    }




    /**
     * 打开权限申请Activity
     * @param context 上下文
     * @param permissions 权限
     * @param permissionRequestCallback
     */
    public static void startActivity(Context context, String[] permissions, PermissionRequestCallback permissionRequestCallback){
        mCallback = permissionRequestCallback;
        Intent intent = new Intent(context, ApplyPermissionActivity.class);
        intent.putExtra(PERMISSIONS, permissions);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}

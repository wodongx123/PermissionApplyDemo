package com.wodongx123.permissionapplydemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();
    }

    @Permission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    private void test() {
        // 假设要执行一个需要读写权限的方法。
        Toast.makeText(this, "权限申请成功", Toast.LENGTH_SHORT).show();
    }

    @PermissionDeny
    private void permissionDeny(){
        Log.i(TAG, "permissionDeny: ");
        Toast.makeText(this, "权限被拒绝", Toast.LENGTH_SHORT).show();
    }

    @PermissionNoRequest
    private void permissionNoRequest(){
        Log.i(TAG, "permissionNoRequest: ");
        Toast.makeText(this, "权限被拒绝，且点击了不再访问", Toast.LENGTH_SHORT).show();
    }
}
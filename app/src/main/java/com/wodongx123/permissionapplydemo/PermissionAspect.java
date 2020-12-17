package com.wodongx123.permissionapplydemo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import java.lang.reflect.Method;


@Aspect
public class PermissionAspect {

    private static final String TAG = "PermissionAspect";

    // 通过annotation，声明需要将注解所带的参数传入到方法中
    // annotation括号中写的变量名，和方法的参数的变量名必须一致，而方法的类型名需要和注解的类型一致
    @Pointcut("execution(@Permission * * (..)) && @annotation(cc)")
    public void getPermission(Permission cc){}

    @Around("getPermission(cc)")
    public void getPermissionJoinPoint(final ProceedingJoinPoint proceedingJoinPoint, Permission cc){

        Log.i(TAG, "getPermissionJoinPoint: " + proceedingJoinPoint + "   " + cc);

        // 检查方法所在的类是否包括Context。
        Object o = proceedingJoinPoint.getThis();
        Context context = null;
        if (o instanceof Context)
            context = (Context) o;
        else if (o instanceof Fragment)
            context = ((Fragment) o).getContext();

        if (context == null || cc == null) return;

        // 打开权限申请Activity
        ApplyPermissionActivity.startActivity(context, cc.value(), new PermissionRequestCallback() {
            @Override
            public void permissionSuccess() {
                // 权限申请成功时的回调，不做任何操作，继续执行原方法
                try {
                    proceedingJoinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void permissionDeny() {
                // 权限申请失败时的回调
                invokeAnnotation(proceedingJoinPoint.getThis(), PermissionDeny.class);
            }

            @Override
            public void permissionNoRequest() {
                // 权限申请失败且不再询问时的回调
                invokeAnnotation(proceedingJoinPoint.getThis(), PermissionNoRequest.class);
            }
        });
    }


    /**
     * 在o这个类中寻找被annotationclass注解标记的方法并且执行
     * @param o
     * @param annotationClass
     */
    public static void invokeAnnotation(Object o, Class annotationClass){
        Class<?> objectClass = o.getClass();
        Log.e(TAG, "invokeAnnotation: " + objectClass);

        // 遍历这个类（不包含父类）所有的方法
        Method[] methods = objectClass.getDeclaredMethods();
        for (Method method : methods) {
            // 忽视private
            method.setAccessible(true);


            // 检查当前所在的方法是否被注解标记
            boolean annotationPresent = method.isAnnotationPresent(annotationClass);

            Log.e(TAG, "invokeAnnotation: " + method + "     " + annotationPresent );

            // 有注解就执行
            if (annotationPresent)
                try{
                    method.invoke(o);
                }catch (Exception e){
                    e.printStackTrace();
                }
        }
    }
}

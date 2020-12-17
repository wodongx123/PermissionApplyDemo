package com.wodongx123.permissionapplydemo;


public interface PermissionRequestCallback {

    /**
     * 申请成功和已经申请过
     */
    void permissionSuccess();

    /**
     * 申请拒绝
     */
    void permissionDeny();

    /**
     * 申请拒绝且不再询问
     */
    void permissionNoRequest();
}

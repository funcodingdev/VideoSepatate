package com.hasee.videosepatate;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Android M Permission (Android 6.0权限控制代码封装)
 */
public class BaseActivity extends Activity {
    //**************** Android M Permission (Android 6.0权限控制代码封装)
    private int permissionRequestCode = 88;
    private PermissionCallback permissionRunnable;

    /**
     * 权限回调 接口
     */
    public interface PermissionCallback {
        /**
         * 有权限 时的操作
         */
        void hasPermission();
        /**
         * 没有权限 时的操作
         */
        void noPermission();
    }

    /**
     * Android M运行时权限请求封装
     *
     * @param permissionDes 权限描述, 字符串
     * @param runnable      请求权限回调
     * @param permissions   请求的权限（数组类型），直接从Manifest中读取相应的值，比如Manifest.permission.WRITE_CONTACTS
     */
    public void performCodeWithPermission(@NonNull String permissionDes, PermissionCallback runnable, @NonNull String... permissions) {
        if (permissions == null || permissions.length == 0)
            return;
        //        this.permissionrequestCode = requestCode;
        this.permissionRunnable = runnable;
        // 判断 系统版本, 是否小于安卓6.0
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || checkPermissionGranted(permissions)) {
            if (permissionRunnable != null) {
                permissionRunnable.hasPermission();
                permissionRunnable = null;
            }
        } else {
            //permission has not been granted.
            requestPermission(permissionDes, permissionRequestCode, permissions);
        }

    }


    /**
     * 检查是否授权, 所一个权限没有授权, 则为false
     */
    private boolean checkPermissionGranted(String[] permissions) {
        boolean flag = true;
        for (String p : permissions) {
            // 判断权限是否被复制
            if (ActivityCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
                break;
            }
        }
        return flag;
    }

    /**
     * @param permissionDes 权限的权限字符串
     * @param requestCode 权限的请求码
     * @param permissions 权限的数组
     */
    private void requestPermission(String permissionDes, final int requestCode, final String[] permissions) {
        if (shouldShowRequestPermissionRationale(permissions)) {
            /*1. 第一次请求权限时，用户拒绝了，下一次：shouldShowRequestPermissionRationale()  返回 true，应该显示一些为什么需要这个权限的说明
            2.第二次请求权限时，用户拒绝了，并选择了“不在提醒”的选项时：shouldShowRequestPermissionRationale()  返回 false
            3. 设备的策略禁止当前应用获取这个权限的授权：shouldShowRequestPermissionRationale()  返回 false*/
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.

            //            Snackbar.make(getWindow().getDecorView(), requestName,
            //                    Snackbar.LENGTH_INDEFINITE)
            //                    .setAction(R.string.common_ok, new View.OnClickListener() {
            //                        @Override
            //                        public void onClick(View view) {
            //                            ActivityCompat.requestPermissions(BaseAppCompatActivity.this,
            //                                    permissions,
            //                                    requestCode);
            //                        }
            //                    })
            //                    .show();
            //如果用户之前拒绝过此权限，再提示一次准备授权相关权限
            new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(permissionDes)
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
                        }
                    }).show();

        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(BaseActivity.this, permissions, requestCode);
        }
    }

    /**
     * 权限 判断
     * @param permissions
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(String[] permissions) {
        boolean flag = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, p)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 当权限请求已完成时收到回调。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == permissionRequestCode) {
            if (verifyPermissions(grantResults)) {
                if (permissionRunnable != null) {
                    permissionRunnable.hasPermission();
                    permissionRunnable = null;
                }
            } else {
                Toast.makeText(this, "暂无权限执行相关操作！", Toast.LENGTH_SHORT).show();
                if (permissionRunnable != null) {
                    permissionRunnable.noPermission();
                    permissionRunnable = null;
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    /**
     * 判断 用户是否授权
     * @param grantResults
     * @return
     */
    public boolean verifyPermissions(int[] grantResults) {
        // 至少必须检查一个结果。
        if (grantResults.length < 1) {
            return false;
        }

        // 验证每个必需的权限已被授予，否则返回false。
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    //********************** END Android M Permission ****************************************
}

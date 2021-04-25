package com.stupidtree.style.picker

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stupidtree.style.picker.GalleryPicker.RC_CHOOSE_PHOTO

object PermissionUtils {

    /**
     * 申请外部权限
     */
    fun grantExternalStoragePermissions(mContext: Activity) {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // 未授权，申请授权(从相册选择图片需要读取存储卡的权限)
            ActivityCompat.requestPermissions(mContext, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    RC_CHOOSE_PHOTO)
            return
        }
    }

    /**
     * 申请相机权限
     */
    fun grantCameraPermission(activity: Activity) {
        //动态申请相机权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
        }
    }


    private const val GET_RECODE_AUDIO = 1
    private val PERMISSION_AUDIO = arrayOf(
            Manifest.permission.RECORD_AUDIO
    )

    /**
     * 申请录音权限
     */
    fun grantAudioPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            val permissions = arrayOf(Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
            //验证是否许可权限
            for (str in permissions) {
                if (activity.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    activity.requestPermissions(permissions, 101)
                    return
                }
            }
        }
        val permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSION_AUDIO,
                    GET_RECODE_AUDIO)
        }
    }
}
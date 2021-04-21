package com.stupidtree.style.picker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.stupidtree.style.picker.FileProviderUtils.getUriForFile
import com.stupidtree.style.picker.FileProviderUtils.setIntentDataAndType
import java.io.File


/**
 * 此类封装了跳转到系统相册选取、裁剪图片的过程
 * 不必关心
 */
object GalleryPicker {

    /**
     * 这些是调用系统相册选择、裁剪图片要用到的状态码
     */
    const val RC_CHOOSE_PHOTO = 10
    const val RC_TAKE_PHOTO = 11
    const val RC_CROP_PHOTO = 12

    fun getPhotoFile(context: Context):File{
        return File(context.externalCacheDir, "photo.jpg")
    }
    /**
     * 打开相机
     */
    fun takePhoto(mContext: Activity) {
        if ((ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)) {
            // 未授权，申请授权
            ActivityCompat.requestPermissions(mContext, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA),
                    RC_TAKE_PHOTO)
            return
        }
        // 已授权
        val intentToTakePhoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // 设置照片输出位置

        //  tempPhotoPath = photoFile.getAbsolutePath();
        val tempImgUri = getUriForFile(mContext, getPhotoFile(mContext))
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
        mContext.startActivityForResult(intentToTakePhoto, RC_TAKE_PHOTO)
    }

    /**
     * 拍照输出真实路径
     */
    var tempPhotoPath: String? = null

    /**
     * 选图
     */
    fun choosePhoto(mContext: Activity, multiple: Boolean) {
        PermissionUtils.grantExternalStoragePermissions(mContext)
        // 已授权，获取照片
        val intentToPickPic = Intent(Intent.ACTION_PICK, null)
        if (multiple) intentToPickPic.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        mContext.startActivityForResult(intentToPickPic, RC_CHOOSE_PHOTO)
    }

    fun getCroppedCacheDir(context: Context): Uri? {
        return Uri.parse("file:///" + context.getExternalFilesDir(null) + "/cropped.jpg")
    }

    /**
     * 剪裁图片
     */
    fun cropPhoto(mContext: Activity, path: String?, size: Int) {

        if (path == null || getCroppedCacheDir(mContext) == null) {
            return
        }
        val intent = Intent("com.android.camera.action.CROP")
        setIntentDataAndType(mContext, intent, File(path))
        intent.putExtra("crop", "true")
        intent.putExtra("aspectX", 1)
        intent.putExtra("aspectY", 1)
        intent.putExtra("outputX", size)
        intent.putExtra("outputY", size)
        intent.putExtra("scale", true)
        intent.putExtra("return-data", false)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getCroppedCacheDir(mContext))
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        mContext.startActivityForResult(intent, RC_CROP_PHOTO)
    }
}

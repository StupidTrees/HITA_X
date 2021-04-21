package com.stupidtree.style.picker

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException

/**
 * 此类用于适配Android7.0以下/以上的文件权限
 * 不必关心
 */
object FileProviderUtils {
    /**
     * 获取文件的Uri
     */
    @JvmStatic
    fun getUriForFile(context: Context, file: File): Uri {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            getUriForFile24(context, file)
        } else {
            Uri.fromFile(file)
        }
    }

    /**
     * Android 7 获取文件的Uri
     */
    private fun getUriForFile24(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(context,
                "com.stupidtree.hitax.fileprovider", file)
    }

    @JvmStatic
    fun setIntentDataAndType(context: Context,
                             intent: Intent,
                             file: File) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setDataAndType(getUriForFile(context, file), "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        } else {
            intent.setDataAndType(Uri.fromFile(file), "image/*")
            // apk放在cache文件中，需要获取读写权限
            chmod(file.absolutePath)
        }
    }

    /**
     * 修改文件权限
     */
    private fun chmod(path: String) {
        try {
            val command = "chmod 777 $path"
            val runtime = Runtime.getRuntime()
            runtime.exec(command)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * 根据URI获取文件真实路径（兼容多机型）
     */
    fun getFilePathByUri(context: Context, uri: Uri): String? {
        // 判断uri的标头是 content 还是 file，分别用不同的方法处理
        if ("content".equals(uri.scheme, ignoreCase = true)) {
            val sdkVersion = Build.VERSION.SDK_INT
            return if (sdkVersion >= 19) {
                // api >= 19
                getRealPathFromUriAboveApi19(context, uri)
            } else {
                // api < 19
                getRealPathFromUriBelowAPI19(context, uri)
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * 适配api19及以上,根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    @SuppressLint("NewApi")
    private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            val documentId = DocumentsContract.getDocumentId(uri)
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                val type = documentId.split(":".toRegex()).toTypedArray()[0]
                val id = documentId.split(":".toRegex()).toTypedArray()[1]
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(id)

                // 判断文件类型
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                filePath = getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isDownloadsDocument(uri)) {
                // DownloadsProvider
                val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), documentId.toLong())
                filePath = getDataColumn(context, contentUri, null, null)
            } else if (isExternalStorageDocument(uri)) {
                // ExternalStorageProvider
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    filePath = context.getExternalFilesDir(null).toString() + "/" + split[1]
                }
            } else {
                Log.e("FileHandlerUtil", "路径错误")
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file" == uri.scheme) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.path
        }
        return filePath
    }

    /**
     * 适配api19以下(不包括api19),根据uri获取图片的绝对路径
     *
     * @param context 上下文对象
     * @param uri     图片的Uri
     * @return 如果Uri对应的图片存在, 那么返回该图片的绝对路径, 否则返回null
     */
    private fun getRealPathFromUriBelowAPI19(context: Context, uri: Uri): String? {
        return getDataColumn(context, uri, null, null)
    }

    /**
     * 获取数据库表中的 _data 列，即返回Uri对应的文件路径
     */
    private fun getDataColumn(context: Context, uri: Uri?, selection: String?, selectionArgs: Array<String>?): String? {
        var path: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        var cursor: Cursor? = null
        try {
            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            cursor?.close()
        }
        return path
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is MediaProvider
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri the Uri to check
     * @return Whether the Uri authority is DownloadsProvider
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }
}
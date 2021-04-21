package com.stupidtree.style

import androidx.core.content.FileProvider

/**
 * 不加这个，Android7以上无法调用系统相册选图片
 */
class MFileProvider : FileProvider() 
package com.stupidtree.component.web

/**
 * 封装了服务器返回格式的类
 * @param <TextRecord> 返回数据的类型
</TextRecord> */
class ApiResponse<T>(//返回状态码
        var code: Int, //返回message
        var message: String, //返回数据
        var data: T?) {

    override fun toString(): String {
        return "ApiResponse{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}'
    }

}
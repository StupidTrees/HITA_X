package com.stupidtree.sync.util

import java.lang.RuntimeException

/**
 * Twitter_Snowflake<br></br>
 * SnowFlake的结构如下(每部分用-分开):<br></br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br></br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br></br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 - 开始时间截)
 * 得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br></br>
 * 10位的数据机器位，可以部署在1024个节点，包括5位datacenterId和5位workerId<br></br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4096个ID序号<br></br>
 * 加起来刚好64位，为一个Long型。<br></br>
 * SnowFlake的优点是，整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高，经测试，SnowFlake每秒能够产生26万ID左右。
 */
class Snowflake(workerId: Long, datacenterId: Long) {
    // ==============================Fields===========================================
    /** 开始时间截 (2015-01-01)  */
    private val twepoch = 1420041600000L

    /** 机器id所占的位数  */
    private val workerIdBits = 5L

    /** 数据标识id所占的位数  */
    private val datacenterIdBits = 5L

    /** 支持的最大机器id，结果是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数)  */
    private val maxWorkerId = -1L xor (-1L shl workerIdBits.toInt())

    /** 支持的最大数据标识id，结果是31  */
    private val maxDatacenterId = -1L xor (-1L shl datacenterIdBits.toInt())

    /** 序列在id中占的位数  */
    private val sequenceBits = 12L

    /** 机器ID向左移12位  */
    private val workerIdShift = sequenceBits

    /** 数据标识id向左移17位(12+5)  */
    private val datacenterIdShift = sequenceBits + workerIdBits

    /** 时间截向左移22位(5+5+12)  */
    private val timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits

    /** 生成序列的掩码，这里为4095 (0b111111111111=0xfff=4095)  */
    private val sequenceMask = -1L xor (-1L shl sequenceBits.toInt())

    /** 工作机器ID(0~31)  */
    private val workerId: Long

    /** 数据中心ID(0~31)  */
    private val datacenterId: Long

    /** 毫秒内序列(0~4095)  */
    private var sequence = 0L

    /** 上次生成ID的时间截  */
    private var lastTimestamp = -1L
    // ==============================Methods==========================================
    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    @Synchronized
    fun nextId(): Long {
        var timestamp = timeGen()

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw RuntimeException(
                String.format(
                    "Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp
                )
            )
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = sequence + 1 and sequenceMask
            //毫秒内序列溢出
            if (sequence == 0L) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp)
            }
        } else {
            sequence = 0L
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp

        //移位并通过或运算拼到一起组成64位的ID
        return (timestamp - twepoch shl timestampLeftShift.toInt() //
                or (datacenterId shl datacenterIdShift.toInt()) //
                or (workerId shl workerIdShift.toInt()) //
                or sequence)
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected fun tilNextMillis(lastTimestamp: Long): Long {
        var timestamp = timeGen()
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen()
        }
        return timestamp
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected fun timeGen(): Long {
        return System.currentTimeMillis()
    }

    companion object {
        //==============================Test=============================================
        /** 测试  */
        @JvmStatic
        fun main(args: Array<String>) {
            val idWorker = Snowflake(0, 0)
            for (i in 0..999) {
                val id = idWorker.nextId()
                println(java.lang.Long.toBinaryString(id))
                println(id)
            }
        }
    }
    //==============================Constructors=====================================
    /**
     * 构造函数
     * @param workerId 工作ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    init {
        require(!(workerId > maxWorkerId || workerId < 0)) {
            String.format(
                "worker Id can't be greater than %d or less than 0",
                maxWorkerId
            )
        }
        require(!(datacenterId > maxDatacenterId || datacenterId < 0)) {
            String.format(
                "datacenter Id can't be greater than %d or less than 0",
                maxDatacenterId
            )
        }
        this.workerId = workerId
        this.datacenterId = datacenterId
    }
}
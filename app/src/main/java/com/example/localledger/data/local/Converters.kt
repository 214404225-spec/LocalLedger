package com.example.localledger.data.local

import androidx.room.TypeConverter
import java.math.BigDecimal

/**
 * Room 类型转换器，用于支持 BigDecimal 的持久化
 * - 将 BigDecimal 转为 String 存入 SQLite
 * - 从 String 安全解析回 BigDecimal
 */
class Converters {

    @TypeConverter
    fun fromBigDecimal(value: BigDecimal?): String? {
        return value?.toPlainString() // 使用 toPlainString() 避免科学计数法
    }

    @TypeConverter
    fun toBigDecimal(value: String?): BigDecimal? {
        if (value == null) return null
        return try {
            BigDecimal(value)
        } catch (e: NumberFormatException) {
            // 如果解析失败（如空字符串、非法格式），返回 null 而非崩溃
            null
        }
    }
}
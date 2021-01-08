package com.jspbb.util.web

import org.thymeleaf.context.IEngineContext
import org.thymeleaf.context.ITemplateContext
import java.math.BigDecimal
import java.math.BigInteger
import java.time.*
import java.util.*


object ThymeleafUtils {
    fun getInt(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): Int? = context.getVariableLocal(name, isVariableLocal)?.let { getInt(it) }
    fun getInt(value: Any): Int = when (value) {
        is Int -> value
        is Number -> value.toInt()
        is String -> value.toInt()
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to Int")
    }

    fun getInts(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<Int>? = context.getVariableLocal(name, isVariableLocal)?.let { getInts(it) }
    fun getInts(value: Any): List<Int> = when (value) {
        is Int -> listOf(value)
        is Number -> listOf(value.toInt())
        is String -> value.split(",").fold(ArrayList()) { list, v -> list.add(v.toInt());list }
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(getInt(v)); list }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to List<Int>")
    }

    fun getLong(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): Long? = context.getVariableLocal(name, isVariableLocal)?.let { getLong(it) }
    fun getLong(value: Any): Long = when (value) {
        is Long -> value
        is Number -> value.toLong()
        is String -> value.toLong()
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to Long")
    }

    fun getLongs(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<Long>? = context.getVariableLocal(name, isVariableLocal)?.let { getLongs(it) }
    fun getLongs(value: Any): List<Long> = when (value) {
        is Long -> listOf(value)
        is Number -> listOf(value.toLong())
        is String -> value.split(",").fold(ArrayList()) { list, v -> list.add(v.toLong());list }
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(getLong(v)); list }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to List<Long>")
    }

    fun getDouble(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): Double? = context.getVariableLocal(name, isVariableLocal)?.let { getDouble(it) }
    fun getDouble(value: Any): Double = when (value) {
        is Double -> value
        is Number -> value.toDouble()
        is String -> value.toDouble()
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to Double")
    }

    fun getDoubles(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<Double>? = context.getVariableLocal(name, isVariableLocal)?.let { getDoubles(it) }
    fun getDoubles(value: Any): List<Double> = when (value) {
        is Double -> listOf(value)
        is Number -> listOf(value.toDouble())
        is String -> value.split(",").fold(ArrayList()) { list, v -> list.add(v.toDouble());list }
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(getDouble(v)); list }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to List<Double>")
    }

    fun getBigInteger(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): BigInteger? = context.getVariableLocal(name, isVariableLocal)?.let { getBigInteger(it) }
    fun getBigInteger(value: Any): BigInteger = when (value) {
        is BigInteger -> value
        is Number -> BigInteger.valueOf(value.toLong())
        is String -> BigInteger(value)
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to BigInteger")
    }

    fun getBigIntegers(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<BigInteger>? = context.getVariableLocal(name, isVariableLocal)?.let { getBigIntegers(it) }
    fun getBigIntegers(value: Any): List<BigInteger> = when (value) {
        is BigInteger -> listOf(value)
        is Number -> listOf(BigInteger.valueOf(value.toLong()))
        is String -> value.split(",").fold(ArrayList()) { list, v -> list.add(BigInteger(v));list }
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(getBigInteger(v)); list }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to List<BigInteger>")
    }

    fun getBigDecimal(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): BigDecimal? = context.getVariableLocal(name, isVariableLocal)?.let { getBigDecimal(it) }
    fun getBigDecimal(value: Any): BigDecimal = when (value) {
        is BigDecimal -> value
        is Float -> BigDecimal(value.toDouble())
        is Double -> BigDecimal(value)
        is Number -> BigDecimal(value.toLong())
        is String -> BigDecimal(value)
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to BigDecimal")
    }

    fun getBigDecimals(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<BigDecimal>? = context.getVariableLocal(name, isVariableLocal)?.let { getBigDecimals(it) }
    fun getBigDecimals(value: Any): List<BigDecimal> = when (value) {
        is BigDecimal -> listOf(value)
        is Float -> listOf(BigDecimal(value.toDouble()))
        is Double -> listOf(BigDecimal(value))
        is Number -> listOf(BigDecimal(value.toLong()))
        is String -> value.split(",").fold(ArrayList()) { list, v -> list.add(BigDecimal(v));list }
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(getBigDecimal(v)); list }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to List<BigDecimal>")
    }

    fun getString(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): String? = context.getVariableLocal(name, isVariableLocal)?.toString()

    fun getStrings(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): List<String>? = context.getVariableLocal(name, isVariableLocal)?.toString()?.split(",")
    fun getStrings(value: Any) = when (value) {
        is Collection<*> -> value.fold(ArrayList()) { list, v -> if (v != null) list.add(v.toString()); list }
        else -> value.toString().split(",")
    }

    fun getDate(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): OffsetDateTime? = context.getVariableLocal(name, isVariableLocal)?.let { getDate(it) }
    fun getDate(value: Any): OffsetDateTime = when (value) {
        is OffsetDateTime -> value
        is ZonedDateTime -> value.toOffsetDateTime()
        is Instant -> OffsetDateTime.ofInstant(value, ZoneId.systemDefault())
        is LocalDateTime -> value.atZone(ZoneId.systemDefault()).toOffsetDateTime()
        is LocalDate -> value.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
        is Date -> OffsetDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
        is String -> Dates.parseOffsetDateTime(value)
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to Date")
    }

    fun getBoolean(context: ITemplateContext, name: String, isVariableLocal: Boolean = true): Boolean? = context.getVariableLocal(name, isVariableLocal)?.let { getBoolean(it) }
    fun getBoolean(value: Any): Boolean = when (value) {
        is Boolean -> value
        is String -> when (value) {
            "true" -> true
            "false" -> false
            else -> throw RuntimeException("cannot convert '$value' to Boolean")
        }
        is Number -> when (value) {
            0 -> false
            else -> true
        }
        else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to Boolean")
    }

    fun getPage(context: ITemplateContext): Int = getInt(context, "page", false) ?: 1
    fun getPageSize(context: ITemplateContext): Int = getInt(context, "pageSize") ?: 20
    fun getPageSize(context: ITemplateContext, defaultValue: Int): Int = getInt(context, "pageSize") ?: defaultValue
    fun getOffset(context: ITemplateContext): Int = getInt(context, "offset") ?: 0
    fun getLimit(context: ITemplateContext): Int? = getInt(context, "limit")
    fun pageable(context: ITemplateContext): Boolean = context.getVariableLocal("pageable") != null

    private fun ITemplateContext.getVariableLocal(name: String, isVariableLocal: Boolean = true): Any? {
        return if (isVariableLocal) {
            if (this !is IEngineContext || this.isVariableLocal(name)) {
                this.getVariable(name)
            } else null
        } else {
            this.getVariable(name)
        }
    }
}
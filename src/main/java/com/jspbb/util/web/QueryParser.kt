package com.jspbb.util.web

import com.jspbb.util.web.QueryParser.Companion.preventInjection
import com.jspbb.util.web.Servlets.parseQueryString
import org.apache.commons.lang3.StringUtils
import org.thymeleaf.context.ITemplateContext
import java.time.*
import java.util.*
import java.util.regex.Pattern

/**
 * 查询解析器
 *
 * 表、主键、外键命名的规则
 * <li>独立主键全部用`id_`作为主键（包括one-to-one表）
 * <li>使用外键关联表名加ID作为外键，如`user_id_` `usergroup_id_`
 * <li>特殊情况使用表意名称加ID作为外键，如`edit_user_id_`
 *
 * 关联方式
 * <li>many-to-one(EQ_user-username)：被关联表表名作为leftId
 * <li>特殊many-to-one(EQ_editUser__user-username)：被关联表表名作为leftId
 * <li>one-to-one(EQ_Mext-username)：被关联表表名作为leftId
 * <li>one-to-many(EQ_Mext-username)：被关联表表名作为leftId
 *
 * 比较方式（下划线前缀）：EQ_userId、Contain_title
 * 表关联（中划线）：EQ_user-username
 * 表关联别名（双下划线）：EQ_editorUser__user-username
 * 类型（下划线后缀）：EQ_userId_Int
 * one-to-many（M标识）：EQ_user-Mext-username
 *
 * <li>OrderBy=t.publishDate desc
 * <li>Distinct=true
 * <li>EQ_userId=12
 * <li>Contain_title=foo
 * <li>EQ_price_Int=10
 */
class QueryParser(
        private val params: Map<String, Any> = emptyMap(),
        private val defaultOrderBy: String? = null,
        private val excludeTableAlias: List<String> = emptyList(),
        private val tablePrefix: String = "jspbb_"
) {
    private val whereConditions = mutableListOf<WhereCondition>()
    private val whereOrConditions = mutableMapOf<Char, MutableList<WhereCondition>>()

    init {
        // Like_name
        // Like_user-username
        // EQ_price_Int
        // Like_1questionExt-title
        // Like_1questionExt-markdown
        for ((key, v) in params) {
            if (key == DISTINCT || key == ORDER_BY) continue
            // 将双下划线替换为@作为表名标识
            val parts = key.replace("__", "@").split("_")
            if (parts.size < 2) throw RuntimeException("query parts not valid: '$key'. e.g. 'Like_name' 'Like_user-userExt-username'")
            val operatorPart = parts[0]
            val tableColumnPart = parts[1]
            val typePart = if (parts.size > 2) parts[2] else TYPE_STRING
            // 为 null 则忽略
            val value: Any = getValue(typePart, v, operatorPart) ?: continue
            // 分组。如：Like_1questionExt-title Like_1questionExt-markdown
            val group = tableColumnPart.first()
            if (group.isDigit()) {
                // 数字则分组
                val column: String = preventInjection(getColumn(tableColumnPart.substring(1)))
                if (whereOrConditions[group] == null) whereOrConditions[group] = mutableListOf()
                whereOrConditions[group]?.add(WhereCondition(column, getOperator(operatorPart), value))
            } else {
                // 非数字不分组
                val column: String = preventInjection(getColumn(tableColumnPart))
                whereConditions.add(WhereCondition(column, getOperator(operatorPart), value))
            }
        }
    }

    /**
     * 支持Like, Contain, In, NotIn, IsNull, EQ, NE, GT, LT, GE, LE
     */
    fun getWhereConditions() = whereConditions

    fun getWhereOrConditions() = whereOrConditions

    fun getDistinct(): Boolean = params.keys.any { it == DISTINCT }

    /**
     * 支持多个 OrderBy 参数
     */
    fun getOrderBy(): String? {
        val buff = StringBuilder()
        val orderBy = params[ORDER_BY] ?: defaultOrderBy ?: return null
        for (value in ThymeleafUtils.getStrings(orderBy)) {
            // username
            // username_desc
            // user-username
            // ext__userExt-lastLoginDate
            // editUser__user-ext__userExt-lastLoginDate_desc => [editUser@user-ext@userExt-lastLoginDate, desc]
            val parts = value.replace("__", "@").split("_")
            // editUser@user-ext@userExt-lastLoginDate => ext.last_login_date_
            // name => t.name_
            val column = getColumn(parts[0])
            // 排序方式前加空格 ` desc`
            val direction = if (parts.size > 1) " ${parts[1]}" else ""
            buff.append(preventInjection("$column$direction", true)).append(',')
        }
        return if (buff.isNotBlank()) buff.substring(0, buff.length - 1) else null
    }

    fun getJoinTables(): List<JoinTable> {
        val tableColumnParts = mutableListOf<String>()
        for (key in params.keys) {
            // Like_name
            // Like_user-username
            // Like_user-userExt-username
            // Like_questionExt-title
            // EQ_price_Int
            // Like_editUser__user-ext-username
            // Like_1questionExt-title
            // Like_1questionExt-markdown
            if (key == DISTINCT || key == ORDER_BY) continue
            // 将双下划线替换为@作为表名标识
            // Like_editUser@user-userExt-username => [Like, editUser@user-userExt-username]
            val parts = key.replace("__", "@").split("_")
            if (parts.size < 2) throw RuntimeException("query parts not valid: '$key'. e.g. 'Like_name' 'Like_user-userExt-username'")
            // parts[0] = Like 无需处理
            // parts[1] = editUser@user-userExt-username
            tableColumnParts.add(parts[1])
        }
        (params[ORDER_BY] ?: defaultOrderBy)?.let { orderBy ->
            for (value in ThymeleafUtils.getStrings(orderBy)) {
                if (value.isBlank()) continue
                // name
                // name_desc
                // user-username
                // ext__userExt-lastLoginDate
                // editUser__user-ext__userExt-lastLoginDate_desc => [editUser@user-ext@userExt-lastLoginDate, desc]
                val parts = value.replace("__", "@").split("_")
                // parts[0] = editUser@user-ext@userExt-lastLoginDate
                // parts[1] = desc 无需处理
                tableColumnParts.add(parts[0])
            }
        }
        return parseJoinTable(tableColumnParts)
    }

    private fun parseJoinTable(tableColumnParts: List<String>): List<JoinTable> {
        val joinTables = mutableListOf<JoinTable>()
        for (part in tableColumnParts) {
            // 分组标识无需处理，只需要去除标识。如：Like_1questionExt-title Like_1questionExt-markdown
            val tableColumnPart = if (part.first().isDigit()) part.substring(1) else part
            // tables = [editUser@user | userExt | username]
            val tables = tableColumnPart.split("-").toMutableList()
            // 最后一个是字段名，不是表名，去除
            // tables = [editUser@user | userExt]
            tables.removeAt(tables.lastIndex)

            var preTableAlias = MAIN_TABLE + "_"
            // user-userExt questionExt
            for (tablePartOrig in tables) {
                var tablePart = tablePartOrig
                // one to one 或 one to many
                var one2many = false
                if (tablePart.startsWith(ONE_TO_MANY)) {
                    one2many = true
                    tablePart = tablePart.substring(ONE_TO_MANY.length)
                }
                // user | questionExt | editUser@user | userId@userRole
                var table = tablePart
                // 别名结尾要加下划线
                // user_ | questionExt_ | editUser@user_ | userId@userRole_
                var tableAlias = table + "_"
                var rightTableId = "id_"
                // table name 与 table alias 不一致，如 editUser@user-userExt-username
                val atIndex = tablePart.indexOf("@")
                if (atIndex != -1) {
                    // user | userRole
                    table = tablePart.substring(atIndex + 1)
                    // editUser_ | userId_
                    tableAlias = tablePart.substring(0, atIndex) + "_"
                    // one2many 则别名部分为左表的ID。如：Like_userId__userRole-role-name
                    // many2one 则正常为别名。如：Like_editUser__user-username
                    if (one2many) {
                        // user_id_
                        rightTableId = camelToUnderscore(tableAlias)
                        // 别名结尾要加下划线
                        // userRole_
                        tableAlias = table + "_"
                    }
                }
                val leftId = if (one2many) "$preTableAlias.id_" else "$preTableAlias.${camelToUnderscore(tableAlias)}id_"
                val rightId = "$tableAlias.$rightTableId"
                if (!joinTables.any { it.tableAlias == tableAlias } || excludeTableAlias.contains(tableAlias)) {
                    joinTables.add(JoinTable(camelToUnderscore(table, tablePrefix), tableAlias, leftId, rightId))
                }
                preTableAlias = tableAlias
            }
        }
        return joinTables
    }

    /** 驼峰名转下划线。如：editUser -> edit_user。可加前缀[prefix]，如 prefix = jspbb_，editUser -> jspbb_edit_user*/
    private fun camelToUnderscore(name: String, prefix: String = ""): String {
        val buff = StringBuilder(prefix)
        name.forEach { c -> buff.append(if (c.isUpperCase()) "_${c.toLowerCase()}" else c) }
        return buff.toString()
    }

    /**
     * <li> username => t_.username_
     * <li> user-username => user_.username_
     * <li> editUser@user-username => editUser_.username_
     * <li> user-MuserExt-realName => userExt_.real_name_
     * <li> user-MuserId@userRole-name => userRole_.real_name_
     * <li> user-MuserId@userRole-role-name => role_.real_name_
     *
     * <li> 没有 table name 则使用 t. 作为前缀
     * <li> 多个 table name 使用最后一个 table name 作为前缀
     */
    private fun getColumn(s: String): String {
        // user-ext@userExt-realName => [user, ext@userExt, realName]
        val columnParts = s.split("-")
        val columnField = StringBuilder()
        // 最后一个为字段名 realName => real_name
        for (c in columnParts.last()) {
            columnField.append(if (c.isUpperCase()) "_${c.toLowerCase()}" else c)
        }
        // 加上结尾的下划线 real_name => real_name_
        columnField.append("_")

        // 没有 table name 则用 t，为主表别名。后面会加别名下划线，这里不用加。
        var columnTable = MAIN_TABLE
        if (columnParts.size >= 2) {
            // 倒数第二个为最后一个表名，使用最后一个表名
            // user | editUser@user | MuserExt | MuserId@userRole | role
            columnTable = columnParts[columnParts.size - 2]
            // 如果为 `Mext@userExt` 则去除 `M`
            if (columnTable.startsWith(ONE_TO_MANY)) {
                // userExt | userId@userRole
                columnTable = columnTable.substring(ONE_TO_MANY.length)
                // one2many 规则是右边的表别名
                // userExt | userRole
                columnTable = columnTable.split("@").last()
            } else {
                // user | editUser@user | role
                // user | editUser | role
                columnTable = columnTable.split("@").first()
            }
        }
        // 表别名要加上下划线 userExt_.real_name_
        return "${columnTable}_.$columnField"
    }

    /**
     * 支持Like, Contain, StartWith, EndWith, In, NotIn, IsNull, IsNotNull, EQ, NE, GT, LT, GE, LE
     */
    private fun getOperator(s: String): String {
        return when (s) {
            OPERATOR_LIKE, OPERATOR_CONTAINS, OPERATOR_STARTS_WITH, OPERATOR_ENDS_WITH -> "LIKE"
            OPERATOR_IN -> "IN"
            OPERATOR_NOT_IN -> "NOT IN"
            OPERATOR_IS_NULL -> "IS NULL"
            OPERATOR_IS_NOT_NULL -> "IS NOT NULL"
            OPERATOR_EQ -> "="
            OPERATOR_NE -> "<>"
            OPERATOR_GT -> ">"
            OPERATOR_GE -> ">="
            OPERATOR_LT -> "<"
            OPERATOR_LE -> "<="
            else -> throw RuntimeException("QueryParser operator '$s' not supported. Support: Like, Contain, StartWith, EndWith, In, NotIn, IsNull, IsNotNull, EQ, NE, GT, LT, GE, LE")
        }
    }

    /**
     * 支持 String, Int(Integer), Long, Double, Boolean, Date, BigDecimal, BigInteger
     */
    private fun getValue(type: String, value: Any, operator: String): Any? {
        // IS NULL 和 IS NOT NULL 不需要 value，直接返回空串
        if (operator == OPERATOR_IS_NULL || operator == OPERATOR_IS_NOT_NULL) return ""
        // value 为空串则返回 null，将被忽略
        // value 为空串或空格串 且 type 非字符串 则返回 null，将被忽略
        if (value is String && (value.isEmpty() || (type != TYPE_STRING && value.isBlank()))) return null
        return when (type) {
            TYPE_STRING -> when (operator) {
                OPERATOR_IN, OPERATOR_NOT_IN -> ThymeleafUtils.getStrings(value)
                OPERATOR_CONTAINS -> "%$value%"
                OPERATOR_STARTS_WITH -> "$value%"
                OPERATOR_ENDS_WITH -> "%$value"
                else -> value.toString()
            }
            TYPE_INT, TYPE_INTEGER -> if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) ThymeleafUtils.getInts(value) else ThymeleafUtils.getInt(value)
            TYPE_LONG -> if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) ThymeleafUtils.getLongs(value) else ThymeleafUtils.getLong(value)
            TYPE_DOUBLE -> if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) ThymeleafUtils.getDoubles(value) else ThymeleafUtils.getDouble(value)
            TYPE_BIG_INTEGER -> if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) ThymeleafUtils.getBigIntegers(value) else ThymeleafUtils.getBigInteger(value)
            TYPE_BIG_DECIMAL -> if (operator == OPERATOR_IN || operator == OPERATOR_NOT_IN) ThymeleafUtils.getBigDecimals(value) else ThymeleafUtils.getBigDecimal(value)
            TYPE_BOOLEAN -> ThymeleafUtils.getBoolean(value)
            TYPE_DATE_TIME -> when (value) {
                is OffsetDateTime -> value
                is ZonedDateTime -> value.toOffsetDateTime()
                is Instant -> OffsetDateTime.ofInstant(value, ZoneId.systemDefault())
                is LocalDateTime -> value.atZone(ZoneId.systemDefault()).toOffsetDateTime()
                // 日期对象（不含时间）且小于等于某一天，则取这天的最后一毫秒
                is LocalDate -> if (operator == OPERATOR_LE) value.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime() else value.atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                is Date -> OffsetDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault())
                is Number -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault())
                // 如果日期字符串长度为10 `yyyy-MM-dd`（只有日期，没有时间），且小于等于某一天，则取这天的最后一毫秒
                is String -> if (value.length == 10 && operator == OPERATOR_LE) LocalDate.parse(value).atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime() else Dates.parseOffsetDateTime(value)
                else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to OffsetDateTime")
            }
            TYPE_DATE -> when (value) {
                // 小于等于某一天，则取这天的最后一毫秒；否则取一天开始的时间。
                is OffsetDateTime -> {
                    if (operator == OPERATOR_LE) LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                is ZonedDateTime -> {
                    if (operator == OPERATOR_LE) LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                is Instant -> {
                    if (operator == OPERATOR_LE) LocalDateTime.ofInstant(value, ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else LocalDateTime.ofInstant(value, ZoneId.systemDefault()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                is LocalDateTime -> {
                    if (operator == OPERATOR_LE) value.toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else value.toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                is LocalDate -> if (operator == OPERATOR_LE) value.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime() else value.atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                is Date -> {
                    if (operator == OPERATOR_LE) LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else LocalDateTime.ofInstant(value.toInstant(), ZoneId.systemDefault()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                is Number -> {
                    if (operator == OPERATOR_LE) LocalDateTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toOffsetDateTime()
                    else LocalDateTime.ofInstant(Instant.ofEpochMilli(value.toLong()), ZoneId.systemDefault()).toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                }
                // 如果日期字符串长度为10 `yyyy-MM-dd`（只有日期，没有时间），且小于等于某一天，则取这天的最后一毫秒
                is String -> if (value.length == 10 && operator == OPERATOR_LE) LocalDate.parse(value).atTime(LocalTime.MAX) else LocalDate.parse(value).atStartOfDay().atZone(ZoneId.systemDefault()).toOffsetDateTime()
                else -> throw RuntimeException("cannot convert '${value.javaClass.name}' to OffsetDateTime")
            }
            else -> throw RuntimeException("QueryParser type '$type' not supported. Support: String, Int(Integer), Long, Double, Boolean, Date, BigDecimal, BigInteger")
        }
    }

    companion object {
        const val TYPE_STRING = "String"
        const val TYPE_INT = "Int"
        const val TYPE_INTEGER = "Integer"
        const val TYPE_LONG = "Long"
        const val TYPE_DOUBLE = "Double"
        const val TYPE_BIG_INTEGER = "BigInteger"
        const val TYPE_BIG_DECIMAL = "BigDecimal"
        const val TYPE_BOOLEAN = "Boolean"
        const val TYPE_DATE_TIME = "DateTime"
        const val TYPE_DATE = "Date"

        /** 字符串使用 LIKE 可以支持通配符，如`%` */
        const val OPERATOR_LIKE = "Like"

        /** 包含字符串。前后加通配符，如`%name%` */
        const val OPERATOR_CONTAINS = "Contains"

        /** 字符串开头。后加通配符，如`name%` */
        const val OPERATOR_STARTS_WITH = "StartsWith"

        /** 字符串结尾。前加通配符，如`%name` */
        const val OPERATOR_ENDS_WITH = "EndsWith"
        const val OPERATOR_IN = "In"
        const val OPERATOR_NOT_IN = "NotIn"
        const val OPERATOR_IS_NULL = "IsNull"
        const val OPERATOR_IS_NOT_NULL = "IsNotNull"

        /** 等于 `=` equals */
        const val OPERATOR_EQ = "EQ"

        /** 不等于 `!=` not equals */
        const val OPERATOR_NE = "NE"

        /** 大于 `>` greater than */
        const val OPERATOR_GT = "GT"

        /** 大于等于 `>=` greater than or equal to */
        const val OPERATOR_GE = "GE"

        /** 小于 `<` less then */
        const val OPERATOR_LT = "LT"

        /** 小于等于 `<=` less than or equal to */
        const val OPERATOR_LE = "LE"

        const val DISTINCT = "Distinct"
        const val ORDER_BY = "OrderBy"


        /**
         * one to one 和 one to many 的关联表主键与主表一致，需要特别表示并处理。使用`O`易于数字`0`混淆，改用`M`。
         */
        private const val ONE_TO_MANY = "M"
        private const val PREFIX = "Q_"
        private const val MAIN_TABLE = "t"
        private val PREVENT_INJECTION_PATTERN: Pattern = Pattern.compile("""[\w.,]*""")
        fun getQueryMap(context: ITemplateContext): MutableMap<String, Any> {
            val queryMap = mutableMapOf<String, Any>()
            context.variableNames.forEach { name ->
                if (name.startsWith(PREFIX)) {
                    queryMap[name.substring(PREFIX.length)] = context.getVariable(name)
                }
            }
            return queryMap
        }

        /**
         * 从 http 请求中获取查询参数
         */
        fun getQueryMap(queryString: String?): MutableMap<String, String> {
            val params = LinkedHashMap<String, String>()
            val paramsMap = parseQueryString(queryString)
            for ((key, value) in paramsMap) {
                if (key.startsWith(PREFIX)) {
                    params[key.substring(PREFIX.length)] = joinByComma(value)
                }
            }
            return params
        }

        /**
         * 与 [StringUtils.join] 不同，null 和 空串 当做不存在，不参与 join
         *
         * @param list 需要 join 的数组，可以为 null
         * @return join 后的字符串。没有内容则为空串
         */
        private fun joinByComma(list: List<String?>?): String {
            if (list.isNullOrEmpty()) return ""
            // 使用 fold 实现，代码比较晦涩，只作为参考。
//            val buff = list.fold(StringBuilder()) { buff, s -> if (!s.isNullOrBlank()) buff.append(s).append(',') else buff }
            val buff = StringBuilder()
            for (s in list) if (!s.isNullOrBlank()) buff.append(s).append(',')
            // 去除最后一个分隔符
            return if (buff.length > 1) buff.substring(0, buff.length - 1) else ""
        }

        /**
         * 只允许_ . , 字母 数字。orderBy 需要用到逗号。
         */
        fun preventInjection(sql: String, allowSpace: Boolean = false): String {
            val buffer = StringBuilder()
            for (s in sql) {
                if ((allowSpace && s == ' ') || PREVENT_INJECTION_PATTERN.matcher(s.toString()).matches()) {
                    buffer.append(s)
                }
            }
            return buffer.toString()
        }
    }
}

class WhereCondition(val column: String, val operator: String, val value: Any)

class JoinTable(tableName: String, tableAlias: String, leftId: String, rightId: String) {
    val tableName: String = preventInjection(tableName)
    val tableAlias: String = preventInjection(tableAlias)
    val leftId: String = preventInjection(leftId)
    val rightId: String = preventInjection(rightId)
}
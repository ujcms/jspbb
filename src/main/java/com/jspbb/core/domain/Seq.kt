package com.jspbb.core.domain


/**
 * 序列实体类
 */
data class Seq(
        /** 序列名称 */
        var name: String = "",
        /** 下一个序列值 */
        private var nextVal: Long = 1,
        /** 序列缓存大小 */
        private var cacheSize: Int = 0
) {
    /** 当前缓存值 */
    private var cacheVal = nextVal

    /**
     * 获取下一个缓存值。如果缓存为空，则返回<code>null</code>。
     */
    fun nextCachedVal(): Long? = if (!isCacheEmpty()) cacheVal++ else null

    /**
     * 缓存是否为空。如果为空请先执行[makeCache]生成缓存
     */
    fun isCacheEmpty(): Boolean = cacheVal >= nextVal

    /**
     * 生成缓存。生成缓存后，需持久化到数据库。
     *
     * @param defaultCacheSize 如果数据库中[cacheSize] < 0，则使用该值。
     */
    fun makeCache(defaultCacheSize: Int = DEFAULT_CACHE_SIZE) {
        cacheVal = nextVal
        nextVal += when {
            cacheSize > 0 -> cacheSize
            defaultCacheSize > 0 -> defaultCacheSize
            else -> DEFAULT_CACHE_SIZE
        }
    }
    companion object {
        /** 序列默认缓存数。如果数据库和程序均未指定缓存大小，则使用该值。 */
        const val DEFAULT_CACHE_SIZE = 50
    }
}

package com.jspbb.core.domain

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.jspbb.core.domain.config.*
import org.springframework.mobile.device.DeviceResolver
import javax.servlet.http.HttpServletRequest

data class Config(var name: String = "", var value: String?)

class Configs(configs: List<Config>) {
    private val configMap = associateConfigMap(configs)
    // 使用 json 作为桥梁，将字符串格式的 Map 转为 对象。
    val base = mapper.readValue<ConfigBase>(mapper.writeValueAsString(configMap[ConfigBase.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    val signUp = mapper.readValue<ConfigSignUp>(mapper.writeValueAsString(configMap[ConfigSignUp.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    val email = mapper.readValue<ConfigEmail>(mapper.writeValueAsString(configMap[ConfigEmail.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    val restrict = mapper.readValue<ConfigRestrict>(mapper.writeValueAsString(configMap[ConfigRestrict.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    val watermark = mapper.readValue<ConfigWatermark>(mapper.writeValueAsString(configMap[ConfigWatermark.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    val upload = mapper.readValue<ConfigUpload>(mapper.writeValueAsString(configMap[ConfigUpload.TYPE_PREFIX] ?: emptyMap<String, String?>()))
    /**
     * 根据请求获取主题
     */
    fun getTheme(request: HttpServletRequest, deviceResolver: DeviceResolver): String {
        val device = deviceResolver.resolveDevice(request)
        return when {
            // 手机和平板都显示手机主题
            device.isMobile || device.isTablet -> base.themeMobile
            else -> base.themeNormal
        }
    }

    companion object {
        // 增加 LocalDateTime 的处理、忽略未识别的属性。
        val mapper: ObjectMapper = ObjectMapper().registerModule(JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

        /**
         * 将 config 列表转换成以前缀分类的 Map，子 Map 的 key 去掉了前缀。
         */
        fun associateConfigMap(configs: List<Config>): Map<String, Map<String, String?>> {
            val configMap = HashMap<String, MutableMap<String, String?>>()
            for ((name, value) in configs) {
                // 下划线之前为前缀
                val index = name.indexOf("_")
                if (index >= 0 && index < name.length) {
                    val prefix = name.substring(0, index + 1)
                    var map = configMap[prefix]
                    if (map == null) {
                        map = HashMap()
                        configMap[prefix] = map
                    }
                    map[name.substring(index + 1)] = value
                }
            }
            return configMap
        }

        /**
         * 将对象转为 Map<String, String?>
         */
        fun toMap(obj: Any) = mapper.readValue<Map<String, String?>>(mapper.writeValueAsString(obj))
    }
}

package com.jspbb.util.ip

import org.lionsoul.ip2region.DbConfig
import org.lionsoul.ip2region.DbSearcher
import org.lionsoul.ip2region.Util
import org.springframework.core.io.Resource


class IpSeeker(
        private val dbFile: Resource
) : AutoCloseable {
    /** 查找IP信息 */
    fun find(ip: String?): Region {
        // 本机地址直接设置为LAN
        if ("0:0:0:0:0:0:0:1" == ip || "127.0.0.1" == ip || "localhost" == ip) return Region(LAN, LAN, LAN, LAN)
        try {
            if (ip != null && Util.isIpAddress(ip)) {
                searcher.btreeSearch(ip)?.let {
                    // 数据格式为：国家|区域|省份|城市|ISP
                    val arr = it.region.split("|").toMutableList()
                    // 如果是'内网IP'，则将所有值设置为'LAN'。ip2region只会在arr[3]给出'内网IP'
                    if (arr[3] == "内网IP") {
                        arr[0] = LAN; arr[1] = arr[0]; arr[2] = arr[0]; arr[3] = arr[0]; arr[4] = arr[1]
                    }
                    // 无值的部分为'0'
                    return Region(if (arr[0] != "0") arr[0] else null,
                            if (arr[2] != "0") arr[2] else null,
                            if (arr[3] != "0") arr[3] else null,
                            if (arr[4] != "0") arr[4] else null)
                }
            }
        } catch (e: NumberFormatException) {
            // invalid ip address
        }
        return Region()
    }

    override fun close() = searcher.close()

    private val searcher = DbSearcher(DbConfig(), dbFile.file.absolutePath)

    companion object {
        /** 内网 */
        const val LAN = "LAN"
    }
}
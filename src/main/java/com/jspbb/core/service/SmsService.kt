package com.jspbb.core.service

import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest
import com.aliyuncs.profile.DefaultProfile
import com.github.pagehelper.Page
import com.github.pagehelper.PageHelper
import com.jspbb.core.domain.Sms
import com.jspbb.core.mapper.SmsMapper
import com.jspbb.util.ip.IpSeeker
import com.jspbb.util.web.QueryParser
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.OffsetDateTime

/**
 * 短信Service
 */
@Service
class SmsService(
        private val ipSeeker: IpSeeker,
        private val configService: ConfigService,
        private val props: SmsProperties,
        private val seqService: SeqService,
        private val mapper: SmsMapper
) {
    @Component
    @ConfigurationProperties(prefix = "aliyun.mns")
    data class SmsProperties(
            var accessKeyId: String = "",
            var accessKeySecret: String = "",
            var signName: String = "",
            var verifyCodeName: String = "",
            var verifyCodeLength: Int = 6,
            var templateCode: String = ""
    )

    fun select(id: Long): Sms? = mapper.select(id)

    @Transactional
    fun insert(record: Sms) {
        record.id = seqService.getNextVal(Sms.TABLE_NAME)
        mapper.insert(record)
    }

    @Transactional
    fun update(record: Sms): Long = mapper.update(record)

    @Transactional
    fun delete(id: Long): Long = mapper.delete(id)

    fun selectPage(queryMap: Map<String, Any>, page: Int, pageSize: Int): Page<Sms> {
        val parser = QueryParser(queryMap, "id_desc")
        return PageHelper.startPage<Sms>(page, pageSize).doSelectPage { mapper.selectAll(parser) }
    }

    fun selectList(queryMap: Map<String, Any>, offset: Int = 0, limit: Int? = null): List<Sms> {
        val parser = QueryParser(queryMap, "id_desc")
        return if (limit == null) {
            mapper.selectAll(parser)
        } else {
            PageHelper.offsetPage<Sms>(offset, limit, false).doSelectPage { mapper.selectAll(parser) }
        }
    }

    @Transactional
    fun sendMobileMessage(mobile: String, usage: String, ip: String): Sms {
        val code = sendMobileMessage(props.accessKeyId, props.accessKeySecret, props.signName, props.templateCode, props.verifyCodeName, props.verifyCodeLength, mobile)
        val region = ipSeeker.find(ip)
        val sms = Sms(type = Sms.TYPE_MOBILE, code = code, usage = usage, receiver = mobile, ip = ip, ipCountry = region.country, ipProvince = region.province, ipCity = region.city)
        insert(sms)
        return sms
    }

    @Transactional
    fun sendEmailMessage(email: String, usage: String, ip: String): Sms {
        val code = RandomStringUtils.randomNumeric(props.verifyCodeLength)
        val configs = configService.selectConfigs()
        configs.email.sendMail(arrayOf(email), configs.signUp.verifyEmailSubject, configs.signUp.getReplacedVerifyEmailText(code))
        val region = ipSeeker.find(ip)
        val sms = Sms(type = Sms.TYPE_EMAIL, code = code, usage = usage, receiver = email, ip = ip, ipCountry = region.country, ipProvince = region.province, ipCity = region.city)
        insert(sms)
        return sms
    }

    @Transactional
    fun tryEmailMessage(emailMessage: String?, emailMessageId: Long?, email: String?): Boolean = tryMessage(emailMessage, emailMessageId, email, Sms.TYPE_EMAIL)

    @Transactional
    fun tryMobileMessage(mobileMessage: String?, mobileMessageId: Long?, mobile: String?): Boolean = tryMessage(mobileMessage, mobileMessageId, mobile, Sms.TYPE_MOBILE)

    private fun tryMessage(message: String?, messageId: Long?, receiver: String?, type: String): Boolean {
        if (message.isNullOrBlank() || messageId == null || receiver.isNullOrBlank()) return false
        val sms = select(messageId) ?: return false
        // 验证码长度正确才是有效的尝试，防止尝试计数无意义增加
        if (!sms.isUnused() || sms.type != type || sms.receiver != receiver || sms.code.length != message.length) return false
        // 尝试失败增加计数
        return when {
            sms.isExpired() -> {
                sms.status = Sms.STATUS_EXPIRED
                sms.tryCount += 1
                update(sms);
                false
            }
            sms.tryCount >= Sms.MAX_TRY_COUNT -> {
                sms.status = Sms.STATUS_TOO_MANY_ATTEMPTS
                sms.tryCount += 1
                update(sms);
                false
            }
            sms.code != message -> {
                sms.tryCount += 1
                update(sms);
                false
            }
            else -> true
        }
    }

    @Transactional
    fun validateEmailMessage(emailMessage: String?, emailMessageId: Long?, email: String?, usage: String): Boolean = validateMessage(emailMessage, emailMessageId, email, Sms.TYPE_EMAIL, usage)

    @Transactional
    fun validateMobileMessage(mobileMessage: String?, mobileMessageId: Long?, mobile: String?, usage: String): Boolean = validateMessage(mobileMessage, mobileMessageId, mobile, Sms.TYPE_MOBILE, usage)

    private fun validateMessage(message: String?, messageId: Long?, receiver: String?, type: String, usage: String): Boolean {
        if (message.isNullOrBlank() || messageId == null || receiver.isNullOrBlank()) return false
        val sms = select(messageId) ?: return false
        if (!sms.isUnused() || sms.type != type || sms.receiver != receiver || sms.usage != usage) return false
        val isValid = sms.code == message
        sms.status = if (isValid) Sms.STATUS_CORRECT else Sms.STATUS_WRONG
        update(sms)
        return isValid
    }


    fun countByIp(ip: String, beginDate: OffsetDateTime, type: String): Long {
        return mapper.countByIp(ip, beginDate, type)
    }

    companion object {
        // 短信发送SDK：https://help.aliyun.com/document_detail/55359.html
        // 短信发送API：https://help.aliyun.com/document_detail/55284.html
        fun sendMobileMessage(accessKeyId: String, accessKeySecret: String, signName: String, templateCode: String,
                              verifyCodeName: String, verifyCodeLength: Int, mobile: String): String {
            if (accessKeyId.isBlank()) throw RuntimeException("aliyun.mns.accessKeyId not set")
            val code = RandomStringUtils.randomNumeric(verifyCodeLength)
            //设置超时时间-可自行调整
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000")
            System.setProperty("sun.net.client.defaultReadTimeout", "10000")
            //初始化ascClient需要的几个参数
            val product = "Dysmsapi"//短信API产品名称（短信产品名固定，无需修改）
            val domain = "dysmsapi.aliyuncs.com"//短信API产品域名（接口地址固定，无需修改）
            //初始化ascClient,暂时不支持多region（请勿修改）
            val profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret)
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain)
            val acsClient = DefaultAcsClient(profile)
            //组装请求对象
            val request = SendSmsRequest()
            //使用post提交
            request.method = com.aliyuncs.http.MethodType.POST
            //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.phoneNumbers = mobile
            //必填:短信签名-可在短信控制台中找到
            request.signName = signName
            //必填:短信模板-可在短信控制台中找到
            request.templateCode = templateCode
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.templateParam = """{"$verifyCodeName":"$code"}"""
            //可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.outId = "yourOutId"
            //请求失败这里会抛ClientException异常
            val sendSmsResponse = acsClient.getAcsResponse(request)
            if (sendSmsResponse.code == "OK") {
                //请求成功
                return code
            } else {
                //请求失败
                throw RuntimeException("short message sending failure: ${sendSmsResponse.code}")
            }
        }

// 老版本
//        fun sendMobileMessage(accessId: String, accessKey: String, endpoint: String, topic: String, signName: String,
//                              templateCode: String, verifyCodeName: String, verifyCodeLength: Int, templateParams: String?, mobile: String): String {
//            // Step 1. 获取主题引用
//            val account = CloudAccount(accessId, accessKey, endpoint)
//            val client = account.mnsClient
//            val cloudTopic = client.getTopicRef(topic)
//            // Step 2. 设置SMS消息体（必须）
//            // 注：目前暂时不支持消息内容为空，需要指定消息内容，不为空即可。
//            val msg = RawTopicMessage()
//            msg.messageBody = "sms-message"
//            // Step 3. 生成SMS消息属性
//            val messageAttributes = MessageAttributes()
//            val batchSmsAttributes = BatchSmsAttributes()
//            // 3.1 设置发送短信的签名（SMSSignName）
//            batchSmsAttributes.freeSignName = signName
//            // 3.2 设置发送短信使用的模板（SMSTempateCode）
//            batchSmsAttributes.templateCode = templateCode
//            // 3.3 设置发送短信所使用的模板中参数对应的值（在短信模板中定义的，没有可以不用设置）
//            val smsReceiverParams = BatchSmsAttributes.SmsReceiverParams()
//            val code = RandomStringUtils.randomNumeric(verifyCodeLength)
//            smsReceiverParams.setParam(verifyCodeName, code)
//            if (templateParams != null && templateParams.isNotBlank()) {
//                val param: Map<String, String> = jacksonObjectMapper().readValue(templateParams)
//                param.forEach { key, value -> smsReceiverParams.setParam(key, value) }
//            }
//            // 3.4 增加接收短信的号码
//            batchSmsAttributes.addSmsReceiver(mobile, smsReceiverParams)
//            messageAttributes.batchSmsAttributes = batchSmsAttributes
//            try {
//                val topicMessage = cloudTopic.publishMessage(msg, messageAttributes)
//                println(topicMessage)
//            } finally {
//                client.close()
//            }
//            return code
//        }
    }
}

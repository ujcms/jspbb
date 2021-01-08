package com.jspbb.util.captcha;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;

/**
 * TrialImageCaptchaService
 *
 * @author liufang
 */
public interface TrialImageCaptchaService extends ImageCaptchaService {
    /**
     * 尝试验证码是否正确，不管尝试是否成功，验证码都继续保持有效
     *
     * @param id
     * @param response
     * @return
     * @throws CaptchaServiceException
     */
    Boolean tryResponseForID(String id, Object response) throws CaptchaServiceException;

    Boolean tryResponseForID(String id, Object response, boolean removeOnError) throws CaptchaServiceException;
}

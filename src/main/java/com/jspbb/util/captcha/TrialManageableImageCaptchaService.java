package com.jspbb.util.captcha;

import com.octo.captcha.engine.CaptchaEngine;
import com.octo.captcha.engine.image.gimpy.DefaultGimpyEngine;
import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.captchastore.CaptchaStore;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;

/**
 * TrialManageableImageCaptchaService
 *
 * @author liufang
 */
public class TrialManageableImageCaptchaService extends DefaultManageableImageCaptchaService implements TrialImageCaptchaService {
    /**
     * Construct a new ImageCaptchaService with a
     * {@link FastHashMapCaptchaStore} and a {@link DefaultGimpyEngine}
     * minGuarantedStorageDelayInSeconds = 180s maxCaptchaStoreSize = 100000
     * captchaStoreLoadBeforeGarbageCollection=75000
     */
    public TrialManageableImageCaptchaService() {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), 180, 100000, 75000);
    }

    /**
     * Construct a new ImageCaptchaService with a
     * {@link FastHashMapCaptchaStore} and a {@link DefaultGimpyEngine}
     *
     * @param minGuarantedStorageDelayInSeconds
     * @param maxCaptchaStoreSize
     * @param captchaStoreLoadBeforeGarbageCollection
     */
    public TrialManageableImageCaptchaService(int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(new FastHashMapCaptchaStore(), new DefaultGimpyEngine(), minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    /**
     * @param captchaStore
     * @param captchaEngine
     * @param minGuarantedStorageDelayInSeconds
     * @param maxCaptchaStoreSize
     * @param captchaStoreLoadBeforeGarbageCollection
     */
    public TrialManageableImageCaptchaService(CaptchaStore captchaStore, CaptchaEngine captchaEngine, int minGuarantedStorageDelayInSeconds, int maxCaptchaStoreSize, int captchaStoreLoadBeforeGarbageCollection) {
        super(captchaStore, captchaEngine, minGuarantedStorageDelayInSeconds, maxCaptchaStoreSize, captchaStoreLoadBeforeGarbageCollection);
    }

    @Override
    public Boolean tryResponseForID(String id, Object response) throws CaptchaServiceException {
        return tryResponseForID(id, response, false);
    }

    @Override
    public Boolean tryResponseForID(String id, Object response, boolean removeOnError) throws CaptchaServiceException {
        if (!store.hasCaptcha(id)) throw new CaptchaServiceException("Invalid ID, could not validate unexisting or already validated captcha");
        Boolean valid = store.getCaptcha(id).validateResponse(response);
        if (removeOnError) store.removeCaptcha(id);
        return valid;
    }
}

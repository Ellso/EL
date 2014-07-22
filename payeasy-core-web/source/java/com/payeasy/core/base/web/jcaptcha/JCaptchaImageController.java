package com.payeasy.core.base.web.jcaptcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class JCaptchaImageController implements Controller, InitializingBean {

    private ImageCaptchaService imageCaptchaService;

    public ImageCaptchaService getImageCaptchaService() {
        return this.imageCaptchaService;
    }

    public void setImageCaptchaService(ImageCaptchaService imageCaptchaService) {
        this.imageCaptchaService = imageCaptchaService;
    }

    public void afterPropertiesSet() throws Exception {

        if (this.imageCaptchaService == null) {
            throw new RuntimeException("Required imageCaptchaService not set");
        }
    }

    public ModelAndView handleRequest(HttpServletRequest httpServletRequest, HttpServletResponse response) throws Exception {
        byte[] captchaChallengeAsJpeg = null;

        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();

        //get the session id that will identify the generated captcha.
        //the same id must be used to validate the response, the session id is a good candidate!
        String captchaId = httpServletRequest.getSession().getId();

        BufferedImage challenge =
                this.imageCaptchaService.getImageChallengeForID(captchaId, httpServletRequest.getLocale());

        JPEGImageEncoder jpegEncoder =
                JPEGCodec.createJPEGEncoder(jpegOutputStream);
        jpegEncoder.encode(challenge);

        captchaChallengeAsJpeg = jpegOutputStream.toByteArray();

        // configure cache  directives
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        //flush content in the response
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close();
        return null;
    }

}
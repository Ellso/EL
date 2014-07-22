package com.payeasy.core.acl.web.security.providers;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.OtpBadTokenException;
import com.payeasy.core.acl.web.security.OtpServiceException;
import com.payeasy.otp.delegate.OtpBusinessDelegate;
import com.payeasy.otp.dto.OtpRecordDTO;
import com.payeasy.otp.exception.HinetOtpServerException;
import com.payeasy.otp.exception.OtpException;

@SuppressWarnings("unchecked")
public class OtpAuthenticationProvider extends AclAuthenticationProvider {

    private static final Log logger = LogFactory.getLog(OtpAuthenticationProvider.class);

    private OtpBusinessDelegate otpBD;

    public void setOtpBD(OtpBusinessDelegate otpBD) {
        this.otpBD = otpBD;
    }

    public boolean supports(Class authentication) {
        return (OtpAuthenticationToken.class.isAssignableFrom(authentication));
    }

    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
        Assert.notNull(this.otpBD, "otpBD is required");
    }

    protected final void postAuthentication(Authentication authentication) {
        Assert.isInstanceOf(OtpAuthenticationToken.class, authentication,
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only OtpAuthenticationToken is supported"));

        this.validateToken((OtpAuthenticationToken) authentication);
    }

    private void validateToken(OtpAuthenticationToken authentication) {

        String username = authentication.getName();
        String token = authentication.getToken();
        String serial = authentication.getSerial();

        if (StringUtils.isBlank(username)) {
            throw new OtpBadTokenException("Username is blank");
        }

        if (StringUtils.isBlank(token)) {
            throw new OtpBadTokenException("Token is blank");
        }

        if (StringUtils.isBlank(serial)) {
            throw new OtpBadTokenException("Serial is blank");
        }

        OtpRecordDTO otpRecord = new OtpRecordDTO();
        otpRecord.setUserId(username);
        otpRecord.setOtpToken(token);
        otpRecord.setOtpSerial(serial);

        boolean result = false;

        try {
            result = this.otpBD.checkToken(otpRecord);
        } catch (OtpException e) {
            logger.error("validateToken(otpRecord = " + otpRecord
                    + ") - Catch OtpBadTokenException", e);
            throw new OtpServiceException("validateToken(otpRecord = " + otpRecord
                    + ") - Catch OtpException", e);
        } catch (HinetOtpServerException e) {
            logger.error("validateToken(otpRecord = " + otpRecord
                    + ") - Catch HinetOtpServerException", e);
            throw new OtpServiceException("validateToken(otpRecord = " + otpRecord
                    + ") - Catch HinetOtpServerException", e);
        }

        if (!result) {
            throw new OtpBadTokenException("Bad token");
        }
    }

}
package com.payeasy.core.acl.web.security.event;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.Authentication;
import org.springframework.security.ui.WebAuthenticationDetails;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationFailureEvent;
import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationSuccessEvent;

public class JdbcAuthenticationEventHandler extends AbstractAuthenticationEventHandler implements InitializingBean {

    private static final Log logger = LogFactory.getLog(JdbcAuthenticationEventHandler.class);

    private JdbcTemplate jdbcTemplate;

    private String meaAppName;
    private String successStatus;
    private String defaultExceptionStatus;
    private Properties exceptionStatusMappings;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setMeaAppName(String meaAppName) {
        this.meaAppName = meaAppName;
    }

    public void setSuccessStatus(String successStatus) {
        this.successStatus = successStatus;
    }

    public void setDefaultExceptionStatus(String defaultExceptionStatus) {
        this.defaultExceptionStatus = defaultExceptionStatus;
    }

    public void setExceptionStatusMappings(Properties exceptionStatusMappings) {
        this.exceptionStatusMappings = exceptionStatusMappings;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.jdbcTemplate, "jdbcTemplate is required");
        Assert.hasText(this.meaAppName, "meaAppName is required");
        Assert.hasText(this.successStatus, "successStatus is required");
        Assert.hasText(this.defaultExceptionStatus, "defaultExceptionStatus is required");
        Assert.notEmpty(this.exceptionStatusMappings, "exceptionStatusMappings is required");
    }

    public void handle(ApplicationEvent event) {
        if (event instanceof AclAuthenticationSuccessEvent) {
            AclAuthenticationSuccessEvent successEvent = (AclAuthenticationSuccessEvent) event;
            Authentication authentication = successEvent.getAuthentication();
            String actType = successEvent.getActType().getValue();

            this.persist(authentication, actType, null);
        }

        if (event instanceof AclAuthenticationFailureEvent) {
            AclAuthenticationFailureEvent failureEvent = (AclAuthenticationFailureEvent) event;
            Authentication authentication = failureEvent.getAuthentication();
            String actType = failureEvent.getActType().getValue();
            String exceptionClassName = failureEvent.getException().getClass().getName();

            this.persist(authentication, actType, exceptionClassName);
        }
    }

    private void persist(Authentication authentication, String actType, String exceptionClassName) {

        final String sql = new StringBuffer()
                .append("INSERT INTO MEM_EVENT_AUDIT ")
                .append("  (MEA_NUM, ")
                .append("   MEM_ID, ")
                .append("   MEA_ACT_TYPE, ")
                .append("   MEA_STATUS_FLAG, ")
                .append("   MEA_IP, ")
                .append("   MEA_DATE, ")
                .append("   MEA_APP_NAME) ")
                .append("VALUES ")
                .append("  (MEM_EVENT_AUDIT_COUNTER.NEXTVAL, ?, ?, ?, ?, SYSDATE, ?)")
                .toString();

        String memId = authentication.getName();
        String meaStatusFlag = this.successStatus;
        String meaIp = ((WebAuthenticationDetails) authentication.getDetails()).getRemoteAddress();

        if (StringUtils.isNotBlank(exceptionClassName)) {
            meaStatusFlag = this.exceptionStatusMappings.getProperty(exceptionClassName, this.defaultExceptionStatus);
        }

        Object[] params = new Object[] { memId, actType, meaStatusFlag, meaIp, this.meaAppName };

        try {
            this.jdbcTemplate.update(sql, params);
        } catch (DataAccessException e) {
            logger.error("persist(memId = " + memId
                    + ", actType = " + actType
                    + ", meaStatusFlag = " + meaStatusFlag
                    + ", meaIp = " + meaIp
                    + ", meaAppName = " + this.meaAppName
                    + ") - Catch DataAccessException", e);
        }
    }

}
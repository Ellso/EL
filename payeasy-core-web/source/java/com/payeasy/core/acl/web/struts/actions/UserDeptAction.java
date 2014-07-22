package com.payeasy.core.acl.web.struts.actions;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import com.payeasy.acl.authentication.service.delegate.AuthenticationBD;
import com.payeasy.acl.authentication.service.dto.UserDeptDTO;
import com.payeasy.acl.authentication.service.exception.AclAuthenticationException;
import com.payeasy.core.acl.web.struts.AclActionSupport;

public class UserDeptAction extends AclActionSupport {

    private static final long serialVersionUID = -2359290394586656301L;

    private static final Logger logger = Logger.getLogger(UserDeptAction.class);

    // spring attribute
    // =========================================================================
    private AuthenticationBD authenticationBD;
    
    // form attribute
    // =========================================================================
    private List<UserDeptDTO> userDepts;
    
    public void setAuthenticationBD(AuthenticationBD authenticationBD) {
        this.authenticationBD = authenticationBD;
    }

    public List<UserDeptDTO> getUserDepts() {
        return userDepts;
    }

    public void setUserDepts(List<UserDeptDTO> userDepts) {
        this.userDepts = userDepts;
    }

    @SkipValidation
    public String list() {
        Long usrNum = this.getAclUserDetails().getUsernum();
        
        try {
            this.userDepts = this.authenticationBD.findUserDeptsByUsrNum(usrNum);
        } catch (AclAuthenticationException e) {
            logger.error("list(usrNum = " + usrNum
                    + ") - Catch AclAuthenticationException", e);
            return ERROR;
        }
        
        return SUCCESS;
    }
    
    public String update() {
        
        try {
            this.authenticationBD.updateUserDepts(userDepts);
        } catch (AclAuthenticationException e) {
            logger.error("update(userDepts = " + userDepts
                    + ") - Catch AclAuthenticationException", e);
            return ERROR;
        }
        
        return SUCCESS;
    }
    
}

package com.payeasy.core.acl.web.security.userdetails;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.AccountExpiredException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.DisabledException;
import org.springframework.security.LockedException;
import org.springframework.security.providers.dao.SaltSource;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

import com.payeasy.acl.authentication.service.delegate.AuthenticationBD;
import com.payeasy.acl.authentication.service.dto.GrantedMenuDTO;
import com.payeasy.acl.authentication.service.dto.GrantedPermissionDTO;
import com.payeasy.acl.authentication.service.dto.UserDetailDTO;
import com.payeasy.acl.authentication.service.exception.AclAuthenticationException;
import com.payeasy.acl.authentication.service.exception.AclUserNotFoundException;

public class AclUserDetailsManagerImpl implements AclUserDetailsManager {

    private static final long serialVersionUID = -8063217695304747205L;
    private static final Logger logger = Logger.getLogger(AclUserDetailsManagerImpl.class);

    public static final String ROLE_APPLICATION_ADMIN = "ROLE_APPLICATION_ADMIN";

    private Long appNum;
    private SaltSource saltSource;
    private PasswordEncoder passwordEncoder;
    private AuthenticationBD authenticationBD;
    private boolean hideDbApplicationAdmin = false;
    private List<String> applicationAdmins = new ArrayList<String>();

    public void setAppNum(Long appNum) {
        this.appNum = appNum;
    }

    public void setSaltSource(SaltSource saltSource) {
        this.saltSource = saltSource;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setAuthenticationBD(AuthenticationBD authenticationBD) {
        this.authenticationBD = authenticationBD;
    }

    public void setHideDbApplicationAdmin(boolean hideDbApplicationAdmin) {
        this.hideDbApplicationAdmin = hideDbApplicationAdmin;
    }

    public void setApplicationAdmins(List<String> applicationAdmins) {
        this.applicationAdmins = applicationAdmins;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.appNum, "appNum is required");
        Assert.notNull(this.passwordEncoder, "passwordEncoder is required");
        Assert.notNull(this.authenticationBD, "authenticationBD is required");
        Assert.notNull(this.applicationAdmins, "applicationAdmins is required");
    }

    public UserDetails loadUserByUsername(String username) {

        // step1 - �d�ߨϥΪ̸��
        UserDetailDTO userDetailDTO = this.loadUserByUsrId(username);

        // step2 - �d���v�����
        List<AclGrantedAuthority> grantedAuthorities = new ArrayList<AclGrantedAuthority>(); // �v�����
        grantedAuthorities.add(new AclGrantedAuthority("HOLDER")); // �[�J���v��

        List<GrantedPermissionDTO> grantedPermissionDTOs = this.loadPermissionsByUsrId(this.appNum, username);

        // step3 - �d�ߥ��x�޲z�����
        Boolean isApplicationAdmin = this.applicationAdmins.contains(username); // �ѳ]�w�ɪ����]�w���x�޲z��

        if (isApplicationAdmin == false && this.hideDbApplicationAdmin == false) { // �P�_�O�_���ø�Ʈw���x�޲z���]�w
            isApplicationAdmin = this.isApplicationAdminByUsrId(this.appNum, username);
        }

        // step4 - �Y�����x�޲z���A�h�[�JROLE_APPLICATION_ADMIN�v��
        if (isApplicationAdmin) {
            grantedAuthorities.add(new AclGrantedAuthority(ROLE_APPLICATION_ADMIN));
        }

        // step5 - ��z�v����ơA�ðO���v�����������y����
        List<Long> grantedMenuNums = new ArrayList<Long>(); // �O���v����ƹ����쪺���y����

        for (int i = 0; i < grantedPermissionDTOs.size(); i++) {
            GrantedPermissionDTO grantedPermissionDTO = grantedPermissionDTOs.get(i);

            grantedMenuNums.add(grantedPermissionDTO.getMenuNum());
            grantedAuthorities.add(new AclGrantedAuthority(grantedPermissionDTO.getPerId()));
        }

        // step6 - �d�ߥ��x�����
        List<AclGrantedMenu> grantedMenus = new ArrayList<AclGrantedMenu>(); // �����
        List<GrantedMenuDTO> grantedMenuDTOs = this.loadMenusByAppNum(this.appNum);

        // step7 - �Q���v�����������y�����ο��𪬶��h�A���o�t�Υi�H��ܪ������
        for (GrantedMenuDTO grantedMenu : grantedMenuDTOs) {
            Long menuNum = grantedMenu.getMenuNum();

            if (grantedMenu != null) {
                // �ھ��v�����y�����A�ӧP�_�O�_���i��ܪ������
                if (grantedMenu.containsAnyMenuChild(grantedMenuNums) || grantedMenuNums.contains(menuNum)) {
                    grantedMenus.add(new AclGrantedMenu(
                            grantedMenu.getMenuNum(),
                            grantedMenu.getMenuParent(),
                            grantedMenu.isLeaf(),
                            grantedMenu.getMenuName(),
                            grantedMenu.getMenuUrl()
                    ));
                }
            }
        }

        // step8 - �إߨϥΪ̸�ơA�t�����v�����
        AclUserDetails userDetails = this.createNewAclUserDetails(userDetailDTO, grantedAuthorities, grantedMenus);

        if (logger.isDebugEnabled()) {
            logger.debug("AclUserDetails: " + userDetails);
        }

        return userDetails;
    }

    public boolean isPasswordValid(UserDetails userDetails, String rawPass) {
        Object salt = null;

        if (this.saltSource != null) {
            salt = this.saltSource.getSalt(userDetails);
        }

        return this.passwordEncoder.isPasswordValid(userDetails.getPassword(), rawPass, salt);
    }

    public void changePasswordByUsername(String username, String newPassword, String oldPassword) {
        // step1 - �d�ߨϥΪ̸��
        UserDetailDTO userDetailDTO = this.loadUserByUsrId(username);

        // step2 - �ܧ�ϥΪ̱K�X�ɡA���d���v���ο����
        List<AclGrantedAuthority> grantedAuthorities = new ArrayList<AclGrantedAuthority>(); // �v�����
        grantedAuthorities.add(new AclGrantedAuthority("HOLDER")); // �[�J���v��

        List<AclGrantedMenu> grantedMenus = new ArrayList<AclGrantedMenu>(); // �����

        UserDetails userDetails = this.createNewAclUserDetails(userDetailDTO, grantedAuthorities, grantedMenus);

        // step3 - �ˬd�b���αK�X�����A
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked", userDetails);
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled", userDetails);
        }

        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired", userDetails);
        }

        // step4 - �����±K�X�O�_�X�k
        Object salt = null;

        if (this.saltSource != null) {
            salt = this.saltSource.getSalt(userDetails);
        }

        if (!this.passwordEncoder.isPasswordValid(userDetails.getPassword(), oldPassword, salt)) {
            throw new BadCredentialsException("Bad old credentials", userDetails);
        }

        // step5 - �ܧ�K�X
        try {
            this.authenticationBD.changePasswordByUsrId(username,
                    this.passwordEncoder.encodePassword(newPassword, salt));
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

    }

    public void resetPasswordCountByUsername(String username) {
        try {
            this.authenticationBD.resetPasswordCountByUsrId(username);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    public void addPasswordCountByUsername(String username) {
        try {
            this.authenticationBD.addPasswordCountByUsrId(username);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    public void lockUserByUsername(String username) {
        try {
            this.authenticationBD.lockUserByUsrId(username);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private UserDetailDTO loadUserByUsrId(String usrId){
        try {
            return this.authenticationBD.loadUserByUsrId(usrId);
        } catch (AclUserNotFoundException e) {
            throw new UsernameNotFoundException("Username not found", usrId);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private Boolean isApplicationAdminByUsrId(Long appNum, String usrId) {
        try {
            return this.authenticationBD.isApplicationAdminByUsrId(appNum, usrId);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private List<GrantedPermissionDTO> loadPermissionsByUsrId(Long appNum, String usrId){
        try {
            return this.authenticationBD.loadPermissionsByUsrId(appNum, usrId);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private List<GrantedMenuDTO> loadMenusByAppNum(Long appNum) {
        try {
            return this.authenticationBD.loadMenusByAppNum(appNum);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    private AclUserDetails createNewAclUserDetails(UserDetailDTO userDetailDTO,
            List<AclGrantedAuthority> grantedAuthorities, List<AclGrantedMenu> grantedMenus) {

        return new AclUserDetails(
                userDetailDTO.getUsrNum(),
                userDetailDTO.getUsrName(),
                userDetailDTO.getUsrId(),
                userDetailDTO.getUsrPwd(),
                userDetailDTO.getUsrRegno(),
                userDetailDTO.getUsrTel(),
                userDetailDTO.getUsrFax(),
                userDetailDTO.getUsrEmail(),
                userDetailDTO.getUsrMtel(),
                userDetailDTO.getUsrInvalidCount(),
                (userDetailDTO.getUsrEnabled() == 1),
                (userDetailDTO.getUsrLocked() == 0),
                true,
                (System.currentTimeMillis() < userDetailDTO.getUsrExpiredDate().getTime()),
                grantedAuthorities.toArray(new AclGrantedAuthority[grantedAuthorities.size()]),
                grantedMenus.toArray(new AclGrantedMenu[grantedMenus.size()])
        );
    }

}
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

        // step1 - 查詢使用者資料
        UserDetailDTO userDetailDTO = this.loadUserByUsrId(username);

        // step2 - 查詢權限資料
        List<AclGrantedAuthority> grantedAuthorities = new ArrayList<AclGrantedAuthority>(); // 權限資料
        grantedAuthorities.add(new AclGrantedAuthority("HOLDER")); // 加入基本權限

        List<GrantedPermissionDTO> grantedPermissionDTOs = this.loadPermissionsByUsrId(this.appNum, username);

        // step3 - 查詢平台管理員資料
        Boolean isApplicationAdmin = this.applicationAdmins.contains(username); // 由設定檔直接設定平台管理員

        if (isApplicationAdmin == false && this.hideDbApplicationAdmin == false) { // 判斷是否隱藏資料庫平台管理員設定
            isApplicationAdmin = this.isApplicationAdminByUsrId(this.appNum, username);
        }

        // step4 - 若為平台管理員，則加入ROLE_APPLICATION_ADMIN權限
        if (isApplicationAdmin) {
            grantedAuthorities.add(new AclGrantedAuthority(ROLE_APPLICATION_ADMIN));
        }

        // step5 - 整理權限資料，並記錄權限相關的選單流水號
        List<Long> grantedMenuNums = new ArrayList<Long>(); // 記錄權限資料對應到的選單流水號

        for (int i = 0; i < grantedPermissionDTOs.size(); i++) {
            GrantedPermissionDTO grantedPermissionDTO = grantedPermissionDTOs.get(i);

            grantedMenuNums.add(grantedPermissionDTO.getMenuNum());
            grantedAuthorities.add(new AclGrantedAuthority(grantedPermissionDTO.getPerId()));
        }

        // step6 - 查詢平台選單資料
        List<AclGrantedMenu> grantedMenus = new ArrayList<AclGrantedMenu>(); // 選單資料
        List<GrantedMenuDTO> grantedMenuDTOs = this.loadMenusByAppNum(this.appNum);

        // step7 - 利用權限對應的選單流水號及選單樹狀階層，取得系統可以顯示的選單資料
        for (GrantedMenuDTO grantedMenu : grantedMenuDTOs) {
            Long menuNum = grantedMenu.getMenuNum();

            if (grantedMenu != null) {
                // 根據權限選單流水號，來判斷是否為可顯示的選單資料
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

        // step8 - 建立使用者資料，含選單及權限資料
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
        // step1 - 查詢使用者資料
        UserDetailDTO userDetailDTO = this.loadUserByUsrId(username);

        // step2 - 變更使用者密碼時，不查詢權限及選單資料
        List<AclGrantedAuthority> grantedAuthorities = new ArrayList<AclGrantedAuthority>(); // 權限資料
        grantedAuthorities.add(new AclGrantedAuthority("HOLDER")); // 加入基本權限

        List<AclGrantedMenu> grantedMenus = new ArrayList<AclGrantedMenu>(); // 選單資料

        UserDetails userDetails = this.createNewAclUserDetails(userDetailDTO, grantedAuthorities, grantedMenus);

        // step3 - 檢查帳號及密碼的狀態
        if (!userDetails.isAccountNonLocked()) {
            throw new LockedException("User account is locked", userDetails);
        }

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User is disabled", userDetails);
        }

        if (!userDetails.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired", userDetails);
        }

        // step4 - 驗證舊密碼是否合法
        Object salt = null;

        if (this.saltSource != null) {
            salt = this.saltSource.getSalt(userDetails);
        }

        if (!this.passwordEncoder.isPasswordValid(userDetails.getPassword(), oldPassword, salt)) {
            throw new BadCredentialsException("Bad old credentials", userDetails);
        }

        // step5 - 變更密碼
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
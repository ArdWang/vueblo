package com.markerhub.shiro;


import cn.hutool.core.bean.BeanUtil;
import com.markerhub.entity.User;
import com.markerhub.service.UserService;
import com.markerhub.util.JwtUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AccountRealm extends AuthorizingRealm{

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;



    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        JwtToken jwtToken = (JwtToken)token;

        String userId = jwtUtils.getClaimByToken((String)jwtToken.getPrincipal()).getSubject();

        User user =  userService.getById(Long.valueOf(userId));

        if(user == null){
            throw  new UnknownAccountException("账户不存在");
        }

        if(user.getStatus() == -1){
            throw  new LockedAccountException("账户已经锁定");
        }

        AccountProfile profile = new AccountProfile();

        BeanUtil.copyProperties(user, profile);

        System.out.print("-----进行处理-----");

        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(), getName());
    }
}
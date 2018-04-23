package org.bt2.Boot2Demo;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 用于判断admin
 * 注意：此配置文件在spring容器加载bean之前进行拦截，所以不能在spring容器中获取相关bean，本项目采用单独jdbc进行此配置
 * 文件的相关数据库操作;
 * 此配置文件每一个对应一个类型的账户验证
 */
@Configuration
public class MyShiroRealm extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(MyShiroRealm.class);

    @Override
    public String getName() {
        return "admin";
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

//        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) token;
//        if (myToken.getSignature() == null || myToken.getSignature().isEmpty()) {
//            //请从新登录;
//            log.info("令牌为空");
//            throw new UnknownAccountException();
//        }
//        ResponseResult<TokenModel> result = tokenService.getByToken(myToken.getSignature());
//        if (result.isSuccess()) {
////            如果token存在判断是否过期
//            long now_times = System.currentTimeMillis();
//            if (result.getData().getEndTimes() <= 0 || result.getData().getEndTimes() < now_times) {
////                密钥过期,请从新登录;
//                log.info("令牌过期");
//                throw new UnknownAccountException();
//            }
//
////            判断是否是作废的令牌
//            if (result.getData().getIsUse().equals("Y")) {
////                令牌已作废
//                log.info("令牌已用过");
//                throw new UnknownAccountException();
//            }
//            myToken.setUsername(result.getData().getAccount());
//            return new SimpleAuthenticationInfo(
//                    myToken,
//                    myToken.getSignature(),
//                    getName()
//            );
//        } else {
//            if (myToken.getUsername() != null && !myToken.getUsername().isEmpty()) {
//                return new SimpleAuthenticationInfo(
//                        myToken,
//                        myToken.getSignature(),
//                        getName()
//                );
//            } else
        //请从新登录;
        throw new UnknownAccountException();
//        }
    }


    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //User{id=1, username='admin', password='3ef7164d1f6167cb9f2658c07d3c2f0a', enable=1}
//        User user = (User) SecurityUtils.getSubject().getPrincipal();
//        List<Permission> permissions = permissionService.findPermissionAndRoleNameByUserId(user.getUserId());
//        List<Role> roles=permissionService.findPermissionByUserId(user.getUserId());
        // 权限信息对象info,用来存放查出的用户的所有的角色（role）及权限（permission）
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        //这里赋给两个不存在的值，使controller中的权限验证失败，验证在此失败会继续进入myShiroRealm2验证权限
//        info.addRole("admin");
//        for(Permission permission: permissions){
//            System.out.println("permission:"+permission.getPermission());
//            System.out.println("permission.getRoleName():"+permission.getRoleName());
//            info.addStringPermission(permission.getPermission());
//            info.addRole(permission.getRoleName());
//        }
        return info;
    }

}
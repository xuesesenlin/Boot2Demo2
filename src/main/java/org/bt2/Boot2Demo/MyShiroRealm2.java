package org.bt2.Boot2Demo;

import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.bt2.Boot2Demo.publics.login.model.LoginModel;
import org.bt2.Boot2Demo.publics.login.service.AccountService;
import org.bt2.Boot2Demo.publics.login.service.impl.AccountServiceImpl;
import org.bt2.Boot2Demo.publics.token.model.TokenModel;
import org.bt2.Boot2Demo.publics.token.service.TokenService;
import org.bt2.Boot2Demo.publics.token.service.impl.TokenServiceImpl;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 * 用于判断user
 * 注意：此配置文件在spring容器加载bean之前进行拦截，所以不能在spring容器中获取相关bean，本项目采用单独jdbc进行此配置
 * 文件的相关数据库操作
 * 此配置文件每一个对应一个类型的账户验证
 */
@Configuration
public class MyShiroRealm2 extends AuthorizingRealm {

    private static final Logger log = LoggerFactory.getLogger(MyShiroRealm2.class);

    @Override
    public String getName() {
        return "user";
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        MyUsernamePasswordToken myToken = (MyUsernamePasswordToken) token;
        if (myToken.getSignature() == null || myToken.getSignature().isEmpty()) {
            //请从新登录;
            log.info("令牌为空");
            throw new UnknownAccountException();
        }
        TokenService tokenService = new TokenServiceImpl();
        ResponseResult<TokenModel> result = tokenService.getByToken2(myToken.getSignature());
        if (result.isSuccess()) {
//            如果token存在判断是否过期
            long now_times = System.currentTimeMillis();
            if (result.getData().getEndTimes() <= 0 || result.getData().getEndTimes() < now_times) {
//                密钥过期,请从新登录;
                log.info("令牌过期");
                throw new UnknownAccountException("令牌过期");
            }

//            判断是否是作废的令牌
            if (result.getData().getIsUse().equals("Y")) {
//                令牌已作废
                log.info("令牌已用过");
                throw new UnknownAccountException("令牌已作废");
            }
            myToken.setUsername(result.getData().getAccount());
            return new SimpleAuthenticationInfo(
                    myToken,
                    myToken.getSignature(),
                    getName()
            );
        } else {
            if (myToken.getUsername() != null && !myToken.getUsername().isEmpty()) {
                AccountService accountService = new AccountServiceImpl();
                ResponseResult<LoginModel> account = accountService.getByAccount2(myToken.getUsername(), myToken.getType());
                if (account.isSuccess())
                    return new SimpleAuthenticationInfo(
                            account.getData(),
                            account.getData().getPassword(),
                            getName()
                    );
                else
                    throw new UnknownAccountException("账号或密码错误");
            } else
                //请从新登录;
                throw new UnknownAccountException("账户丢失，请从新登录");
        }
    }

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRole("user");
        return info;
    }

}
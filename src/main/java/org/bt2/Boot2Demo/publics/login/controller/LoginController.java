package org.bt2.Boot2Demo.publics.login.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.SessionException;
import org.apache.shiro.subject.Subject;
import org.bt2.Boot2Demo.MyUsernamePasswordToken;
import org.bt2.Boot2Demo.publics.login.model.LoginModel;
import org.bt2.Boot2Demo.publics.token.model.TokenModel;
import org.bt2.Boot2Demo.publics.token.service.TokenService;
import org.bt2.Boot2Demo.util.base64.Base64Util;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;
import org.bt2.Boot2Demo.util.uuidUtil.GetUuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * login
 */
@Api(value = "login", description = "登录")
@RestController
@RequestMapping("/login")
public class LoginController {
    private final static Logger log = LoggerFactory
            .getLogger(LoginController.class);

    @Autowired
    private TokenService tokenService;

    /**
     * @param model         LoginModel
     * @param bindingResult BindingResult
     * @return ResponseResult<LoginModel>
     */
    @ApiOperation(value = "登录",
            response = ResponseResult.class,
            httpMethod = "POST",
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @RequestMapping(value = "/login",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseResult<LoginModel> login(
            @Valid @RequestBody LoginModel model,
            BindingResult bindingResult,
            HttpServletResponse response) {

        ResponseResult<LoginModel> result = new ResponseResult<>();
        if (bindingResult.hasErrors()) {
            result.setSuccess(false);
            result.setMessage(bindingResult.getFieldError().getDefaultMessage());
            return result;
        }
        String types = "user";
        if (model.getTypes() == 1)
            types = "cus";
        if (model.getTypes() == 2)
            types = "admin";

        //验证用户和令牌的有效性
        MyUsernamePasswordToken token = new MyUsernamePasswordToken(model.getUsername(),
//此处决定此方法只能用于普通用户
                types,
                Base64Util.encode(model.getPassword()));
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
            log.info("获取令牌成功");
//        生成新的token
            long times = System.currentTimeMillis() + (1000 * 60 * 15);
            TokenModel tokenModel = new TokenModel();
            tokenModel.setToken(GetUuid.getUUID());
            tokenModel.setIsUse("N");
            tokenModel.setEndTimes(times);
            tokenModel.setAccount(model.getUsername());
            tokenModel.setUuid(GetUuid.getUUID());
            ResponseResult<TokenModel> result1 = tokenService.add(tokenModel);
            if (result1.isSuccess()) {
//                用cookie方式
//                Cookie cookie = new Cookie("token", tokenModel.getToken());
//                cookie.setPath("/");
//                cookie.setMaxAge(60);
//                response.addCookie(cookie);
//                用头部信息方式
                response.setHeader("Access-Control-Expose-Headers", "token");
                response.setHeader("token", tokenModel.getToken());
                result.setSuccess(true);
                result.setMessage(tokenModel.getToken());
                return result;
            } else {
                result.setSuccess(false);
                result.setMessage("令牌生成错误");
                return result;
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            result.setSuccess(false);
            result.setMessage("账号或密码错误");
            return result;
        }
    }

    @GetMapping("/logout")
    public void logout(HttpServletRequest request,
                       HttpServletResponse response) {
        try {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("token")) {
                    tokenService.updateToken(cookies[i].getValue());
                    Cookie cookie = new Cookie("token", null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
            Subject subject = SecurityUtils.getSubject();
            if (subject.isAuthenticated()) {
                subject.logout();
                if (log.isDebugEnabled()) {
                    log.debug("用户退出登录");
                }
            }
        } catch (SessionException e) {
            e.printStackTrace();
        }
    }

}

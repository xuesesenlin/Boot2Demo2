package org.bt2.Boot2Demo;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.bt2.Boot2Demo.publics.token.model.TokenModel;
import org.bt2.Boot2Demo.publics.token.service.TokenService;
import org.bt2.Boot2Demo.publics.token.service.impl.TokenServiceImpl;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;
import org.bt2.Boot2Demo.util.uuidUtil.GetUuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;

/**
 * @author Created by pangkunkun on 2017/11/18.
 */
public class MyAccessControlFilter extends AccessControlFilter {

    private static final Logger log = LoggerFactory.getLogger(MyAccessControlFilter.class);

    /**
     * 表示是否允许访问；mappedValue就是[urls]配置中拦截器参数部分，如果允许访问返回true，否则false；
     * (感觉这里应该是对白名单（不需要登录的接口）放行的)
     * 如果isAccessAllowed返回true则onAccessDenied方法不会继续执行
     * 这里可以用来判断一些不被通过的链接（个人备注）
     * * 表示是否允许访问 ，如果允许访问返回true，否则false；
     *
     * @param servletRequest
     * @param servletResponse
     * @param object          表示写在拦截器中括号里面的字符串 mappedValue 就是 [urls] 配置中拦截器参数部分
     * @return
     * @throws Exception
     */
    @Override
    public boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object object) throws Exception {
        Subject subject = getSubject(servletRequest, servletResponse);
        String url = getPathWithinApplication(servletRequest);
        log.info("当前用户正在访问的 url => " + url);
        log.info("subject.isPermitted(url);" + subject.isPermitted(url));
        return false;
    }

    /**
     * 表示当访问拒绝时是否已经处理了；如果返回true表示需要继续处理；如果返回false表示该拦截器实例已经处理了，将直接返回即可。
     * onAccessDenied是否执行取决于isAccessAllowed的值，如果返回true则onAccessDenied不会执行；如果返回false，执行onAccessDenied
     * 如果onAccessDenied也返回false，则直接返回，不会进入请求的方法（只有isAccessAllowed和onAccessDenied的情况下）
     */
    @Override
    public boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

//        获取所有请求的参数
        Enumeration enu = request.getParameterNames();
        while (enu.hasMoreElements()) {
            String paraName = (String) enu.nextElement();
            String s = request.getParameter(paraName).replace("共产党", "#");
            request.setAttribute(paraName, s);
        }

        String token_str = httpServletRequest.getHeader("token");
        if (token_str == null || token_str.trim().equals("")) {
            log.info("未获取头部信息");
            onLoginFail(response, "非法的请求");
            return false;
        }
//验证用户和令牌的有效性(此处应该根据uuid取缓存数据然后判断令牌时候有效)
        MyUsernamePasswordToken token = new MyUsernamePasswordToken(null, "user", token_str);
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (Exception e) {
            log.info("令牌验证失败，令牌错误");
            log.info(e.getMessage());
            onLoginFail(response, e.getMessage());
            return false;
        }
        log.info("令牌验证成功");

//        废弃原有令牌
        TokenService tokenService = new TokenServiceImpl();
        ResponseResult<TokenModel> result1 = tokenService.updateToken2(token_str);
        if (result1.isSuccess()) {
//        新的token
//        保存进库
            TokenModel tokenModel = new TokenModel();
            tokenModel.setEndTimes(System.currentTimeMillis() * (1000 * 60 * 60 * 24));
            tokenModel.setIsUse("N");
            tokenModel.setToken(GetUuid.getUUID());
            tokenModel.setUuid(GetUuid.getUUID());
            ResponseResult<TokenModel> result = tokenService.add2(tokenModel);
            if (result.isSuccess()) {
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader("Access-Control-Expose-Headers", "token");
                httpServletResponse.setHeader("token", tokenModel.getToken());
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    /**
     * 登录失败
     */
    private void onLoginFail(ServletResponse response, String message) throws IOException {
        log.info("设置返回");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Content-type", "application/json; charset=utf-8");
        httpResponse.setHeader("Access-Control-Expose-Headers", "token");
        httpResponse.setHeader("token", "logout");
        httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ResponseResult result = new ResponseResult();
        result.setSuccess(false);
        result.setMessage(message);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jsonGenerator = objectMapper.getFactory()
                .createGenerator(System.out, JsonEncoding.UTF8);

        //对象转JSON
        String json = objectMapper.writeValueAsString(result);//返回字符串，输出
        httpResponse.getWriter().append(json);
//        httpResponse.sendRedirect("/index");
    }

    /**
     * TODO 跨域请求
     */
    private void dealCrossDomain() {

    }
}

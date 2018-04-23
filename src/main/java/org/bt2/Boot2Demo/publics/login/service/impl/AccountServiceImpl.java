package org.bt2.Boot2Demo.publics.login.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.bt2.Boot2Demo.publics.login.mapper.AccountMapper;
import org.bt2.Boot2Demo.publics.login.model.LoginModel;
import org.bt2.Boot2Demo.publics.login.service.AccountService;
import org.bt2.Boot2Demo.util.base64.Base64Util;
import org.bt2.Boot2Demo.util.jdbc.JDBC;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;
import org.bt2.Boot2Demo.util.sl4j.Sl4jToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private static Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Resource
    private AccountMapper mapper;

    @Override
    public ResponseResult<LoginModel> add(LoginModel model) {
        ResponseResult<LoginModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                model.toString(),
                200,
                null));
        LoginModel model1 = mapper.getByUsername(model.getUsername());
        if (model1 != null) {
            result.setSuccess(false);
            result.setMessage("账号重复");
            result.setData(null);
        } else {
            try {
                model.setPassword(Base64Util.encode(model.getPassword()));
            } catch (Exception e) {
                result.setSuccess(false);
                result.setMessage("创建账号失败");
                result.setData(null);
                return result;
            }
            int i = mapper.add(model);
            switch (i) {
                case 1:
                    result.setSuccess(true);
                    result.setMessage("创建账号成功");
                    result.setData(model);
                    break;
                default:
                    result.setSuccess(false);
                    result.setMessage("创建账号失败");
                    result.setData(null);
                    break;
            }
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                200,
                result.getMessage()));
        return result;
    }

    @Override
    public ResponseResult<LoginModel> putPWD(String account, String password) {
        ResponseResult<LoginModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                account + "," + password,
                200,
                null));
        int i = mapper.putPWD(account, password);
        switch (i) {
            case 1:
                result.setSuccess(true);
                result.setMessage("密码修改成功");
                result.setData(null);
                break;
            default:
                result.setSuccess(false);
                result.setMessage("密码修改失败");
                result.setData(null);
                break;
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                result.getCode(),
                result.getMessage()));
        return result;
    }

    @Override
    public ResponseResult<LoginModel> getById(String id) {
        ResponseResult<LoginModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                id,
                200,
                null));
        LoginModel model = mapper.getByUsername(id);
        if (model != null) {
            result.setSuccess(true);
            result.setMessage(null);
            result.setData(model);
        } else {
            result.setSuccess(false);
            result.setMessage("账户未找到");
            result.setData(null);
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                200,
                result.getMessage()));
        return result;
    }

    @Override
    public ResponseResult<LoginModel> getByAccount(String account) {
        ResponseResult<LoginModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                account,
                200,
                null));
        LoginModel model = mapper.getByUsername(account);
        if (model != null) {
            result.setSuccess(true);
            result.setData(model);
        } else {
            result.setSuccess(false);
            result.setData(null);
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                result.getCode(),
                result.getMessage()));
        return result;
    }

    @Override
    public ResponseResult<LoginModel> getByAccount2(String account, String types) {
        int type = 0;
        if (types.equals("cus"))
            type = 1;
        if (types.equals("admin"))
            type = 2;
        ResponseResult<LoginModel> result = new ResponseResult<>();
        JDBC jdbc = new JDBC();
        List<LoginModel> models = jdbc.queryToken2("select * from account_table where username = '" + account + "'" +
                " and types = " + type);
        if (models.size() > 0) {
            result.setSuccess(true);
            result.setData(models.get(0));
        } else
            result.setSuccess(false);
        return result;
    }

    @Override
    public ResponseResult<Page<LoginModel>> findAllPage(int pageNow, int pageSize, String type, String account) {
        ResponseResult<Page<LoginModel>> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                pageNow + "," + pageSize + "," + type,
                200,
                null));
        PageHelper.startPage(pageNow, pageSize);
        Page<LoginModel> page = mapper.findAllPage(type, account);
        result.setSuccess(true);
        result.setData(page);
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                result.getCode(),
                null));
        return result;
    }

    @Override
    public ResponseResult<LoginModel> del(String id) {
        ResponseResult<LoginModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                id,
                200,
                null));
        int i = mapper.del(id);
        switch (i) {
            case 1:
                result.setSuccess(true);
                result.setMessage("删除账号成功");
                result.setData(null);
                break;
            default:
                result.setSuccess(false);
                result.setMessage("删除账号失败");
                result.setData(null);
                break;
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                result.getCode(),
                result.getMessage()));
        return result;
    }
}

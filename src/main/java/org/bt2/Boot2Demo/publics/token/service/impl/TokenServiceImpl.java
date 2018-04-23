package org.bt2.Boot2Demo.publics.token.service.impl;

import org.bt2.Boot2Demo.publics.token.mapper.TokenMapper;
import org.bt2.Boot2Demo.publics.token.model.TokenModel;
import org.bt2.Boot2Demo.publics.token.service.TokenService;
import org.bt2.Boot2Demo.util.jdbc.JDBC;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;
import org.bt2.Boot2Demo.util.sl4j.Sl4jToString;
import org.bt2.Boot2Demo.util.uuidUtil.GetUuid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private static Logger logger = LoggerFactory.getLogger(TokenServiceImpl.class);

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    private TokenMapper mapper;

    @Override
    public ResponseResult<TokenModel> add(TokenModel model) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        model.setUuid(GetUuid.getUUID());
        int i = mapper.add(model);
        switch (i) {
            case 1:
                result.setSuccess(true);
                result.setData(model);
                break;
            default:
                result.setSuccess(false);
                result.setData(null);
                break;
        }
        return result;
    }

    @Override
    public ResponseResult<TokenModel> add2(TokenModel model) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        JDBC jdbc = new JDBC();
        int i = jdbc.update("insert into token_table (uuid,account,token,end_time,is_use)" +
                " values ('" + GetUuid.getUUID() + "','" + model.getAccount() + "','" + model.getToken()
                + "'," + model.getEndTimes() + ",'" + model.getIsUse() + "')");
        if (i > 0) {
            result.setSuccess(true);
        } else
            result.setSuccess(false);
        return result;
    }

    @Override
    public ResponseResult updateToken(String token) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                token,
                200,
                null));
        int i = mapper.updateToken(token);
        switch (i) {
            case 1:
                result.setSuccess(true);
                result.setData(null);
                break;
            default:
                result.setSuccess(false);
                result.setData(null);
                break;
        }
        logger.info(Sl4jToString.info(2,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                result.toString(),
                result.getCode(),
                null));
        return result;
    }

    @Override
    public ResponseResult updateToken2(String token) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        JDBC jdbc = new JDBC();
        int i = jdbc.update("update token_table set is_use = 'Y' where token = '" + token + "'");
        if (i > 0) {
            result.setSuccess(true);
        } else
            result.setSuccess(false);
        return result;
    }

    @Override
    public ResponseResult getByToken(String token) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        logger.info(Sl4jToString.info(1,
                serviceName,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                token,
                200,
                null));
        TokenModel model = this.mapper.getByToken(token);
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
    public ResponseResult<TokenModel> getByToken2(String token) {
        ResponseResult<TokenModel> result = new ResponseResult<>();
        JDBC jdbc = new JDBC();
        List<TokenModel> models = jdbc.queryToken("select uuid,account,token,end_time endTimes,is_use isUse from token_table where token='"
                + token + "' ORDER BY end_time desc LIMIT 1");

        if (models.size() > 0) {
            result.setSuccess(true);
            result.setData(models.get(0));
        } else
            result.setSuccess(false);
        return result;
    }
}

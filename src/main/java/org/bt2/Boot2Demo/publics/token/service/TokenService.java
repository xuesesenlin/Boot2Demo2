package org.bt2.Boot2Demo.publics.token.service;


import org.bt2.Boot2Demo.publics.token.model.TokenModel;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;

public interface TokenService {

    ResponseResult<TokenModel> add(TokenModel model);

    ResponseResult<TokenModel> add2(TokenModel model);

    ResponseResult<TokenModel> updateToken(String token);

    ResponseResult<TokenModel> updateToken2(String token);

    ResponseResult<TokenModel> getByToken(String token);

    ResponseResult<TokenModel> getByToken2(String token);
}

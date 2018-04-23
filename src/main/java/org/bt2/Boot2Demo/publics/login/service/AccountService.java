package org.bt2.Boot2Demo.publics.login.service;

import com.github.pagehelper.Page;
import org.bt2.Boot2Demo.publics.login.model.LoginModel;
import org.bt2.Boot2Demo.util.resultJson.ResponseResult;

/**
 * @name 账户接口
 */
public interface AccountService {
    /**
     * 新增账户
     *
     * @param model
     * @return int
     */
    ResponseResult<LoginModel> add(LoginModel model);

    /**
     * 修改密码
     *
     * @param account
     * @param password
     * @return
     */
    ResponseResult<LoginModel> putPWD(String account, String password);

    /**
     * 根据id获取实体
     *
     * @param id
     * @return
     */
    ResponseResult<LoginModel> getById(String id);

    /**
     * 根据账户获取实体
     *
     * @param account
     * @return
     */
    ResponseResult<LoginModel> getByAccount(String account);

    ResponseResult<LoginModel> getByAccount2(String account, String types);

    /**
     * 根据类型获取账户
     *
     * @return
     */
    ResponseResult<Page<LoginModel>> findAllPage(int pageNow, int pageSize, String type, String account);

    /**
     * 根据id删除实体
     *
     * @param id
     * @return
     */
    ResponseResult<LoginModel> del(String id);

}

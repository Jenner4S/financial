package com.zhou.api;

import com.googlecode.jsonrpc4j.JsonRpcService;
import com.zhou.api.domain.ParamInf;
import com.zhou.entity.Product;

import java.util.List;

/**
 * 产品相关的rpc服务
 */
@JsonRpcService
public interface ProductRpc {
    /**
     * 查询多个产品
     * @param req
     * @return
     */
    List<Product> query(ParamInf req);

    /**
     * 查询单个产品
     * @param id
     * @return
     */
    Product findOne(String id);
}

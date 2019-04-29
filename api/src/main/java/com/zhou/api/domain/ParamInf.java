package com.zhou.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.math.BigDecimal;
import java.util.List;

@JsonDeserialize(as = ProductRpcReq.class)
public interface ParamInf {
     List<String> getIdList();
     BigDecimal getMinRewardRate();
     BigDecimal getMaxRewardRate();
     List<String> getStatusList();
}

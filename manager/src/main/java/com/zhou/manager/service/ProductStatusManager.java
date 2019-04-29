package com.zhou.manager.service;

import com.zhou.api.events.ProductStatusEvent;
import com.zhou.entity.enums.ProductStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * 管理产品状态
 */
@Component
public class ProductStatusManager {
    private static final String MQ_DESTINATION = "VirtualTopic.PRODUCT_STATUS";
    private static final Logger LOG = LoggerFactory.getLogger(ProductStatusManager.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void changeStatus(String id, ProductStatus status){
        ProductStatusEvent event = new ProductStatusEvent(id, status);
        LOG.info("send message:{}",event);
        jmsTemplate.convertAndSend(MQ_DESTINATION, event);
    }
    //@PostConstruct
    public void init(){
        changeStatus("001", ProductStatus.IN_SELL);
    }
}

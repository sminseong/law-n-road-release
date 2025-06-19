package com.lawnroad.payment.mapper;

import com.lawnroad.payment.model.WebhookVO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WebhookMapper {
    void insert(WebhookVO log);
}

package com.nls.paymentservice.shared.mapper;

import com.nls.paymentservice.api.dto.response.PayOSRes;
import com.nls.paymentservice.domain.entity.PaymentLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orderCode", ignore = true)
    PaymentLog convertPayOSResToPaymentLog(PayOSRes res);

}

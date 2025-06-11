package com.nls.paymentservice.shared.mapper;

import com.nls.paymentservice.api.dto.response.PayOSRes;
import com.nls.paymentservice.domain.entity.PaymentLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    PaymentLog convertPayOSResToPaymentLog(PayOSRes res);

}

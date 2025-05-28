package com.nls.paymentservice.application;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;

import java.util.Map;
import java.util.UUID;

public interface IVnpayGateway {

    String createPaymentUrl(CreatePaymentReq request, UUID paymentId);

    VnpayValidationResult handleVnpayIPN(Map<String, String> params);

}

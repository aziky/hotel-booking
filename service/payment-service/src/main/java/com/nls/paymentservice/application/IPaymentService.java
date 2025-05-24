package com.nls.paymentservice.application;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;

import java.util.Map;

public interface IPaymentService {

    ApiResponse<CreatePaymentRes> createPayment(CreatePaymentReq request);

    String handleVnpResponse(Map<String, String> params);

}

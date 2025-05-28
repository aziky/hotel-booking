package com.nls.paymentservice.api.controller;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.common.dto.response.ApiResponse;
import com.nls.common.dto.response.CreatePaymentRes;
import com.nls.paymentservice.application.IPaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("payment")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    IPaymentService paymentService;

    @PostMapping
    public ApiResponse<CreatePaymentRes> createPayment(@RequestBody CreatePaymentReq request) {
        return paymentService.createPayment(request);
    }

    @GetMapping("/IPN/vnpay")
    public String handleVnpayIPN(@RequestParam Map<String, String> params) {
        return "redirect:" + paymentService.handleVnpResponse(params);
    }
}

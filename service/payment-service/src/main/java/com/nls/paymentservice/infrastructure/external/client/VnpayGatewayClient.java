package com.nls.paymentservice.infrastructure.external.client;

import com.nls.common.dto.request.CreatePaymentReq;
import com.nls.paymentservice.application.IVnpayGateway;
import com.nls.paymentservice.infrastructure.config.VnpayConfig;
import com.nls.paymentservice.infrastructure.external.dto.response.VnpayValidationResult;
import com.nls.paymentservice.infrastructure.properties.VnpayProperties;
import com.nls.paymentservice.infrastructure.properties.WebUrlProperties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VnpayGatewayClient implements IVnpayGateway {

    VnpayProperties vnpayProperties;
    VnpayConfig vnpayConfig;
    WebUrlProperties webUrlProperties;

    @Override
    public String createPaymentUrl(CreatePaymentReq request, UUID paymentId) {
        try {

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());

            String amount = String.valueOf(request.totalAmount().multiply(BigDecimal.valueOf(100)).intValue());

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", vnpayProperties.vnpVersion());
            vnp_Params.put("vnp_Command", vnpayProperties.vnpCommand());
            vnp_Params.put("vnp_TmnCode", vnpayProperties.vnpTmpCode());
            vnp_Params.put("vnp_Amount", amount);
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_IpAddr", getClientIp());
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", vnpayProperties.responseHost() + vnpayProperties.vnpayReturnUrl());
            vnp_Params.put("vnp_OrderType", "other");
            vnp_Params.put("vnp_TxnRef", paymentId.toString());
            vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + paymentId);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    //Build hash data
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    //Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            String vnp_SecureHash = vnpayConfig.hmacSHA512(vnpayProperties.secretKey(), hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            return vnpayProperties.vnpayUrl() + "?" + query;
        } catch (Exception e) {
            log.info("Error at create payment url cause by {}", e.getMessage());
            return null;
        }
    }

    @Override
    public VnpayValidationResult handleVnpayIPN(Map<String, String> params) {
        try {
            log.info("Star handle vpn ipn with request: {}", params);
            String vnpSecureHash = params.get("vnp_SecureHash");

            Map<String, String> fieldsToHash = new HashMap<>(params);
            fieldsToHash.remove("vnp_SecureHash");
            fieldsToHash.remove("vnp_SecureHashType");

            String signValue = vnpayConfig.hashAllFields(fieldsToHash);

//            if (!signValue.equalsIgnoreCase(vnpSecureHash)) {
//                log.warn("Invalid signature: expected {}, got {}", signValue, vnpSecureHash);
//                return new VnpayValidationResult(false, webUrlProperties.host() + webUrlProperties.paymentFail());
//            }

            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                log.info("Payment successful with paymentId: {}", params.get("vnp_TxnRef"));
                return new VnpayValidationResult(true, webUrlProperties.host() + webUrlProperties.paymentSuccess());
            } else {
                log.info("Payment failed with code: {}", responseCode);
                return new VnpayValidationResult(false,webUrlProperties.host() + webUrlProperties.paymentFail() );
            }

        } catch (Exception e) {
            log.error("Error at handle vnpay ipn: {}", e.getMessage());
            return new VnpayValidationResult(false, webUrlProperties.host() + webUrlProperties.paymentFail());
        }
    }

    private String getClientIp() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress();
        } catch (UnknownHostException e) {
            return "127.0.0.1";
        }
    }

}

package com.nls.common.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PropertyRes {
    private UUID id;
    private String name;
    private String address;
    private String city;
    private String country;
}
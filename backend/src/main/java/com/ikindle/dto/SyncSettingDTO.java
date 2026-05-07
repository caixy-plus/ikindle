package com.ikindle.dto;

import lombok.Data;

@Data
public class SyncSettingDTO {
    private Long id;
    private Long userId;
    private String kindleEmail;
    private Boolean autoSync;
    private String priority;
    private String preferredFormat;
}

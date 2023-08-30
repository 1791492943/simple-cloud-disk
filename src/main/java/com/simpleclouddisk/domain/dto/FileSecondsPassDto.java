package com.simpleclouddisk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileSecondsPassDto {

    private String fileName;
    private String minioName;
    private Long filePid;

}

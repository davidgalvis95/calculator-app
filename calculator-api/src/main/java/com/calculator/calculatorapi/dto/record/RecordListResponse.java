package com.calculator.calculatorapi.dto.record;

import com.calculator.calculatorapi.dto.record.RecordDto;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class RecordListResponse {
    int page;
    int totalPages;
    long totalRecords;
    String nextPageToken;
    String prevPageToken;
    List<RecordDto> records;
}

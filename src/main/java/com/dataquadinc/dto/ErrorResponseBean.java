package com.dataquadinc.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class  ErrorResponseBean {
    public boolean success;
    public String message;
    public String error;
    public Map<String, String> fieldErrors;
}

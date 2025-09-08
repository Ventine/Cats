// dto/CryptoResponseDto.java
package com.cats.Proyect_Cats.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String message;
    private String detail;
}

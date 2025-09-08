// dto/CryptoResponseDto.java
package com.cats.Proyect_Cats.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;

@Getter
@Setter
@Data
public class CryptoResponseDto {
    private String id;
    private String symbol;
    private String name;
    private double currentPrice;
}

package com.kabutar.keyfort.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RefreshTokenDto {
    private String grantType;
    private String refreshToken;
    private String clientId;
}

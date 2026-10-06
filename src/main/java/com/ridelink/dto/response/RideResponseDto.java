package com.ridelink.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RideResponseDto {
    private String id;
    private String riderId;
    private double distance;
    private String status;
}

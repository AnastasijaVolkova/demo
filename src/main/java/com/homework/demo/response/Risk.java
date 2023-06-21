package com.homework.demo.response;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Risk {

    private String riskType;
    private double sumInsured;
    private double premium;

    public Risk(String riskType) {
        this.riskType = riskType;
    }

}

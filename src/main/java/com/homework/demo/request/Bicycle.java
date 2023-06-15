package com.homework.demo.request;

import com.homework.demo.enums.Coverage;
import com.homework.demo.enums.RiskType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
public class Bicycle {

    @NotNull
    private String make;
    @NotNull
    private String model;
    private Coverage coverage = Coverage.STANDARD;
    @NotNull
    private Integer manufactureYear;
    @NotNull
    private Double sumInsured;
    private Set<RiskType> risks = Collections.singleton(RiskType.THEFT);

}

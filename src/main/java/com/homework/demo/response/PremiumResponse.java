package com.homework.demo.response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PremiumResponse {

    private List<CalculationObject> objects;
    private double premium;

    public void initObjects() {
        this.objects = new ArrayList<>();
    }

}

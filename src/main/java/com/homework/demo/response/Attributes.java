package com.homework.demo.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.homework.demo.request.Bicycle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Attributes {

    @JsonProperty("MANUFACTURE_YEAR")
    private int manufactureYear;
    @JsonProperty("MODEL")
    private String model;
    @JsonProperty("MAKE")
    private String make;

    public static Attributes init(Bicycle bicycle) {
        return new Attributes(bicycle.getManufactureYear(), bicycle.getModel(), bicycle.getMake());
    }

}

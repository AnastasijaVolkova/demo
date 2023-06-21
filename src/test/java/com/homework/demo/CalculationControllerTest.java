package com.homework.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.demo.enums.Coverage;
import com.homework.demo.request.Bicycle;
import com.homework.demo.request.SumInsuredAndPremiumsCalculationRequest;
import com.homework.demo.response.PremiumResponse;
import com.homework.demo.service.CalculationService;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CalculationController.class)
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CalculationService service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void calculatePremiumsAndInsuredSumTest() throws Exception {
        PremiumResponse response = new PremiumResponse();
        response.initObjects();
        response.setPremium(112.04);
        when(service.calculateSumInsuredAndPremiums(any())).thenReturn(response);

        MvcResult result = mockMvc.perform(post("/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"bicycles\" : [ {\"make\" : \"Pearl\", \"model\" : \"Gravel SL EVO\", " +
                                "\"coverage\" : \"EXTRA\", \"manufactureYear\" : 2015, " +
                                "\"sumInsured\" : 1000, " +
                                "\"risks\" : [ \"THEFT\", \"DAMAGE\", \"THIRD_PARTY_DAMAGE\"]}]}"))
                .andExpect(status().isOk())
                .andReturn();

        SumInsuredAndPremiumsCalculationRequest request = new SumInsuredAndPremiumsCalculationRequest();
        Bicycle bicycle = new Bicycle();
        bicycle.setMake("Pearl");
        bicycle.setModel("Gravel SL EVO");
        bicycle.setCoverage(Coverage.EXTRA);
        bicycle.setManufactureYear(2015);
        bicycle.setSumInsured(1000.0);
        bicycle.setRisks(Sets.newSet("THEFT", "DAMAGE", "THIRD_PARTY_DAMAGE"));
        request.setBicycles(Collections.singletonList(bicycle));

        assertEquals(result.getResponse().getContentAsString(), "{\"objects\":[],\"premium\":112.04}");
        verify(service).calculateSumInsuredAndPremiums(request);
    }

}
package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyResponse {
	 private String fromCurrency;
	    private String toCurrency;
	    private double rate;
	    private double convertedAmount;

}

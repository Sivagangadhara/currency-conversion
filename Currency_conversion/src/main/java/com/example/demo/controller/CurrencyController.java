package com.example.demo.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.demo.Entity.ConversionHistory;
import com.example.demo.dtos.CurrencyRequest;
import com.example.demo.dtos.CurrencyResponse;
import com.example.demo.repository.ConversionRepository;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    @Autowired
    private ConversionRepository conversionRepository;

    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestBody CurrencyRequest request) {
        try {
            String from = request.getFromCurrency().toUpperCase();
            String to = request.getToCurrency().toUpperCase();
            double amount = request.getAmount();

            // API URL (using open.er-api.com)
            String url = "https://open.er-api.com/v6/latest/" + from;

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);

            if (response == null || !"success".equals(response.get("result"))) {
                return ResponseEntity.badRequest().body("Error fetching exchange rate. Check currency codes.");
            }

            Map<String, Double> rates = (Map<String, Double>) response.get("rates");
            if (rates == null || !rates.containsKey(to)) {
                return ResponseEntity.badRequest().body("Invalid target currency code: " + to);
            }

            double rate = rates.get(to);
            double convertedAmount = amount * rate;

            // ✅ Save the conversion in the database
            ConversionHistory history = new ConversionHistory();
            history.setFromCurrency(from);
            history.setToCurrency(to);
            history.setAmount(amount);
            history.setRate(rate);
            history.setConvertedAmount(convertedAmount);
            history.setTimestamp(LocalDateTime.now());

            conversionRepository.save(history);

            // ✅ Prepare the response object
            CurrencyResponse currencyResponse =
                    new CurrencyResponse(from, to, rate, convertedAmount);

            return ResponseEntity.ok(currencyResponse);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
}

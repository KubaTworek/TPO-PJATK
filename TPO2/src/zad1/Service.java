/**
 * @author Tworek Jakub S25646
 */

package zad1;


import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.*;

public class Service {
    private static final String WEATHER_API_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String EXCHANGE_RATE_API_BASE_URL = "https://api.exchangerate.host/convert";
    private static final String NBP_API_BASE_URL = "http://api.nbp.pl/api/exchangerates/rates";
    private final Locale locale;
    private final ObjectMapper mapper = new ObjectMapper();

    public Service(String country) {
        this.locale = getLocaleFromNameOfCountry(country);
    }

    private Locale getLocaleFromNameOfCountry(String country) {
        return Arrays.stream(Locale.getAvailableLocales())
                .filter(locale -> country.equalsIgnoreCase(locale.getDisplayCountry(Locale.ENGLISH)))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid country name"));
    }

    public String getWeather(String city) {
        String url = String.format("%s?q=%s,%s&appid=%s&units=metric", WEATHER_API_BASE_URL, city.replaceAll("\\s", ""), locale.getCountry(), getWeatherApiKey());
        return executeHttpGetRequest(url);
    }

    public Double getRateFor(String currency) {
        String baseCurrencyCode = getCurrencyCode(this.locale);
        String jsonString = executeHttpGetRequest(String.format("%s?from=%s&to=%s", EXCHANGE_RATE_API_BASE_URL, currency, baseCurrencyCode));
        JsonNode json = null;
        try {
            json = mapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Optional.ofNullable(json.get("info"))
                .map(info -> info.get("rate"))
                .map(JsonNode::asDouble)
                .orElseThrow(() -> new IllegalArgumentException("Invalid currency"));
    }

    public Double getNBPRate() {
        Double rate = null;
        for (String table : Arrays.asList("a", "b", "c")) {
            rate = getExchangeRateFromNBP(table);
            if (rate != null) {
                break;
            }
        }
        return Optional.ofNullable(rate)
                .orElseThrow(() -> new IllegalArgumentException("Failed to get NBP rate"));
    }

    private Double getExchangeRateFromNBP(String table) {
        String currencyCode = getCurrencyCode(locale);

        if (currencyCode == null) {
            return null;
        }

        if ("PLN".equals(currencyCode)) {
            return 1d;
        }

        String url = String.format("%s/%s/%s/", NBP_API_BASE_URL, table, currencyCode);

        try (InputStream inputStream = new URL(url).openStream()) {
            String jsonString = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining());

            JsonNode json = mapper.readTree(jsonString);
            JsonNode rates = json.at("/rates/0/mid");

            return rates.asDouble();

        } catch (IOException e) {
            System.err.println("An error occurred while getting currency rate: " + e.getMessage());
            return null;
        }
    }

    private String getCurrencyCode(Locale locale) {
        Currency currency = Currency.getInstance(locale);
        return currency.getCurrencyCode();
    }

    private String executeHttpGetRequest(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();

            try (InputStream inputStream = connection.getInputStream()) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                return bufferedReader.lines().collect(Collectors.joining());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String getWeatherApiKey() {
        return "71170abf768266d0272d64a7cb0f7ba4";
    }
}
package com.iotfarmproject.iotfarmproject.analytics_reporting.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    QueryApi queryApi;
    List<FluxTable> tables;

    @Autowired
    public AnalyticsService(InfluxDBClient influxDBClient) {
        try {
            String bucket = "mybucket";

            String flux = String.format("from(bucket: \"%s\")" +
                    "  |> range(start:0)\n" +
                    "  |> filter(fn: (r) => r[\"_measurement\"] == \"sensors_data\")" +
                    "  |> filter(fn: (r) => r[\"_field\"] == \"humidity\" or r[\"_field\"] == \"temperature\")" +
                    "  |> yield(name: \"mean\")", bucket);

            queryApi = influxDBClient.getQueryApi();
            tables = queryApi.query(flux);

            PrintHumidity();
            PrintTemperature();

        } catch (Exception e) {
            System.err.println("Error during AnalyticsService initialization: " + e.getMessage());
            e.fillInStackTrace();
        }
    }

    public void PrintHumidity() {
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if ("humidity".equals(fluxRecord.getValueByKey("_field"))) {
                    Double value = (Double) fluxRecord.getValueByKey("_value");
                    System.out.println("Значення вологості: " + value);
                } else {
                    break;
                }
            }
        }
    }

    public void PrintTemperature() {
        for (FluxTable fluxTable : tables) {
            List<FluxRecord> records = fluxTable.getRecords();
            for (FluxRecord fluxRecord : records) {
                if ("temperature".equals(fluxRecord.getValueByKey("_field"))) {
                    Double value = (Double) fluxRecord.getValueByKey("_value");
                    System.out.println("Значення температури: " + value);
                } else {
                    break;
                }
            }
        }
    }
}

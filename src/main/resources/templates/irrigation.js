const express = require('express');
const cors = require('cors');
const {InfluxDB} = require("@influxdata/influxdb-client");
const {json} = require("express");

const app = express();
app.use(cors());

let url = "http://localhost:8086";
let token = "k56HcUqpxgAkINETkcsqUkRG1Lv-4uWK6sVMqRy7_B2FTCMLwKfsGUa8vwSmuNrmdXfgR_7Ixq8e2IK27u_sHg==";
let org = "myorg";
let bucket = "mybucket";

const queryApi = new InfluxDB({url, token}).getQueryApi(org);

const queryTemperature =
    'from(bucket: "' + bucket + '")' +
    '  |> range(start:0)' +
    '  |> filter(fn: (r) => r["_measurement"] == "sensors_data")' +
    '  |> filter(fn: (r) => r["_field"] == "temperature")' +
    '  |> yield(name: "mean")';

const queryHumidity =
    'from(bucket: "' + bucket + '")' +
    '  |> range(start:0)' +
    '  |> filter(fn: (r) => r["_measurement"] == "sensors_data")' +
    '  |> filter(fn: (r) => r["_field"] == "humidity")' +
    '  |> yield(name: "mean")';

async function getQuery(query) {
    const result = [];
    for await (const {values, tableMeta} of queryApi.iterateRows(query)) {
        const obj = tableMeta.toObject(values);
        result.push({
            time: obj._time,
            value: obj._value
        });
        console.log(result[result.length - 1]);
    }
    return result;
}

app.get('/api/temperature', async (req, res) => {
    try {
        const data = await getQuery(queryTemperature);
        res.json(data);
    } catch (error) {
        res.status(500).json({error: "Failed to load data", details: error.message});
    }
});

app.get('/api/humidity', async (req, res) => {
    console.log("Request received for /api/humidity"); // Логування запиту
    try {
        const data = await getQuery(queryHumidity);
        res.json(data);
    } catch (error) {
        res.status(500).json({error: "Failed to load data", details: error.message});
    }
});

app.listen(3012, () => {
    console.log("Server is running on http://localhost:3012");
});

app.close();


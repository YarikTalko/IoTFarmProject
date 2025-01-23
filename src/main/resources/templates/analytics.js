import express from 'express';
import cors from 'cors';
import { InfluxDB } from '@influxdata/influxdb-client';

const app = express();
app.use(cors());
app.use(express.json());

let url = "http://localhost:8086";
let token = "k56HcUqpxgAkINETkcsqUkRG1Lv-4uWK6sVMqRy7_B2FTCMLwKfsGUa8vwSmuNrmdXfgR_7Ixq8e2IK27u_sHg==";
let org = "myorg";
let bucket = "mybucket";

const queryApi = new InfluxDB({url, token}).getQueryApi(org);

let startDate = "", endDate = "";

const getQueryTemperature = () =>
    `from(bucket: "${bucket}") |> range(start: ${startDate}, stop: ${endDate}) |> filter(fn: (r) => r["_measurement"] == "sensors_data") |> filter(fn: (r) => r["_field"] == "temperature") |> yield(name: "mean")`;

const getQueryHumidity = () =>
    `from(bucket: "${bucket}") |> range(start: ${startDate}, stop: ${endDate}) |> filter(fn: (r) => r["_measurement"] == "sensors_data") |> filter(fn: (r) => r["_field"] == "humidity") |> yield(name: "mean")`;

export function processData(start, end) {
    startDate = start;
    endDate = end;
}

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

app.post('/api/analytics', async (req, res) => {
    const { startDate, endDate } = req.body;
    processData(startDate, endDate);
    try {
        const temperatureData = await getQuery(getQueryTemperature());
        const humidityData = await getQuery(getQueryHumidity());
        res.json({ temperatureData, humidityData });
    } catch (error) {
        res.status(500).json({ error: "Failed to process data", details: error.message });
    }
});

app.listen(3013, () => {
    console.log("Server is running on http://localhost:3013");
});

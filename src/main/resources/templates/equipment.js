const express = require('express');
const cors = require('cors');
const {Client} = require('pg');

const app = express();
app.use(cors());

const client = new Client({
    host: "localhost",
    user: "user",
    port: 5432,
    password: "user",
    database: "equipment_manager"
});

client.connect((err) => {
    if (err) {
        console.error("Failed to connect to the database:", err.message);
    } else {
        console.log("Connected to the database.");
    }
});

app.get('/api/equipment_sensor_data', (req, res) => {
    client.query(`SELECT * FROM equipment_sensor_data`, (err, result) => {
        if (err) {
            res.status(500).send("Error occurred: " + err.message);
        } else {
            res.json(result.rows);
        }
    });
});

app.get('/api/equipment_tasks', (req, res) => {
    client.query(`SELECT * FROM equipment_tasks`, (err, result) => {
        if (err) {
            res.status(500).send("Error occurred: " + err.message);
        } else {
            res.json(result.rows);
        }
    });
});

app.listen(3011, () => {
    console.log("Server is running on http://localhost:3011");
});

import express from 'express';
import cors from 'cors';
import pkg from 'pg';
const { Client } = pkg;

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

app.use(express.json()); // Для обробки JSON у запитах

app.put('/api/equipment_tasks/:id', (req, res) => {
    const id = parseInt(req.params.id, 10);
    const { status, priority, assigned_to } = req.body;

    if (!status || !priority || !assigned_to) {
        return res.status(400).send("Status, priority, and assigned_to are required.");
    }

    const query = `
        UPDATE equipment_tasks
        SET status = $1, priority = $2, assigned_to = $3, updated_at = NOW()
        WHERE id = $4
            RETURNING *;
    `;

    client.query(query, [status, priority, assigned_to, id], (err, result) => {
        if (err) {
            console.error("Error updating equipment task:", err.message);
            return res.status(500).send("Error updating equipment task: " + err.message);
        }

        if (result.rowCount === 0) {
            return res.status(404).send("Equipment task not found.");
        }

        res.json(result.rows[0]);
    });
});

app.listen(3011, () => {
    console.log("Server is running on http://localhost:3011");
});

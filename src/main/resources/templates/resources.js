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
    database: "resource_manager"
});

client.connect((err) => {
    if (err) {
        console.error("Failed to connect to the database:", err.message);
    } else {
        console.log("Connected to the database.");
    }
});

app.get('/api/resource_monitoring', (req, res) => {
    client.query(`SELECT * FROM resource_monitoring`, (err, result) => {
        if (err) {
            res.status(500).send("Error occurred: " + err.message);
        } else {
            res.json(result.rows);
        }
    });
});

app.get('/api/resource_tasks', (req, res) => {
    client.query(`SELECT * FROM resource_tasks`, (err, result) => {
        if (err) {
            res.status(500).send("Error occurred: " + err.message);
        } else {
            res.json(result.rows);
        }
    });
});

app.use(express.json()); // Для обробки JSON у запитах

app.put('/api/resource_tasks/:id', (req, res) => {
    const id = parseInt(req.params.id, 10);
    const { status, priority } = req.body;

    if (!status || !priority) {
        return res.status(400).send("Both status and priority are required.");
    }

    const query = `
        UPDATE resource_tasks
        SET status = $1, priority = $2, updated_at = NOW()
        WHERE id = $3
        RETURNING *;
    `;

    client.query(query, [status, priority, id], (err, result) => {
        if (err) {
            console.error("Error updating task:", err.message);
            return res.status(500).send("Error updating task: " + err.message);
        }

        if (result.rowCount === 0) {
            return res.status(404).send("Task not found.");
        }

        res.json(result.rows[0]);
    });
});


app.listen(3010, () => {
    console.log("Server is running on http://localhost:3010");
});

const express = require('express');
const swaggerUi = require('swagger-ui-express');
const openapiSpec = require('./config/swagger');
const meetingRoutes = require('./routes/meeting.routes');

const app = express();

app.use(express.json());

// ---- Route d'accueil ----
app.get('/', (req, res) => {
  res.send('Welcome to Meeting Service!');
});

// ---- Health endpoint ----
app.get('/health', (req, res) => {
  res.status(200).json({
    status: 'UP'
  });
});

// ---- Info endpoint ----
app.get('/info', (req, res) => {
  const packageJson = require('../package.json');

  res.status(200).json({
    name: packageJson.name,
    version: packageJson.version
  });
});

// ---- Swagger documentation ----
app.get('/v3/api-docs', (req, res) => {
  res.json(openapiSpec);
});

app.use(
    '/swagger-ui',
    swaggerUi.serve,
    swaggerUi.setup(openapiSpec)
);

// ---- API routes ----
app.use('/api/meetings', meetingRoutes);

// ---- 404 handler ----
app.use((req, res) => {
  res.status(404).json({
    status: 404,
    error: 'Not Found',
    message: `Route ${req.method} ${req.originalUrl} not found`
  });
});

// ---- Error handler ----
app.use((err, req, res, next) => {
  const status = err.status || 500;

  res.status(status).json({
    status,
    error: status === 500
        ? 'Internal Server Error'
        : 'Error',
    message: err.message
  });
});

module.exports = app;

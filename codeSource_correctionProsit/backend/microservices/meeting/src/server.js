
const app = require("./app");
const { Eureka } = require("eureka-js-client");

const PORT = Number(process.env.PORT) || 8083;

const client = new Eureka({
  instance: {
    app: "MEETING",
    instanceId: `MEETING-localhost:${PORT}`,
    hostName: "localhost",
    ipAddr: "127.0.0.1",
    statusPageUrl: `http://localhost:${PORT}/`,
    port: {
      "$": PORT,
      "@enabled": true
    },
    vipAddress: "MEETING",
    dataCenterInfo: {
      "@class": "com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo",
      name: "MyOwn"
    }
  },
  eureka: {
    host: "localhost",
    port: 8761,
    servicePath: "/eureka/apps/"
  }
});

const server = app.listen(PORT, () => {
  console.log(`Meeting Service demarre sur le port ${PORT}`);

  client.start((error) => {
    if (error) {
      console.error("Erreur Eureka :", error);
    } else {
      console.log("Meeting enregistre dans Eureka !");
    }
  });
});

process.on("SIGINT", () => {
  client.stop();
  server.close(() => process.exit(0));
});

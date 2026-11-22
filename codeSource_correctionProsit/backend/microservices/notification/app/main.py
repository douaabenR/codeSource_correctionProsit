
"""
Notification microservice - entry point.

Run:
    uvicorn app.main:app --reload --port 8084

or:
    python -m app.main
"""

import os
from contextlib import asynccontextmanager

from fastapi import FastAPI
from py_eureka_client.eureka_client import EurekaClient

from app.routers import notification


# Configuration du port
PORT = int(os.getenv("PORT", "8084"))


# Configuration du client Eureka
eureka_client = EurekaClient(
    eureka_server="http://localhost:8761/eureka",
    app_name="NOTIFICATION",
    instance_port=PORT
)


# Cycle de vie de l'application
@asynccontextmanager
async def lifespan(app: FastAPI):

    # Démarrer le client et enregistrer Notification dans Eureka
    await eureka_client.start()

    print("Notification Service démarré !")
    print(f"Port : {PORT}")
    print("Enregistrement Eureka demandé.")

    try:
        yield

    finally:
        # Arrêter le client Eureka à la fermeture
        await eureka_client.stop()
        print("Notification Service arrêté.")


# Création de l'application FastAPI
app = FastAPI(
    title="Notification Microservice API",
    version="1.0.0",
    description=(
        "Notification microservice (Python / FastAPI, no database). "
        "Only the hello endpoint is implemented; "
        "the notification logic is to be developed by students."
    ),
    contact={"name": "Badia Abouhdid"},
    servers=[
        {
            "url": f"http://localhost:{PORT}",
            "description": "Local"
        }
    ],
    docs_url="/swagger-ui",
    openapi_url="/v3/api-docs",
    redoc_url="/redoc",
    lifespan=lifespan
)


# Conserver le routeur existant
app.include_router(notification.router)


# Lancement direct du microservice
if __name__ == "__main__":
    import uvicorn

    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=PORT,
        reload=True
    )

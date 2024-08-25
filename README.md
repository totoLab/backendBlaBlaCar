# Setup

1. `cd backendBlaBlaCar/remote/`
2. `cd db/ && docker compose up -d`
3. `cd keycloak/ && docker compose up -d`
4. connect to `http://localhost:8080`, login with admin/admin credentials and import `keycloak/realm-export.json`

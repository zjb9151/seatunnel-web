# SeaTunnel + DolphinScheduler Integration Platform

Monorepo layout after reorganization:

```
├── config/integration.yml       # Unified configuration
├── integration-dolphinscheduler/  # DS official source (submodule)
├── integration-seatunnel/         # SeaTunnel-Web upstream modules
├── integration-server/            # Orchestrator + unified API (:9000)
├── integration-web/             # Unified frontend
└── scripts/                     # build-all.ps1, start.ps1, stop.ps1
```

## Quick Start

1. Edit [config/integration.yml](config/integration.yml) (DS home, Engine home, MySQL, tokens).

2. Build:
   ```powershell
   $env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
   $env:SEATUNNEL_HOME = "C:\path\to\apache-seatunnel-2.3.11"
   .\scripts\build-all.ps1
   ```

3. Start (single entry — integration-server manages child processes):
   ```powershell
   .\scripts\start.ps1
   ```

4. Open **http://127.0.0.1:9000**

## Architecture

- **integration-server** reads `config/integration.yml`, starts/monitors DolphinScheduler, SeaTunnel Engine, and seatunnel-app, and exposes `/api/v1/*`.
- **integration-web** provides unified navigation; ST/DS pages use iframe embeds in MVP.
- **integration-seatunnel/** and **integration-dolphinscheduler/** remain vendor-clean — no integration patches.

## Dev (frontend only)

```powershell
cd integration-web
npm install
npm run dev   # http://localhost:9001, proxies /api -> :9000
```

## API Endpoints

| Path | Description |
|------|-------------|
| `GET /api/v1/platform/info` | Platform & service health |
| `GET /api/v1/ds/info` | DS embed UI info |
| `GET /api/v1/ds/seatunnel-ui/info` | ST embed UI info |
| `POST /api/v1/auth/embed` | DS session → ST JWT |
| `/api/v1/schedules/**` | Job schedule CRUD |

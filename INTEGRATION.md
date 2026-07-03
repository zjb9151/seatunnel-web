# SeaTunnel + DolphinScheduler Integration Platform

Integration-only monorepo — **no vendor source code**. Official DS / SeaTunnel **release packages** are installed under `runtime/` and managed via UI or API.

```
├── config/integration.yml          # Platform config + runtime install paths
├── integration-server/
│   ├── bundled/                    # Built-in default official tar.gz (download via script)
│   └── integration-app/            # Orchestrator + unified API (:9000)
├── integration-web/                # Unified frontend (+ Runtime version page)
├── runtime/                        # Installed official releases (gitignored)
│   ├── dolphinscheduler/{version}/
│   ├── seatunnel-engine/{version}/
│   └── seatunnel-web/{version}/
└── scripts/
```

## Quick Start

```powershell
Copy-Item config\integration.yml.example config\integration.yml   # if needed
.\scripts\download-bundled.ps1    # DS + Engine official bins -> integration-server/bundled/
.\scripts\build-all.ps1
.\scripts\start.ps1               # extracts bundled -> runtime/, starts DS/ST
```

Open **http://127.0.0.1:9000/runtime** for version install / switch / upgrade.

## Runtime management

| Action | UI | API |
|--------|-----|-----|
| List components | `/runtime` page | `GET /api/v1/runtime/components` |
| Download & install | Download & Install | `POST /api/v1/runtime/{id}/install` |
| Switch version | Switch | `POST /api/v1/runtime/{id}/switch` |
| Install local dist | — | `POST /api/v1/runtime/{id}/install-local` |

On startup `integration-server`:

1. Reads `runtime.*` install dirs (default: `runtime/` next to repo root)
2. Extracts bundled archives from `integration-server/bundled/` if version not installed
3. Or downloads from Apache if no bundled archive (first bootstrap)
4. Sets `processes.*.home` and starts DolphinScheduler, SeaTunnel Engine, SeaTunnel-Web

## Config (`runtime` section)

```yaml
runtime:
  base-dir: ..                              # repo root
  bundled-dir: integration-server/bundled
  dolphinscheduler:
    install-dir: runtime/dolphinscheduler
    version: "3.2.2"
  seatunnel-engine:
    install-dir: runtime/seatunnel-engine
    version: "2.3.11"
  seatunnel-web:
    install-dir: runtime/seatunnel-web
    version: "1.0.3-SNAPSHOT"
```

## Integration code only

| Path | Integration code? |
|------|-------------------|
| `integration-server/` | Yes |
| `integration-web/` | Yes |
| `runtime/` | Official bins only |
| `integration-server/bundled/` | Official archives only |

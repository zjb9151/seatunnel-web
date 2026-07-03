# Runtime (official releases)

Vendor **source code is not in this repo**. Official Apache release packages are:

- Downloaded into `integration-server/bundled/` via `scripts/download-bundled.ps1`
- Extracted to `runtime/{component}/{version}/` on startup or via UI
- Configured in `config/integration.yml` under `runtime.*`

See [INTEGRATION.md](../INTEGRATION.md).

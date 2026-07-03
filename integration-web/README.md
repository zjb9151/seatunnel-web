# Integration Web

Unified frontend for SeaTunnel + DolphinScheduler. Served by `integration-server` at port **9000** (production) or Vite dev server at **9001**.

## Dev

```powershell
cd integration-web
npm install
npm run dev
```

Proxy: `/api` → `http://127.0.0.1:9000`

## Build

```powershell
npm run build:prod
```

Output: `integration-web/dist` (configured in `config/integration.yml` → `integration.web-root`)

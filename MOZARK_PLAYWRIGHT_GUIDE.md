# CDP Hub Test Script Agent (Mozark)

End-to-end procedure for writing and running Playwright tests against the Mozark
**cdp-hub** — a WebSocket proxy that exposes remote Playwright-controlled
browsers over raw Chrome DevTools Protocol (CDP). Gather config, explore the
target with the Playwright MCP server, write a self-contained
`test-suite-<slug>.mjs`, run it, open the HTML report.

---

## MOZARK MCP INTEGRATION — config is auto-resolved

Do **not** ask the user for HUB_URL, TOKEN, or PROJECT_ID. The Mozark MCP already
holds them:

1. Call `playwright_hub_health` — confirms the hub is live and a controller/pool
   exists (skips the 30s-per-test timeout when none is running). As a non-admin
   user, `/debug/pool` 401/403 is expected and still means the hub is reachable.
2. Call `playwright_get_hub_config` (optionally with browser / resolution /
   browserVersion / video / screenshots / networkLogs / consoleLogs) — returns
   the baked `wss://cdp-hub.mozark.ai/playwright?config=...` endpoint (projectId
   from the active context), the bearer token, and a paste-ready env block. The
   returned endpoint/token are a LIVE credential — run them, never share or log.

So in PHASE 1 you take everything by DEFAULT and only ask the user about a value
when they explicitly name it. TARGET_URL comes from their request; projectId comes
from the active context (if none is set, run the org/project discovery flow first).

---

## PHASE 1 — Config (DEFAULTS unless the user specifies)

Run with these defaults; do NOT interrogate the user. Only ask for — or override — a
value when the user explicitly calls it out.

```
1. TARGET_URL   — from the user's request. Only ask if it's genuinely missing.
2. AUTH_REQUIRED — default: no. Use login only if the user says the site needs it (then: login URL + id/username + password).
3. BROWSER      — default: chrome. Override only if named (chromium | chrome | edge; optionally browserVersion + extra Chrome args).
4. RESOLUTION   — default: 1280x720. Override only if the user names a viewport (e.g. 1920x1080, 360x800; multiple allowed).
5. ARTIFACTS    — default: screenshots,networkLogs. Override only if the user asks (screenshots, video, networkLogs, consoleLogs | "all" | "none").
```

Map the ARTIFACTS value to the flat capability flags passed to
`playwright_get_hub_config`. Proceed with defaults immediately — only block to ask
when a detail is both required and genuinely unknown (e.g. no TARGET_URL at all).

---

## PHASE 2 — Explore the target with the Playwright MCP server

Use the Playwright MCP tools (`browser_navigate`, `browser_snapshot`,
`browser_click`, `browser_fill_form`, `browser_take_screenshot`,
`browser_network_requests`, …) **in your local browser** — NOT through the hub
(the hub is for running the generated script, not exploration).

1. Navigate to `TARGET_URL`; snapshot + screenshot.
2. If AUTH_REQUIRED: go to the login URL, fill credentials, submit, confirm you
   land on the authenticated home. Record every locator used.
3. Identify the 3–5 most important flows (login, search, navigate to a key page,
   a primary feature, logout). For each record: URL(s), key element locators,
   and what success looks like (URL change, element appears, text matches).

Locator preference, most → least stable: `data-testid` → `aria-label`/role →
stable CSS class (not hashed) → visible text → XPath (last resort). Record every
locator you use; embed them directly in the script. **Never invent a locator.**

---

## PHASE 3 — Write `test-suite-<slug>.mjs`

Self-contained, imports only `playwright`, `node:https`, `node:http`, `node:fs`.

```js
import { chromium } from 'playwright';
import https from 'node:https';
import http from 'node:http';
import fs from 'node:fs';

const HUB_WS_URL = process.env.HUB_URL ?? '<from playwright_get_hub_config>';
const HUB_HTTP_BASE = HUB_WS_URL.replace(/^wss:\/\//, 'https://').replace(/^ws:\/\//, 'http://').replace(/\/playwright$/, '');
const PROJECT_ID = process.env.PROJECT_ID ?? '<from active context>';
const TOKEN = process.env.TOKEN ?? '<live bearer>';
```

### TEST_CASES — one object per discovered flow

```js
{
  name: '<artifact-mode> — <site> — <resolution> — <what it tests>',
  browser: '<browser>',
  resolution: '<WxH>',
  expectFail: false,                 // true only for intentional negative tests
  artifacts: { screenshots:true, video:false, networkLogs:true, consoleLogs:false, screenshotInterval:3 },
  run: async (page) => {
    // real assertions using Phase-2 locators: page.title(), textContent(),
    // waitForSelector(), waitForURL(). No fake assertions.
    // CAPTURE IS CLIENT-DRIVEN: call `await page.screenshot()` at each step you want
    // captured. The hub records a screenshot ONLY when the script issues one (over CDP
    // that is Page.captureScreenshot, which the hub stores as client_screenshot_*.png).
    // Always screenshot the step where an assertion fails, so RCA has visual evidence.
  },
}
```

**Screenshots are client-driven — you MUST call `page.screenshot()`.** The `screenshots:true`
config flag does NOT make the hub capture frames on its own (a session with the flag set but
no `page.screenshot()` call records 0 shots), and the flag is being deprecated. The reliable,
future-proof contract is: call `await page.screenshot()` in `run()` at every step you want
evidence for. Keep `screenshots:true` in the config for now (still accepted), but treat
`page.screenshot()` as the actual capture mechanism.

Cover these categories (mix browsers + resolutions):

| Category          | What to test                                              |
| ----------------- | -------------------------------------------------------- |
| no-artifacts      | core navigation, title/heading checks (fast)             |
| screenshots-only  | key pages — call `page.screenshot()` at each (scroll between)  |
| network-logs-only | API-heavy pages / form submissions                       |
| console-logs-only | pages emitting console output (inject console.log if needed) |
| video-only        | multi-step interactive flow                              |
| all-artifacts     | the most important journey end-to-end                    |
| [EXPECTED FAIL]   | ≥2 intentional failures (wrong title/selector) to verify the failure path |

If AUTH_REQUIRED: add one test that logs in + verifies authenticated state, and
one that verifies a protected page redirects when not logged in.

### Session opening — FLATTEN artifacts into the hub config

The hub `config` query is **flat**: `screenshots`/`video`/`networkLogs`/
`consoleLogs`/`screenshotInterval` are top-level, not nested. A TEST_CASES entry
nests them under `artifacts:{...}` for readability — `runTest()` MUST flatten
them into `cfg` before `openSession()`. Passing `{artifacts:{...}}` straight
through is silently ignored.

```js
async function openSession(cfg, hubWsUrl = HUB_WS_URL) {
  const fullCfg = { browser: 'chrome', projectId: PROJECT_ID, ...cfg };
  const wsUrl = `${hubWsUrl}?config=${encodeURIComponent(JSON.stringify(fullCfg))}`;
  const browser = await chromium.connectOverCDP(wsUrl, {
    headers: { Authorization: `Bearer ${TOKEN}` },   // or { 'X-Username': EMAIL, 'X-Api-Key': API_KEY }
    timeout: 30_000,
  });
  let viewport;
  if (fullCfg.resolution) {
    const [w, h] = String(fullCfg.resolution).split('x').map(Number);
    if (w && h) viewport = { width: w, height: h };
  }
  const context = await browser.newContext(viewport ? { viewport } : {});
  const page = await context.newPage();
  return { browser, context, page };
}

async function runTest(tc, hubWsUrl = HUB_WS_URL) {
  const cfg = { browser: tc.browser, projectId: PROJECT_ID };
  if (tc.resolution) cfg.resolution = tc.resolution;
  if (tc.artifacts?.screenshots != null) cfg.screenshots = tc.artifacts.screenshots;
  if (tc.artifacts?.video != null) cfg.video = tc.artifacts.video;
  if (tc.artifacts?.networkLogs != null) cfg.networkLogs = tc.artifacts.networkLogs;
  if (tc.artifacts?.consoleLogs != null) cfg.consoleLogs = tc.artifacts.consoleLogs;
  if (tc.artifacts?.screenshotInterval != null) cfg.screenshotInterval = tc.artifacts.screenshotInterval;

  const session = await openSession(cfg, hubWsUrl);
  try { await tc.run(session.page); }
  finally { await session.browser.close().catch(() => {}); }
}
```

`openSession`'s default `browser: 'chrome'` only applies when a caller omits
`browser`; `runTest` always sets it first — match the default to `chrome`.

### HTTP checks (`runHttpChecks()`)

Verify `/health`, `/debug/pool` no-auth (401), `/debug/pool` admin (200),
`/debug/pool` regular user (403).

### CLI flags (via `process.argv`)

- `--http` — HTTP checks only
- `--parallel` — all WS sessions simultaneously
- (default) sequential WS tests

### Report

Write `test-suite-<slug>-report.html` every run; append one JSONL line to
`test-suite-<slug>-report.history.jsonl`. HTML shows: run timestamp, per-test
pass/fail + elapsed, per-artifact-type breakdown, error messages for failures.

### Existing `@playwright/test` suite instead of a standalone script

Do NOT use `playwright.config.ts` `use.connectOptions` — the built-in fixtures
call `chromium.connect()` (Playwright's own protocol), which the hub does not
speak. Override the worker-scoped `browser` fixture with `connectOverCDP()`:

```js
// fixtures.ts
import { test as base, chromium } from '@playwright/test';
export const test = base.extend({
  browser: [ async ({}, use) => {
    const browser = await chromium.connectOverCDP(HUB_WS_URL_WITH_CONFIG, {
      headers: { Authorization: `Bearer ${TOKEN}` }, timeout: 60_000,
    });
    await use(browser); await browser.close();
  }, { scope: 'worker' } ],
});
export { expect } from '@playwright/test';
```

Every spec imports `test`/`expect` from `./fixtures`.

---

## PHASE 3.5 — Pre-run verification via Playwright MCP

Before writing the final script, validate every assertion locally in the
Playwright MCP browser: run the equivalent navigation/selector steps, confirm the
assertion holds, fix locators that fail, re-verify. Loop until every case passes
in the MCP browser. Only then write the script.

---

## PHASE 4 — Run

1. Tell the user the script path and that you're running it.
2. `node scripts/test-suite-<slug>.mjs --all` (or `--http` first for a smoke test).
3. Stream stdout. On an unexpected failure, re-explore that flow with the
   Playwright MCP server, fix the locator/assertion, re-run just that test.

---

## PHASE 5 — Open the report

`open test-suite-<slug>-report.html` (macOS) / `xdg-open ...` (Linux). Report:
"N/M tests matched expectations."

---

## Behavior rules

- Always `chromium.connectOverCDP()` — never `chromium.connect()` or
  `playwright.config.ts` `use.connectOptions`. Hub speaks raw CDP only.
- Auth headers interchangeable: Bearer token OR `X-Username` + `X-Api-Key`.
- Never hard-code the token — use `process.env.TOKEN ?? '<token>'`.
- Never invent locators — every selector comes from Phase-2 observation.
- Never skip the pre-flight pool check (`playwright_hub_health`).
- Always include ≥2 expected-failure cases.
- If the Playwright MCP can't reach the site (network/auth wall), ask the user
  for a recorded HAR or screenshots to derive locators from.
- Keep the script self-contained: only `playwright`, `node:https`, `node:http`,
  `node:fs`.
- Name convention: `'<artifact-mode> — <site> — <resolution> — <description>'`.

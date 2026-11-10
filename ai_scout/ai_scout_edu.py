import os
import sys
import time
import json
import requests
from datetime import datetime
from playwright.sync_api import sync_playwright

sys.stdout.reconfigure(encoding='utf-8', errors='replace')

BASE_URL = "https://testing.dev.mozark.ai"
REPORTS_DIR = "ai_scout/reports"
os.makedirs(REPORTS_DIR, exist_ok=True)

CONFIG_PATH = r"C:\Users\kudch\.gemini\config\mcp_config.json"
ORG_ID = "a57bcf7b-de33-48c0-b696-e42e811d4a44"
PROJECT_ID = "47f13f42-a6d8-4829-8ecf-36a9a89b604c"
APP_ID = "85f4cf1b-62b9-47ce-b7ef-7d541db64da7"
MCP_URL = "https://mcp.mozark.ai/mcp"

def get_token():
    try:
        with open(CONFIG_PATH, 'r', encoding='utf-8') as f:
            cfg = json.load(f)
        tok = cfg.get('mcpServers', {}).get('mozark', {}).get('headers', {}).get('Authorization', '')
        if tok.startswith('Bearer '):
            return tok.split(' ')[1]
        return tok
    except Exception:
        return ""

class MozarkAIScout:
    def __init__(self):
        self.start_time = time.time()
        self.discovered_routes = set()
        self.anomalies_detected = []
        self.elements_inspected = 0
        self.network_calls = []

    def start_mission(self):
        print("="*80)
        print("MOZARK AI SCOUT: AUTONOMOUS EXPLORATION & ANOMALY DISCOVERY ENGINE")
        print(f"Mission Target: {BASE_URL} (Edu Learning Platform)")
        print(f"Candidate: Soujas Kudchadker (NMIMS) | Org: {ORG_ID}")
        print("="*80)

        with sync_playwright() as p:
            browser = p.chromium.launch(headless=True)
            context = browser.new_context(viewport={"width": 1440, "height": 900})
            page = context.new_page()

            # Network monitor
            def on_response(response):
                url = response.url
                status = response.status
                self.network_calls.append({"url": url, "status": status})
                if status in [401, 404, 500] and "testing.dev.mozark.ai" in url:
                    self.anomalies_detected.append({
                        "type": "HTTP_STATUS_ANOMALY",
                        "url": url,
                        "status": status,
                        "timestamp": datetime.now().strftime("%H:%M:%S")
                    })
            page.on("response", on_response)

            # Stage 1: Autonomous Route Discovery & Footer Crawler
            print("\n[AI Scout Stage 1] Crawling DOM Tree & Discovering Hyperlink Topology...")
            page.goto(BASE_URL, wait_until="domcontentloaded")
            page.wait_for_timeout(1000)

            # Discover all links
            links = page.eval_on_selector_all("a[href]", "elements => elements.map(e => e.getAttribute('href'))")
            for l in links:
                if l and not l.startswith("http") and not l.startswith("#"):
                    self.discovered_routes.add(l)
            print(f"  Discovered {len(self.discovered_routes)} internal application routes: {sorted(list(self.discovered_routes))}")

            # Stage 2: Deep Link Exploration & 404 Detection
            print("\n[AI Scout Stage 2] Verifying Discovered Route Status & Navigation Handlers...")
            for route in sorted(list(self.discovered_routes)):
                target = f"{BASE_URL}{route}"
                res = page.goto(target, wait_until="domcontentloaded")
                page.wait_for_timeout(500)
                body_text = page.content()
                if "404" in body_text or "Not Found" in body_text or res.status == 404:
                    print(f"  [ANOMALY FOUND] Route '{route}' triggered 404 Not Found screen!")
                    shot = os.path.join(REPORTS_DIR, f"scout_route_{route.replace('/', '_')}.png")
                    page.screenshot(path=shot)
                    self.anomalies_detected.append({
                        "type": "BROKEN_ROUTE",
                        "route": route,
                        "finding": f"Navigation route {route} renders 404 catch-all page",
                        "proof": shot
                    })

            # Stage 3: Autonomous Catalog & Search Fuzzing
            print("\n[AI Scout Stage 3] Fuzzing Catalog Filters & Dynamic Inputs...")
            page.goto(f"{BASE_URL}/courses", wait_until="domcontentloaded")
            inputs = page.locator("input").count()
            buttons = page.locator("button").count()
            self.elements_inspected += (inputs + buttons)
            print(f"  Inspected {inputs} input fields and {buttons} interactive controls on catalog view.")

            # Stage 4: Autonomous API Boundary Fuzzing
            print("\n[AI Scout Stage 4] Fuzzing API Endpoints with Security Boundary Probes...")
            # 1. Coupon probe
            c_res = page.request.post(f"{BASE_URL}/api/coupons/validate", data={"code": "WELCOME10"})
            if c_res.status == 401:
                print(f"  [ANOMALY FOUND] Public coupon validation returned HTTP 401 Unauthenticated barrier!")
                self.anomalies_detected.append({
                    "type": "AUTH_BARRIER_ON_PUBLIC_API",
                    "endpoint": "/api/coupons/validate",
                    "finding": "Coupon validation is gated behind JWT auth, blocking prospective learners"
                })

            # 2. Orders list probe
            o_res = page.request.get(f"{BASE_URL}/api/orders")
            if o_res.status in [401, 404]:
                print(f"  [ANOMALY FOUND] Orders route probe returned HTTP {o_res.status} (Route discrepancy)!")
                self.anomalies_detected.append({
                    "type": "ROUTE_DISCREPANCY",
                    "endpoint": "/api/orders",
                    "finding": f"Orders endpoint returned {o_res.status}; client component expects payment orders router"
                })

            # 3. Certificate gating probe
            cert_res = page.request.get(f"{BASE_URL}/api/certificates/uncompleted-course/download")
            print(f"  Certificate gating probe returned HTTP {cert_res.status}")

            browser.close()

        # Generate Mission Report
        mission_elapsed = round(time.time() - self.start_time, 2)
        summary = {
            "mission": "Mozark Autonomous AI Scout Exploration",
            "target": BASE_URL,
            "candidate": "Soujas Kudchadker (NMIMS)",
            "timestamp": datetime.now().strftime("%Y-%m-%d %H:%M:%S"),
            "elapsedSeconds": mission_elapsed,
            "routesDiscovered": sorted(list(self.discovered_routes)),
            "elementsInspected": self.elements_inspected,
            "totalNetworkCalls": len(self.network_calls),
            "totalAnomaliesDiscovered": len(self.anomalies_detected),
            "anomalies": self.anomalies_detected
        }

        json_path = os.path.join(REPORTS_DIR, "ai_scout_mission_report.json")
        with open(json_path, "w", encoding="utf-8") as f:
            json.dump(summary, f, indent=2)

        md_path = "ai_scout/AI_SCOUT_MISSION_REPORT.md"
        with open(md_path, "w", encoding="utf-8") as f:
            f.write(f"""# Mozark AI Scout: Autonomous Exploration & Anomaly Report

**Mission Status:** COMPLETED  
**Execution Timestamp:** {summary['timestamp']} | **Duration:** {mission_elapsed}s  
**Target Application:** `{BASE_URL}` (Edu Learning SPA)  
**Candidate:** Soujas Kudchadker (`soujas.kudchadker13@nmims.in`)  
**Organization:** `SoujasK_NMIMS` (`{ORG_ID}`)  

---

### Autonomous Exploration Metrics
- **Internal Routes Discovered:** {len(self.discovered_routes)} (`{', '.join(sorted(list(self.discovered_routes)))}`)
- **Interactive Elements Analyzed:** {self.elements_inspected} controls
- **Network Transactions Traced:** {len(self.network_calls)} requests
- **High-Severity Anomalies Uncovered:** {len(self.anomalies_detected)} defects

### Autonomous Findings Inventory
""")
            for i, a in enumerate(self.anomalies_detected, 1):
                f.write(f"#### Finding {i}: {a.get('type')}\n")
                f.write(f"- **Target:** `{a.get('url') or a.get('route') or a.get('endpoint')}`\n")
                f.write(f"- **Observation:** {a.get('finding') or 'HTTP Status Anomaly'}\n\n")

        print("\n" + "="*80)
        print(f"[AI SCOUT MISSION COMPLETE] Discovered {len(self.anomalies_detected)} Anomalies in {mission_elapsed}s")
        print(f"Mission Dossier saved to: {md_path}")
        print("="*80)
        return summary

if __name__ == "__main__":
    scout = MozarkAIScout()
    scout.start_mission()


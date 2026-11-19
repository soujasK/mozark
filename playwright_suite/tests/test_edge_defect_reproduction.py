import json
import os
import sys
import time
import requests
from playwright.sync_api import sync_playwright

sys.stdout.reconfigure(encoding="utf-8", errors="replace")

BASE_URL = "https://testing.dev.mozark.ai"
DEFECTS_DIR = os.path.join("playwright_suite", "reports", "defects")


def run_edge_defect_tests():
    os.makedirs(DEFECTS_DIR, exist_ok=True)
    ts = int(time.time())
    results = []

    print("\n" + "=" * 80)
    print("PLAYWRIGHT STAGE 1B: LIVE EDGE & NEWLY DISCOVERED DEFECT REPRODUCTION")
    print("=" * 80)

    email = f"qa_edge_{ts}@example.com"
    pwd = "QaPass1234"
    r_reg = requests.post(
        f"{BASE_URL}/api/auth/register",
        json={"name": "Edge Repro User", "email": email, "password": pwd},
        timeout=25,
    )
    reg_json = r_reg.json()
    token = reg_json.get("token") or reg_json.get("data", {}).get("token")
    user_obj = reg_json.get("user") or reg_json.get("data", {}).get("user") or {}

    courses = requests.get(f"{BASE_URL}/api/courses", timeout=25).json().get("data", [])
    paid_course = next((c for c in courses if (c.get("price") or 0) > 0), courses[0])
    cid = paid_course["_id"]

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=True)
        context = browser.new_context(viewport={"width": 1366, "height": 900})
        page = context.new_page()

        # 1. BUG-503: Footer Newsletter {email} vs {emailAddress} HTTP 400
        print("\n[Edge Repro 1/7] Newsletter Subscribe Field Mismatch (BUG-503 / TC-188)...")
        page.goto(BASE_URL, wait_until="domcontentloaded", timeout=30000)
        footer = page.locator("footer, .site-footer").first
        footer.wait_for(state="visible", timeout=15000)
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)")
        footer.locator("input[type='email'], input").first.fill(f"nl_{ts}@example.com")
        with page.expect_response(lambda r: "/newsletter/subscribe" in r.url, timeout=15000) as nl_info:
            footer.locator("button[type='submit'], button").first.click()
        nl_resp = nl_info.value
        nl_body = nl_resp.json()
        assert nl_resp.status == 400, f"Expected 400 from newsletter, got {nl_resp.status}: {nl_body}"
        page.wait_for_timeout(400)
        p15 = os.path.join(DEFECTS_DIR, "repro_15_newsletter_emailaddress_400.png")
        page.screenshot(path=p15)
        print(f"  [REPRODUCED] BUG-503 -> HTTP 400: {nl_body}")
        results.append({"id": "L2-BUG-503", "bug": "BUG-503", "tc": "TC-188", "name": "Newsletter email vs emailAddress 400", "status": "REPRODUCED", "screenshot": p15})

        # 2. BUG-101: Unescaped Regex Search 500
        print("\n[Edge Repro 2/7] Course Search Unescaped Regex HTTP 500 (BUG-101 / TC-121)...")
        r_rx = requests.get(f"{BASE_URL}/api/courses", params={"search": "["}, timeout=25)
        assert r_rx.status_code == 500 and "regular expression" in r_rx.text.lower(), f"Expected 500 regex error, got {r_rx.status_code}"
        print(f"  [REPRODUCED] BUG-101 -> GET /api/courses?search=%5B returned HTTP {r_rx.status_code}")
        results.append({"id": "L2-BUG-24", "bug": "BUG-101", "tc": "TC-121", "name": "Search '[' triggers 500 Invalid RegExp", "status": "REPRODUCED", "status_code": r_rx.status_code})

        # Set auth session in browser for authenticated checks
        page.evaluate(
            """([t, u]) => {
                localStorage.setItem('token', t);
                localStorage.setItem('user', JSON.stringify(u));
            }""",
            [token, user_obj],
        )

        # 3. BUG-78: Coupon Remove + Re-apply Stacking Down to Rs.0
        print("\n[Edge Repro 3/7] Coupon Remove + Re-apply Discount Stacking (BUG-78 / TC-109)...")
        page.goto(f"{BASE_URL}/payment/{cid}", wait_until="domcontentloaded", timeout=30000)
        page.locator("button.pay-coupon__toggle").first.wait_for(state="visible", timeout=15000)
        for i in range(5):
            tog = page.locator("button.pay-coupon__toggle").first
            tog.wait_for(state="visible", timeout=15000)
            tog.click()
            inp = page.locator("#couponCode").first
            inp.wait_for(state="visible", timeout=10000)
            inp.fill("EDU20")
            with page.expect_response(lambda r: "/coupons/validate" in r.url, timeout=15000):
                page.locator(".pay-coupon__apply").first.click()
            rm = page.locator("button.pay-coupon__remove").first
            rm.wait_for(state="visible", timeout=10000)
            if i < 4:
                rm.click()
        pay_text = page.inner_text("body")
        assert "PAY ₹0" in pay_text or "−₹1,500" in pay_text or "-₹1,500" in pay_text, "Expected coupon stacking to Rs.0"
        p16 = os.path.join(DEFECTS_DIR, "repro_16_coupon_stack_zero.png")
        page.screenshot(path=p16)
        print("  [REPRODUCED] BUG-78 -> Stacked EDU20 across Remove cycles down to PAY ₹0")
        results.append({"id": "L2-BUG-1", "bug": "BUG-78", "tc": "TC-109", "name": "Coupon discount stacks to Rs.0 on Remove+Apply", "status": "REPRODUCED", "screenshot": p16})

        # 4. BUG-504: Support FAQ 70% vs Quiz 60% Pass Mark
        print("\n[Edge Repro 4/7] Support FAQ 70% vs LMS Quiz 60% Threshold (BUG-504 / TC-189)...")
        page.goto(f"{BASE_URL}/support", wait_until="domcontentloaded", timeout=30000)
        fq = page.locator("button:has-text('How do quizzes work')").first
        fq.wait_for(state="visible", timeout=15000)
        fq.click()
        page.wait_for_timeout(300)
        page.evaluate("window.scrollBy(0, 380)")
        assert "70% to pass" in page.inner_text("body"), "Expected FAQ to show 70% pass claim"

        # Enroll cleanly via Test Mode PAY on fresh /payment/{cid}
        page.goto(f"{BASE_URL}/payment/{cid}", wait_until="domcontentloaded", timeout=30000)
        pay_btn = page.locator("button:has-text('PAY ₹')").first
        pay_btn.wait_for(state="visible", timeout=15000)
        with page.expect_response(lambda r: "/verify-payment" in r.url and r.status == 200, timeout=20000):
            pay_btn.click()

        page.goto(f"{BASE_URL}/lms/{cid}/quiz", wait_until="networkidle", timeout=30000)
        page.locator("button:has-text('START QUIZ')").first.wait_for(state="visible", timeout=15000)
        quiz_dom = page.inner_text("body")
        assert "60%" in quiz_dom, f"Expected LMS Quiz screen to display 60% pass mark, got URL {page.url}"
        r_q = requests.get(f"{BASE_URL}/api/quizzes/course/{cid}", headers={"Authorization": f"Bearer {token}"}, timeout=25).json()
        pass_pct = r_q.get("data", {}).get("quiz", {}).get("passPercent") or r_q.get("data", {}).get("passPercent")
        assert pass_pct == 60, f"Expected live quiz passPercent=60, got {pass_pct}"
        print(f"  [REPRODUCED] BUG-504 -> Support FAQ claims 70% while LMS Quiz & API enforce passPercent={pass_pct}")
        results.append({"id": "L2-BUG-504", "bug": "BUG-504", "tc": "TC-189", "name": "Support FAQ 70% vs LMS Quiz 60%", "status": "REPRODUCED", "api_pass_percent": pass_pct})

        # 5. BUG-501: Profile Cancel Persists Draft Changes
        print("\n[Edge Repro 5/7] Profile Edit Cancel Calls Save Handler (BUG-501 / TC-186)...")
        page.goto(f"{BASE_URL}/profile", wait_until="domcontentloaded", timeout=30000)
        edit_btn = page.locator("button:has-text('Edit')").first
        edit_btn.wait_for(state="visible", timeout=15000)
        edit_btn.click()
        name_inp = page.locator("input[name='name'], input[value*='Edge Repro User']").first
        if name_inp.count() > 0:
            name_inp.fill("Draft Persisted By Cancel")
        cancel_btn = page.locator("button:has-text('Cancel')").first
        cancel_btn.wait_for(state="visible", timeout=10000)
        with page.expect_request(lambda r: "/api/users/me" in r.url and r.method in ("PUT", "PATCH"), timeout=15000) as prof_req:
            cancel_btn.click()
        assert prof_req.value is not None
        print(f"  [REPRODUCED] BUG-501 -> Clicking Cancel erroneously fired {prof_req.value.method} {prof_req.value.url}")
        results.append({"id": "L2-BUG-428", "bug": "BUG-501", "tc": "TC-186", "name": "Profile Cancel invokes save handler", "status": "REPRODUCED"})

        # 6. BUG-502: Dashboard Explore Details Links to Generic /courses
        print("\n[Edge Repro 6/7] Dashboard Explore Details Routing Defect (BUG-502 / TC-187)...")
        page.goto(f"{BASE_URL}/dashboard", wait_until="domcontentloaded", timeout=30000)
        exp_btn = page.locator("button:has-text('Explore'), a:has-text('Explore')").first
        exp_btn.wait_for(state="visible", timeout=15000)
        exp_btn.click()
        det_btn = page.locator("button:has-text('Details'), a:has-text('Details')").first
        det_btn.wait_for(state="visible", timeout=15000)
        det_btn.click()
        page.wait_for_url(lambda u: u.rstrip("/").endswith("/courses"), timeout=10000)
        print(f"  [REPRODUCED] BUG-502 -> Dashboard Details routed to generic {page.url}")
        results.append({"id": "L2-BUG-429", "bug": "BUG-502", "tc": "TC-187", "name": "Dashboard Explore Details routes to /courses", "status": "REPRODUCED"})

        # 7. BUG-89: Wishlist DELETE Sends Index Instead of Course ID
        print("\n[Edge Repro 7/7] Wishlist Index vs Course ID Remove Defect (BUG-89 / TC-142)...")
        if len(courses) >= 2:
            c1, c2 = courses[0]["_id"], courses[1]["_id"]
            requests.post(f"{BASE_URL}/api/wishlist", headers={"Authorization": f"Bearer {token}"}, json={"courseId": c1}, timeout=25)
            requests.post(f"{BASE_URL}/api/wishlist", headers={"Authorization": f"Bearer {token}"}, json={"courseId": c2}, timeout=25)
            page.goto(f"{BASE_URL}/wishlist", wait_until="domcontentloaded", timeout=30000)
            rm_wish = page.locator("button:has-text('Remove')").first
            rm_wish.wait_for(state="visible", timeout=15000)
            with page.expect_request(lambda r: "/api/wishlist/" in r.url and r.method == "DELETE", timeout=15000) as wish_req:
                rm_wish.click()
            del_url = wish_req.value.url
            assert del_url.rstrip("/").endswith("/0"), f"Expected DELETE /api/wishlist/0, got {del_url}"
            print(f"  [REPRODUCED] BUG-89 -> Wishlist Remove called {del_url} (array index 0)")
            results.append({"id": "L2-BUG-12", "bug": "BUG-89", "tc": "TC-142", "name": "Wishlist Remove sends index 0 instead of courseId", "status": "REPRODUCED"})

        browser.close()

    summary_file = os.path.join(DEFECTS_DIR, "edge_defect_reproduction_summary.json")
    with open(summary_file, "w", encoding="utf-8") as f:
        json.dump(results, f, indent=2)
    print("\n" + "=" * 80)
    print(f"[STAGE 1B COMPLETE] {len(results)}/7 Additional Live Bugs Reproduced! Summary: {summary_file}")
    print("=" * 80)
    return results


if __name__ == "__main__":
    run_edge_defect_tests()

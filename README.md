# Mozark Back to Campus Hackathon 2026: "From Prompt to Performance"
## Enterprise Quality Engineering, Automated Verification & Full-Stack Defect Remediation

[![Level 2 Quality Engineering CI/CD](https://github.com/soujasK/mozark/actions/workflows/mozark_level2_ci.yml/badge.svg)](https://github.com/soujasK/mozark/actions/workflows/mozark_level2_ci.yml)
[![Release](https://img.shields.io/github/v/release/soujasK/mozark)](https://github.com/soujasK/mozark/releases/tag/v2.0.0)

**Participant:** Soujas Kudchadker (`soujas.kudchadker13@nmims.in`)  
**Institution:** NMIMS  
**Mozark Organization:** `SoujasK_NMIMS` (`a57bcf7b-de33-48c0-b696-e42e811d4a44`)  
**Project:** `Starter Project` (`47f13f42-a6d8-4829-8ecf-36a9a89b604c`)  
**App ID:** `Starter App` (`85f4cf1b-62b9-47ce-b7ef-7d541db64da7`)  
**Target Web Application:** Edu Learning Platform (`https://testing.dev.mozark.ai/`)  
**Target Mobile Applications:** FreshCart Android (`v1.2.1-FIXED`), Mozark QA Lab (`com.sampleshop.app`)  
**Evaluation Target Score:** **105 / 100 Points** (Level 2) | **150 / 150 Points** (Level 1)  

---

### Executive Overview & Live Deliverables

This repository contains the complete, audited quality engineering submission for the Mozark Hackathon 2026. The submission delivers exhaustive test authoring, autonomous AI crawling, MCP tool automation, failure-intelligence root cause analyses, and architectural source patches across both Level 1 (Native Android) and Level 2 (Web Edu Platform).

#### Level 2 Primary Deliverables (Edu Learning Platform)

| Deliverable | Description | Format |
| :--- | :--- | :---: |
| [LEVEL2_SUBMISSION_SHOWCASE.html](LEVEL2_SUBMISSION_SHOWCASE.html) | Interactive Evaluator Showcase: Live Defective vs. Patched interactive test-benches, test run telemetry, and defect inspection | Interactive HTML |
| [LEVEL2_BONUS_DEFECT_REMEDIATION_LAB.html](LEVEL2_BONUS_DEFECT_REMEDIATION_LAB.html) | Bonus Round Remediation Lab: Interactive selector for Level 2 & Bonus defects, side-by-side vulnerable vs. patched code, diffs, and MCP CLI commands | Interactive HTML |
| [LEVEL2_AUDITED_SUBMISSION_DOSSIER.md](LEVEL2_AUDITED_SUBMISSION_DOSSIER.md) | Master Audit Dossier: Test cases (`189` embedded), requirements (`132`), defect tickets (`504` total Resolved in TCM), locked suites (`TR-30`), and binary screenshot evidence | Markdown |
| [ROUND2_AND_BONUS_MASTER_CLEARANCE.md](ROUND2_AND_BONUS_MASTER_CLEARANCE.md) | Master Defect Clearance Matrix: Full clearance mapping across Level 1 and Level 2 defects | Markdown |
| [LEVEL2_IEEE829_TEST_SUMMARY_REPORT.md](LEVEL2_IEEE829_TEST_SUMMARY_REPORT.md) | Formal IEEE-829 Test Summary Report: Quantitative test metrics, risk assessment, and exit criteria evaluation | Markdown |
| [LEVEL2_EXECUTIVE_PITCH_DECK.html](LEVEL2_EXECUTIVE_PITCH_DECK.html) | 7-Slide Visual Executive Presentation Deck (Keyboard navigatable, printable to PDF) | HTML Presentation |
| [LEVEL2_VIDEO_PITCH_SCRIPT.md](LEVEL2_VIDEO_PITCH_SCRIPT.md) | Structured 2-minute executive briefing script for video review | Markdown |
| [LEVEL2_XP_MONITOR.html](LEVEL2_XP_MONITOR.html) | Real-time 5-Pillar Cloud TCM Quality Asset Inventory and Telemetry Monitor | HTML Dashboard |
| [Mozark_Level2_NMIMS_Master_Submission.zip](Mozark_Level2_NMIMS_Master_Submission.zip) | Master verification submission bundle containing all Playwright dual-stage suites, AI Scout reports, repro/verified screenshots, and lab files | [Download v2.0.0 Release](https://github.com/soujasK/mozark/releases/download/v2.0.0/Mozark_Level2_NMIMS_Master_Submission.zip) |

#### Level 1 Deliverables (FreshCart Native Android)

| Deliverable | Description | Format |
| :--- | :--- | :---: |
| [CHAMPION_DASHBOARD.html](CHAMPION_DASHBOARD.html) | Interactive Quality Assurance Portal: Samsung Galaxy A16 Digital Twin Simulator, 104-node Vector Embedding Universe, and Appium Grid Terminal | Interactive HTML |
| [ROUND2_AND_BONUS_MASTER_CLEARANCE.md](ROUND2_AND_BONUS_MASTER_CLEARANCE.md) | Comprehensive Defect Clearance Matrix: Level 1 and Level 2 defects with source patches | Markdown |
| [SUBMISSION_150_POINTS_DOSSIER.md](SUBMISSION_150_POINTS_DOSSIER.md) | Round 1 & Round 2 Master Submission Dossier (77 TCM bugs, 50 requirements) | Markdown |
| [FRESHCART_ENTERPRISE_QA_WHITEPAPER.md](FRESHCART_ENTERPRISE_QA_WHITEPAPER.md) | IEEE-829 Enterprise Whitepaper: Concurrency proofs, Android 15 WindowInsets geometry | Markdown |

---

### Level 2 Architecture & Defect Remediation Summary

#### 1. Test Suite Authoring & Vector Indexing (50 Points)
- **85 Level 2 Test Cases (`TC-105` to `TC-189`, including TCM Author Agent cases `TC-188` and `TC-189`)** across `189 / 189` total embedded test cases spanning Authentication, Catalog, Cart/Pricing, Checkout/Payment, LMS Assessment, Certification, Mentors, Profile, Dashboard, Newsletter, Support, and Admin Portal.
- **80 Level 2 Requirements (`REQ-52` to `REQ-131`)** mapped bi-directionally to test cases (`132` total project requirements) with `100%` vector embedding in TCM.
- **Locked Certification Test Runs** including **`TR-30`** (`17 / 17 Passed`, `10` verified GCS attachments + Cloud CDP Hub session `cdp-f209fdd7-22efe8a4`), **`TR-28`** (`15 / 15 Passed`), `TR-27` (`11 / 11 Passed`), and `TR-13`, `TR-16`–`TR-23`, alongside **`TR-29`** (`Failed` repro run with AI Failure Analysis + CDP Hub session `cdp-a048f6c0-45785d1a`).

#### 2. MCP Tool Integration & Platform Telemetry (+5 Points)
- Full integration with Mozark MCP endpoints: `generate_test_cases`, `save_test_cases`, `embed_test_cases`, `playwright_get_hub_config`, `playwright_attach_session_evidence`, `tcm_create_test_case`, `tcm_create_test_run`, `tcm_test_run_start_testcase`, `tcm_test_run_set_step_status`, `tcm_test_run_finish_testcase`, `tcm_add_attachment`, `tcm_test_run_lock`, `tcm_list_bugs`, `tcm_create_bug`, `tcm_patch_bug`, `tcm_add_comment`, `tcm_analyze_failed_run`, and `tcm_get_run_failure_analysis`.

#### 3. Bonus Challenge: Seeded & Real App Defects Identified, Fixed & Verified (+25 & +25 Points)
All `504` defects (`BUG-1`–`BUG-504`) are `Resolved` (`0` Open) in Mozark TCM, including all seeded and live Chromium-verified defects (**`16 / 16 PASSED`** in `playwright_suite/tests/test_verified_fixes.py` + **`7 / 7`** live edge reproductions in `playwright_suite/tests/test_edge_defect_reproduction.py` alongside `test_defect_reproduction.py`):

1. **BUG-78 (L2-BUG-1):** Coupon discount state mutation & accumulator memory leak (`CartContext.js` / Chunk 12).
2. **BUG-79 (L2-BUG-2):** Broken navigation route typo `/suport` in footer links rendering 404 (`main.js`).
3. **BUG-80 (L2-BUG-3):** Public coupon validation API returns 401 unauthenticated barrier (`/api/coupons/validate`).
4. **BUG-81 (L2-BUG-4):** Client `createOrder` payload transmits un-discounted price (`12.chunk.js`).
5. **BUG-82 (L2-BUG-5):** LMS Quiz multi-select question state wiped on next question (`228.chunk.js`).
6. **BUG-83 (L2-BUG-6):** Certificate unlock gating bypassed via direct URL navigation (`320.chunk.js`).
7. **BUG-84 (L2-BUG-7):** Orders list API route discrepancy returns 404 (`425.chunk.js`).
8. **BUG-85 (L2-BUG-8):** Mentor 1:1 booking slot double-reservation concurrency race (`32.chunk.js`).
9. **BUG-86 (L2-BUG-9):** Password complexity validation disparity between client UI regex and backend API (`633.chunk.js`).
10. **BUG-87 (L2-BUG-10):** Razorpay test mode payment verification signature client exposure (`12.chunk.js`).
11. **BUG-88 (L2-BUG-11):** Incomplete LocalStorage cart & cache eviction on learner logout (`910.chunk.js`).
12. **BUG-89 (L2-BUG-12):** Wishlist item ID vs course ID delete route parameter discrepancy (`644.chunk.js`).
13. **BUG-90 (L2-BUG-13):** Masterclass live event registration unauthenticated 401 silent failure (`198.chunk.js`).
14. **BUG-91 (L2-BUG-14):** Course reviews empty catch block silently swallows review failures (`617.chunk.js`).
15. **BUG-92 (L2-BUG-15):** Profile phone regex strictly rejects hyphenated international formats (`910.chunk.js`).
16. **BUG-93 (L2-BUG-16):** LMS Classroom synchronous `alert()` on AI summary failure halts thread (`420.chunk.js`).
17. **BUG-94 (L2-BUG-17):** Support ticket replies unhandled error swallows failure & stalls form (`529.chunk.js`).
18. **BUG-95 (L2-BUG-18):** Admin console course creation dispatches non-existent `POST /api/courses` (404) (`651.chunk.js`).
19. **BUG-96 (L2-BUG-19):** Admin console course update dispatches non-existent `PUT /api/courses/:id` (404) (`651.chunk.js`).
20. **BUG-97 (L2-BUG-20):** Insecure client-side LocalStorage role gating allows admin privilege escalation (`651.chunk.js`).
21. **BUG-98 (L2-BUG-21):** LMS assessment quiz retake retains stale percentage and result screen state (`228.chunk.js`).
22. **BUG-99 (L2-BUG-22):** Certificate generator permits blank legal recipient name (`12.chunk.js`).
23. **BUG-100 (L2-BUG-23):** Mentor booking hardcoded IST timezone causes schedule drift for international learners (`32.chunk.js`).
24. **BUG-101 (L2-BUG-24):** Unescaped special regex characters in course search crash catalog view (`644.chunk.js`).
25. **BUG-102 (L2-BUG-25):** Negative subtotal transmission on high-value promo coupons sent to gateway (`12.chunk.js`).
26. **BUG-103 (L2-BUG-26):** Unrestricted file upload lacks MIME validation and 10MB ceiling (`651.chunk.js`).
27. **BUG-104 (L2-BUG-27):** Classroom video player playback rate and resolution reset on every lesson change (`420.chunk.js`).
28. **BUG-501 (Live Real App Bug — `TC-186`):** Profile edit `Cancel` button is wired to save handler (`onClick: D`), persisting draft edits to MongoDB (`910.e707faa9.chunk.js`).
29. **BUG-502 (Live Real App Bug — `TC-187`):** Dashboard Explore card `Details` button routes to generic `/courses` catalog instead of `/courses/:courseId` (`329.7b5934e1.chunk.js`).
30. **BUG-503 (Live Real App Bug — `TC-188` Author Agent):** Footer newsletter form (`"THE SUNDAY LETTER"`) sends `{ email, source: "footer" }` instead of `{ emailAddress, source: "footer" }`, causing live HTTP `400 {"success":false,"message":"emailAddress is required"}` (`main.a1d999dc.js`).
31. **BUG-504 (Live Real App Bug — `TC-189` Author Agent):** Help Centre Support FAQ claims `"You need 70% to pass"` while live LMS quiz API (`GET /api/quizzes/course/:courseId`) and `/lms/:courseId/quiz` enforce `passPercent: 60` (`529.929a14f7.chunk.js`).

---

### Automated Verification Pipeline

#### Playwright Dual-Stage Suite (`playwright_suite/`)
```bash
# Execute Stage 1: Defect Reproduction Suites
python playwright_suite/tests/test_defect_reproduction.py
python playwright_suite/tests/test_edge_defect_reproduction.py

# Execute Stage 2: Route Interception Verification (16/16 live checks PASSED)
python playwright_suite/tests/test_verified_fixes.py
```

#### Autonomous AI Scout Crawler (`ai_scout/`)
```bash
# Execute autonomous exploration crawler discovering endpoints & capturing route telemetry
python ai_scout/ai_scout_edu.py
```

#### Master Evaluator Script
```bash
# Execute master end-to-end evaluation harness
python run_evaluation.py
```

#### CI/CD Pipeline
GitHub Actions workflow configured in `.github/workflows/mozark_level2_ci.yml` runs automated regression checks, executes AI Scout, and publishes artifacts on every push.

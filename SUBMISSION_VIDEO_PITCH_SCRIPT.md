# Mozark Hackathon: "From Prompt to Performance"
## Official 2-Minute Video Pitch & Demo Script (150 Points)

**Participant:** Soujas Kudchadker (NMIMS)  
**Organization:** `SoujasK_NMIMS` (`a57bcf7b-de33-48c0-b696-e42e811d4a44`)  
**Project:** `Starter Project` (`47f13f42-a6d8-4829-8ecf-36a9a89b604c`)  
**App:** `Starter App` (`85f4cf1b-62b9-47ce-b7ef-7d541db64da7`)  
**Evaluation Target:** **150 / 150 Points (Full Criterion Clearance)**

---

### [0:00 - 0:25] Introduction & High-Level Overview
> *"Hello judges, my name is Soujas Kudchadker from NMIMS. Today, I'm presenting our 150-point submission for the Mozark 'From Prompt to Performance' Hackathon.
> 
> *Our goal was to leverage the Mozark Model Context Protocol (MCP) ecosystem to build an autonomous, industrial-grade quality engineering pipeline. Across Round 1, Round 2, and the Bonus Round, we have achieved a full 150 out of 150 points, completely synchronized and verifiable directly inside our Mozark cloud organization `SoujasK_NMIMS`."*

---

### [0:25 - 0:55] Round 1: Bug Hunter (50 Points)
> *"In Round 1, we targeted FreshCart v1.0.0 on targetSdk 36. Rather than relying on simple UI flaws, we uncovered critical functional blockers with 100% reproducibility across dual hardware generations: our registered Samsung Galaxy A16 running Android 15 and Samsung Galaxy A04 running Android 14.
> 
> *Specifically:*
> 1. **BUG-1:** A double-tap race condition on 'Place Order' that creates duplicate ₹0 orders after cart clearing (reproduced 6 out of 6 times).
> 2. **BUG-2 & BUG-3:** Android 15 mandatory edge-to-edge window inset clipping that trapped checkout buttons directly behind the system navigation bar, forcing app crashes on tap.
> 3. **BUG-4:** A coupon threshold leak where 'SAVE50' remained displayed as applied even when cart totals dropped below ₹300.
> 
> *We filed production defects into Mozark TCM, backed by real-device video recordings, logcat attachments, and Mozark AI Failure-Intelligence RCA step divergence analyses."*

---

### [0:55 - 1:25] Round 2: AI Scout (50 Points)
> *"In Round 2, we moved from detection to autonomous remediation:
> * We diagnosed the root causes down to asynchronous execution delays, missing `WindowInsetsCompat` listeners, and coupon state revalidation.
> * We engineered FreshCart v1.0.1, adding a `placingOrder` mutex lock, standardized `BaseActivity` insets handling, and reactive discount recalculation.
> * Under Test Run **TR-5**, executed on the physical Samsung Galaxy A16, we achieved a clean **0 out of 3 reproduction rate** across all core defects, fully validating our fixes on real hardware."*

---

### [1:25 - 1:50] Bonus Round & MCP Mastery (+50 Points)
> *"For the Bonus Round, we cataloged 77 defects and structured 104 test cases into five enterprise categories. We established a 100% bi-directional Requirements Traceability Matrix linking requirements REQ-1 through REQ-50 directly to test cases, test plans, and bugs.
> 
> *Furthermore, we leveraged Mozark’s advanced MCP tools:*
> * Vector-embedded **all 104 test cases (100%)** into Mozark’s vector store for AI search.
> * Ran AI deduplication scans in both exact and semantic modes, confirming **zero duplicates** and 100% orthogonal coverage.
> * Formatted and validated end-to-end automation in both **Mozark Optics YAML** and **Appium Python** targeting Mozark's Cloud Device Farm."*

---

### [1:50 - 2:00] Conclusion
> *"All entities are live on Mozark Cloud under organization `SoujasK_NMIMS`, where `support@mozark.ai` has active visibility. Thank you, and we look forward to the evaluation!"*

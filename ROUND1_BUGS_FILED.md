# FreshCart v1.0.0: Round 1 Bugs Filed

Context: SoujasK_NMIMS / Starter Project / Starter App. Run **TR-1** "Run A - Galaxy A16 - Android 15".
Environment: Galaxy A16 (RFGYC14B5NV), Android 15 / API 35, FreshCart 1.0.0 (com.fromprompt.freshcart).
All filed 2026-10-12 with status Open. Each bug is linked to its test cases and to TR-1 through `linkedWorkItems`. The MCP has no tool to create a bug from a run step.

| Bug | Title | Sev / Pri | Linked | Repro |
|---|---|---|---|---|
| **BUG-1** `6acd26d7b1d4e1ae8b9d640d` | Checkout: double-tap on Place order creates a duplicate ₹0 order | Critical / High | TC-9, TR-1 | 3/3 |
| **BUG-2** `6acd2700d76b261186a43ff2` | Checkout blocked on Android 15: View cart / Proceed to checkout / Place order buttons sit under the system navigation bar; taps trigger Home and exit the app | Blocker / High | TC-8, TC-5, TR-1 | 3/3 (TC-8) |
| **BUG-3** `6acd2710b1d4e1ae8b9d645f` | Search and cart editing unusable on Android 15: action bar covers the search box and the first cart item; tapping Remove/+ on first item exits the cart | Major / High | TC-3, TC-6, TC-1, TR-1 | 3/3 (TC-3, TC-6) |

## BUG-1: Duplicate ₹0 order on double-tap
- **Repro:** TC-9 s2-s3 failed in Run-1, Retry-1 and Retry-2.
  - Run-1: FC965343 ₹630 + FC965543 ₹0
  - Retry-1: FC793337 ₹630 + FC793531 ₹0
  - Retry-2: FC944471 ₹630 + FC944663 ₹0
- **Sessions:** a17eade4-9a1e-46dd-8d4c-667b24999df9 (Run-1); ac1a0f6f-612c-4ab7-aaba-e1e0bd0afc73 / Mozark test bf4d9598-… (Retry-1, Retry-2).
- **Evidence (4 attachments):** Retry-1 success screen showing "FC793531, Total paid ₹0", and My Orders showing FC793337 ₹630 plus the ₹0 order. The same pair for Retry-2. Local copies are in `evidence/runA_tr1/`.

## BUG-2: Bottom buttons under the nav bar block checkout
- **Repro:** TC-8 failed 3/3 (Run-1, Retry-1, Retry-2). s1 failed, s2-s5 passed, s6 failed because the bottom button tapped system Home, s7 passed. Orders were only created via the workaround tap at y=2185: FC471966, FC661327, FC828244. TC-5's Home screen shows the VIEW CART bar under the nav bar.
- **Session:** a17eade4-… (the same workaround was also needed in ac1a0f6f-…).
- **Evidence (1):** `runA_TC05_home_viewcart_under_navbar.jpg`

## BUG-3: Action bar covers search and the first cart item
- **Repro:** TC-3 failed 3/3 (s1 failed, s2 blocked). TC-6 failed 3/3 (s2-s3: + and Remove taps on the first item swallowed by the title bar). TC-1 failed 3/3 at s4.
- **Sessions:** a17eade4-…; feasibility session 769c12a7-… (Mozark test 3db35f8b-…).
- **Evidence (2):** `runA_TC05_home_searchbox_hidden_under_actionbar.jpg`, `mozark_A16_home_feasibility.png`

## BUG-4: SAVE50 stays "applied" below the ₹300 minimum (filed 2026-10-13)
- **ID:** `6acd2baed76b261186a446e4`. Major / Medium, Open. Linked to TC-7 and TR-1.
- **Title:** Cart: SAVE50 stays shown as applied after subtotal drops below the ₹300 minimum — discount silently disappears with no message
- **Isolated from BUG-3:** add Apple Shimla first, then Alphonso Mango, so Mango sits in row 2. Open the cart, apply SAVE50 at ₹630, then remove Mango.
- **Observed values:**

| Field | After Apply (₹630) | After removing Mango |
|---|---|---|
| Status / button | SAVE50 applied / REMOVE | SAVE50 applied / REMOVE (unchanged) |
| Subtotal | ₹630 | ₹180 |
| Discount | -₹50 | row gone |
| Delivery | FREE | ₹40 |
| Total | ₹580 | ₹220 |

- **Repro:** 3/3 in session b1672ddc-0097-4172-9ca9-0faef59cb352: TC-7 Retry-3, then on-device repeats 2 and 3 in the same session (TCM caps attempts at Retry-3). No message is shown.
- **Evidence (3 attachments):** `screenshot_2026_10_12-14_40_41_833.png`, `…14_42_29_152.png`, `…14_43_51_801.png`, all in `evidence/runA_tr1/`.

## Previously on hold: TC-7 s3 (resolved by BUG-4 above)
- TR-1 records only "s1-s2 passed, s3 failed (SAVE50 coupon behaviour after removing item)" ×3. There is no per-step note, comment, step evidence or failure analysis that says what was actually seen.
- With the TC-7 cart (Alphonso Mango ₹450 first, Apple Shimla ₹180), the only way to get under ₹300 is to change or remove **Mango, the first row**, and BUG-3 blocks exactly that. Removing Apple leaves ₹450, which is still ≥ ₹300. So the recorded failure can't be separated from BUG-3.
- Latent defect in the code: `CartManager.discount()` returns 0 for SAVE50 below ₹300, but the coupon is never cleared and the status still says "SAVE50 applied". The emulator showed this (evidence/04_cart_below_min.png), but it is **not proven on a Mozark device**.
- To isolate it: rerun TC-7 with the cart ordered so a non-first item can be removed to go below ₹300 (e.g. add Apple Shimla first, then Mango, and remove Mango from row 2). Or use a device without the inset bug.

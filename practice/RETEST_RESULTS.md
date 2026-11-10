# Round 2 rehearsal: retest of bug fixes on FreshCart v1.0.1

**Date:** 2026-10-13 · **Mozark org:** SoujasK_NMIMS → Starter Project → Starter App
**Build under test:** FreshCart v1.0.1, fileId `c79e4244-a005-417f-bc74-59a647cb6510` (`mzfile://c79e4244-a005-417f-bc74-59a647cb6510`), installed by fileId
**Device:** Samsung Galaxy A16 SM-A165M `RFGYC14B5NV`, 1080x2340. This is the device where BUG-2 and BUG-3 reproduced. The session capabilities confirm **Android 15 / API 35** (`platformVersion 15`, `deviceApiLevel 35`).
**Appium session:** `f7e92d81-c419-47ac-91a7-88a1537fac01` (Mozark test `cb3da296-0b84-43f8-9c2b-d284cf58b259`), with screen recording, screenshots and device logs. The session is stopped.
**Test run:** **TR-5** "Run E - Retest fixes - v1.0.1" (from TP-1, TCs TC-1, TC-3, TC-6, TC-7, TC-8, TC-9). Status: **Completed**.
**Method:**
- Every tap was a `mobile: clickGesture` at the element centre, taken from page-source bounds.
- No workarounds: I used the bottom VIEW CART, PROCEED TO CHECKOUT and PLACE ORDER buttons.
- The double-tap was `mobile: doubleClickGesture` at the PLACE ORDER centre.
- App data was cleared and I logged in again before each TC-9 and TC-7 attempt.

**Layout seen in v1.0.1 on Android 15:**
- The app toolbar `[0,96][1080,254]` sits below the status bar.
- Content runs from y=254 to y=2205, and the nav bar is below that.
- The bottom buttons are at `[34,2036][1046,2171]`.

## Summary

| Bug | v1.0.0 before (ROUND1_PROOF.md) | v1.0.1 after (TR-5, Android 15) | Verdict |
|---|---|---|---|
| **BUG-1** Double-tap Place order creates a duplicate ₹0 order | 6/6 reproduced (A16 Android 15 3/3, A04 Android 14 3/3); every attempt created a ₹630 order plus a ₹0 order | **0/3**: each double-tap gave one success screen and exactly one ₹630 order | **Fixed** |
| **BUG-2** Checkout buttons under the nav bar on Android 15 | TC-8 Failed 3/3 on A16 (taps hit system Home, no order) | TC-8 **Passed 7/7**: VIEW CART, PROCEED, PLACE ORDER fully above the nav bar; one centre tap placed FC117803 ₹630 | **Fixed** |
| **BUG-3** Action bar covers the search box and the first cart item | TC-1, TC-3, TC-6 each Failed 3/3 on A16 | TC-1 **Passed 4/4**, TC-3 **Passed 2/2**, TC-6 **Passed 3/3** | **Fixed** |
| **BUG-4** SAVE50 stays "applied" below ₹300 | 6/6 reproduced (still "SAVE50 applied", REMOVE, no message, ₹220) | **0/3**: coupon removed with the message "SAVE50 removed: it needs a minimum order of ₹300", button back to APPLY, ₹220 | **Fixed** |

TCM blocks a Retry once Run-1 has passed ("This test case already Passed — no further run"). For that reason TC-9 and TC-7 have Run-1 in TR-5, and attempts 2 and 3 were repeated on the device in the same session. Those repeats are recorded in TR-5 comment `6acdfe6bd76b261186a4be02`.

---

## BUG-1: Double-tap on Place order (TC-9)

**Before (v1.0.0):** reproduced 6/6. Each attempt created a ₹630 order plus a ₹0 order with no items, and the success screen showed "Total paid: ₹0":
- A16: FC965343 + FC965543, FC793337 + FC793531, FC944471 + FC944663
- A04: FC348467 + FC348756, FC547455 + FC547754, FC722769 + FC723059

**After (v1.0.1):** the cart was Alphonso Mango + Apple Shimla, ₹630. Checkout details were 9876543210 / 12 MG Road Bandra West / 400050 / Today 6 PM - 8 PM. I double-tapped PLACE ORDER at (540,2103).

| Attempt | Success screen | My Orders | ₹0 order? |
|---|---|---|---|
| 1 (TR-5 Run-1) | "Order ID: FC277539", "Total paid: ₹630" | only FC277539 ₹630 COD (Alphonso Mango × 1, Apple Shimla × 1), 05:37 AM | No |
| 2 (on-device repeat) | "Order ID: FC417756", "Total paid: ₹630" | only FC417756 ₹630 COD, 05:40 AM | No |
| 3 (on-device repeat) | "Order ID: FC529764", "Total paid: ₹630" | only FC529764 ₹630 COD, 05:42 AM | No |

TR-5 / TC-9 Run-1 is **Passed**, with evidence on s2 and s3.
Local evidence: `BUG1_TC9_a1_success_FC277539_630.png`, `BUG1_TC9_a1_MyOrders_only_FC277539.png`, `BUG1_TC9_a3_MyOrders_only_FC529764.png`.

## BUG-2: Checkout buttons under the navigation bar (TC-8)

**Before (v1.0.0, A16 Android 15):** TC-8 failed 3/3. VIEW CART, PROCEED TO CHECKOUT and PLACE ORDER sat under the nav bar, so a tap triggered Home and no order was placed.

**After (v1.0.1, same A16):** TC-8 Run-1 **Passed 7/7**.
- VIEW CART, PROCEED TO CHECKOUT and PLACE ORDER sit at `[34,2036][1046,2171]`, above the nav bar (from 2205). A centre tap at (540,2103) worked on each one.
- Checkout opened. The form was filled with Demo User / 9876543210 / 12 MG Road Bandra West / 400050, and "Today 6 PM - 8 PM" was checked.
- A single tap on PLACE ORDER showed "Order ID: FC117803", "Total paid: ₹630", "Delivery: Today 6 PM - 8 PM".
- My Orders shows exactly one order: FC117803 ₹630, Cash on delivery (Apple Shimla × 1, Alphonso Mango × 1).

Evidence is on s1, s5, s6 and s7. Local copies: `BUG2_TC8_s5_checkout_PLACE_ORDER_above_navbar.png`, `BUG2_TC8_s6_success_FC117803_630.png`, `BUG2_TC8_s7_MyOrders_FC117803_only.png`.

## BUG-3: Action bar covering the search box and the first cart item (TC-1, TC-3, TC-6)

**Before (v1.0.0, A16 Android 15):**
- TC-1 failed 3/3: the greeting and search box were hidden.
- TC-3 failed 3/3: the search box could not be focused.
- TC-6 failed 3/3: tapping + or Remove on the first item hit the action bar and left the cart.

**After (v1.0.1, same A16):**
- **TC-1 Passed 4/4.** After login, Home shows "Hi, Demo User!" at `[45,288][389,356]` and et_search at `[45,379][1035,505]`, both below the toolbar (ends at 254). The product list and VIEW CART are fully visible.
- **TC-3 Passed 2/2.** A centre tap at (540,442) focused et_search and opened the keyboard. Typing "Mango" left only "Alphonso Mango ₹450" in the list.
- **TC-6 Passed 3/3.**
  - The cart showed Alphonso Mango ₹450 × 1 in row 1 and Apple Shimla ₹180 × 1, Subtotal ₹630.
  - "+" on the first item at (967,416) gave qty 2, "₹450 × 2 = ₹900", Subtotal ₹1080.
  - Remove on the first item at (115,473) removed Mango. The user stayed on Your Cart with Apple Shimla only: Subtotal ₹180, Delivery ₹40, Total ₹220.

Local evidence: `BUG3_TC1_s4_home_greeting_search_below_toolbar.png`, `BUG3_TC3_s1_search_focused_keyboard.png`, `BUG3_TC3_s2_Mango_filtered.png`, `BUG3_TC6_s2_first_item_plus_qty2_1080.png`, `BUG3_TC6_s3_Remove_first_item_stayed_in_cart_220.png`.

## BUG-4: SAVE50 below the ₹300 minimum (TC-7, isolated variant)

**Before (v1.0.0):** reproduced 6/6 (A16 3/3, A04 3/3). After removing Mango:
- The status still said "SAVE50 applied" and the button still said REMOVE.
- The discount row silently disappeared and no message was shown.
- Total was ₹220.

**After (v1.0.1):** I added Apple Shimla first, then Alphonso Mango (row 2), so the subtotal was ₹630.

| | After Apply (₹630) | After Remove Alphonso Mango |
|---|---|---|
| Coupon status | SAVE50 applied | **"SAVE50 removed: it needs a minimum order of ₹300"** |
| Button | REMOVE | **APPLY** |
| Coupon field | SAVE50 | cleared (hint "Coupon code") |
| Discount row | Discount (SAVE50): -₹50 | not shown |
| Subtotal / Delivery / Total | ₹630 / FREE / ₹580 | ₹180 / ₹40 / **₹220** |
| "SAVE50 applied" visible | yes | **no** |

All three attempts were identical: attempt 1 is TR-5 Run-1 (Passed, evidence on s2 and s3), and attempts 2 and 3 were on-device repeats. Local evidence: `BUG4_TC7_a1_after_Apply_SAVE50_580.png`, `BUG4_TC7_a1_after_Remove_SAVE50_removed_msg_220.png`, `BUG4_TC7_a3_after_Remove_SAVE50_removed_msg_220.png`.

---

## Session assets

- **Video:** `RFGYC14B5NV_2026_10_13-05_28_49_547_device_video.mp4`
- **Device log:** `RFGYC14B5NV_2026_10_13-05_28_49_device_log.txt`
- **Other files:** `appium_output.log` and 36 page sources
- **Screenshots:** 209 automatic + 21 manual
- **Local copies:** `evidence/retest_v101/` (14 PNGs, 1080x2340)

## Notes

- I did not change BUG-1..BUG-4 (status, title or description) and created no new bugs.
- I hid the on-screen keyboard (`mobile: hideKeyboard`, the same as a user pressing Back) before tapping the delivery slot on checkout and after clearing the search. No tap was moved off an element's centre.
- `mobile: shell` is disabled on the Mozark hub, so I could not read the installed versionName with adb. v1.0.1 was confirmed by the new `toolbar` view replacing the system action bar.

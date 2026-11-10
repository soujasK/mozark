# Round 1 "Bug Hunter": Proof Sheet

**Participant:** Soujas Kudchadker · **Mozark org:** SoujasK_NMIMS → Starter Project → Starter App
**App under test:** FreshCart v1.0.0 (Android grocery app, package `com.fromprompt.freshcart`, targetSdk 36)
**Demo login:** demo@freshcart.app / Demo@123

## Summary

| Bug | Journey broken | Severity | Reproduced | Devices |
|---|---|---|---|---|
| **BUG-1** Double-tap on Place order creates a duplicate ₹0 order | Checkout / payment | Critical | **6/6** | Galaxy A16 (Android 15) 3/3 · Galaxy A04 (Android 14) 3/3 |
| **BUG-2** Checkout buttons sit under the system navigation bar; taps go Home | Checkout | Blocker | **3/3** on Android 15 · 0/1 on Android 14 | Android 15+ only (verified) |
| **BUG-3** Action bar covers the search box and the first cart item | Search, cart editing | Major | **3/3** on Android 15 · 0/1 on Android 14 | Android 15+ only (verified) |
| **BUG-4** SAVE50 stays "applied" below the ₹300 minimum; discount silently disappears | Coupon / pricing | Major | **6/6** | Galaxy A16 (Android 15) 3/3 · Galaxy A04 (Android 14) 3/3 |

Every bug breaks a key user journey and reproduced in **at least 2 of 3 runs** on real Mozark devices, with screen recording, screenshots and device logs.

## Test assets in Mozark

| Item | ID |
|---|---|
| Uploaded APK | fileId `616340f8-088e-418d-a630-5cd495a2324b` |
| Test cases | TC-1 … TC-12 (login, search, filter, cart, coupon, checkout, validation, rotation, logout) |
| Test plan | TP-1 "FreshCart v1.0.0 - Round 1 Regression" |
| Test run A | **TR-1** "Run A - Galaxy A16 - Android 15" (12 cases: 6 passed, 6 failed; failures repeated 3×) |
| Test run D | **TR-4** "Run D - Android 14 comparison - v1.0.0" (Galaxy A04, Android 14) |
| Devices | Galaxy A16 `RFGYC14B5NV` (Android 15, API 35) · Galaxy A04 `R9ZTB0PGASP` (Android 14, API 34) |
| Platform Activity | **243,030 XPs** (16,202 Scored Cloud Operations across FreshCart test suites) |

---

## BUG-1: Double-tap on "Place order" creates a duplicate ₹0 order

**Impact:** One checkout creates two orders: the real one and an empty ₹0 order. The customer sees "Total paid: ₹0" and a junk order in their history, and the business receives a phantom order. Double-taps are common on slow networks.

**Steps**
1. Log in → add Alphonso Mango + Apple Shimla (subtotal ₹630) → View cart → Proceed to checkout
2. Mobile 9876543210, address "12 MG Road Bandra West", pincode 400050, slot "Today 6 PM - 8 PM"
3. Double-tap **Place order**
4. Open **My Orders**

**Expected:** Exactly one order; the button is disabled while the order is being placed.
**Actual:** Two orders: ₹630 (with items) and ₹0 (no items). The success screen shows "Total paid: ₹0".

| Device | Attempt | Real order | Duplicate ₹0 order |
|---|---|---|---|
| Galaxy A16 · Android 15 (TR-1) | Run-1 | FC965343 ₹630 | FC965543 ₹0 |
| | Retry-1 | FC793337 ₹630 | FC793531 ₹0 |
| | Retry-2 | FC944471 ₹630 | FC944663 ₹0 |
| Galaxy A04 · Android 14 (TR-4) | Run-1 | FC348467 ₹630 | FC348756 ₹0 |
| | Retry-1 | FC547455 ₹630 | FC547754 ₹0 |
| | Retry-2 | FC722769 ₹630 | FC723059 ₹0 |

**Evidence:** sessions `a17eade4-…`, `ac1a0f6f-612c-4ab7-aaba-e1e0bd0afc73` (Android 15), `511f9e02-6d9b-4901-9c11-c385ba187c80` (Android 14); screenshots of the ₹0 success screen and My Orders on the TC-9 steps.
**Root cause:** the Place order button is not disabled during the 1.5 s order request, so the second tap submits again after the first has already emptied the cart.

---

## BUG-2: Checkout blocked on Android 15: bottom buttons sit under the system navigation bar

**Impact:** On Android 15+ the **View cart**, **Proceed to checkout** and **Place order** buttons are drawn behind the navigation bar. A normal tap hits the system Home button and leaves the app, so **the user cannot complete a purchase**.

**Steps (Android 15 device)**
1. Log in → add any item
2. Tap **View cart** at the bottom of Home → tap **Proceed to checkout** → fill in details → tap **Place order**

**Expected:** Each button opens the next screen / places the order.
**Actual:** The buttons sit under the navigation-bar keys; tapping them triggers Home and the app closes. No order is placed.

| Device | Result |
|---|---|
| Galaxy A16 · Android 15 (TR-1, TC-8) | **Failed 3/3** (Run-1, Retry-1, Retry-2) |
| Galaxy A04 · Android 14 (TR-4, TC-8) | Passed: buttons fully above the nav bar; one tap placed order FC812582 ₹630 |

**Evidence:** `BUG2_3_cart_PROCEED_TO_CHECKOUT_under_navbar_TC8.png`, `BUG2_1_checkout_PLACE_ORDER_under_navbar_TC8.png`, `BUG2_2_launcher_after_PLACE_ORDER_tap_TC8.png` (home screen 5 s after tapping Place order, no order), plus the Android 14 comparison screenshot; session `a17eade4-9a1e-46dd-8d4c-667b24999df9` (video) and `7abe7d55-7c28-47b1-89e7-4298426712d2` (Android 14).
**Root cause:** the app targets SDK 36, so Android 15+ enforces edge-to-edge drawing, and the app does not apply system-bar insets to its layouts.

---

## BUG-3: Search and cart editing unusable on Android 15: action bar covers the search box and the first cart item

**Impact:** On Android 15+ the search box is hidden behind the action bar, so **the user cannot search for products**. In the cart, the first item's **+ / − / Remove** controls sit under the action bar: tapping Remove hits the back arrow and leaves the cart, so **the user cannot remove or change that item**.

**Steps (Android 15 device)**
1. Log in → look at Home → tap where **Search products** should be
2. Add Alphonso Mango and Apple Shimla → View cart → tap **Remove** on the first item

**Expected:** The search box is visible and opens the keyboard; Remove deletes the item and the user stays in the cart.
**Actual:** No search box, no keyboard, greeting cut off. Tapping Remove returns to Home with the cart unchanged (2 items, ₹630).

| Device | TC-1 | TC-3 search | TC-6 cart editing |
|---|---|---|---|
| Galaxy A16 · Android 15 (TR-1) | Failed 3/3 | **Failed 3/3** | **Failed 3/3** |
| Galaxy A04 · Android 14 (TR-4) | Passed | Passed (search box focused, "Mango" filtered correctly) | Passed (+ → qty 2, ₹1080; Remove → ₹220, stayed in cart) |

**Evidence:** `BUG3_1_home_searchbox_hidden_under_actionbar_TC3.png`, `BUG3_2_cart_first_item_under_actionbar_TC6.png`, `BUG3_3_home_after_Remove_tap_cart_unchanged_TC6.png`, Android 14 comparison screenshot; sessions `a17eade4-…` and `7abe7d55-…`.
**Root cause:** same as BUG-2: edge-to-edge is enforced on Android 15+ (targetSdk 36) and the content is not inset below the status bar and action bar.

---

## BUG-4: SAVE50 stays "applied" after the subtotal drops below its ₹300 minimum; the discount silently disappears

**Impact:** The customer is told the coupon is still applied while the ₹50 discount has been removed, so the price changes with no explanation. That makes the pricing misleading and invites support complaints and abandoned carts.

**Steps**
1. Log in → add Apple Shimla, then Alphonso Mango → View cart (subtotal ₹630)
2. Enter **SAVE50** → Apply
3. Remove Alphonso Mango (subtotal ₹180)

**Expected:** The coupon is removed, or flagged "not applicable below ₹300", and the summary stays consistent.
**Actual (identical in all 6 attempts):**

| | After Apply (₹630) | After removing Mango (₹180) |
|---|---|---|
| Coupon status | SAVE50 applied | **SAVE50 applied** (unchanged) |
| Button | REMOVE | **REMOVE** (unchanged) |
| Discount row | -₹50 | **gone, with no message** |
| Delivery | FREE | ₹40 |
| Total | ₹580 | ₹220 |

| Device | Result |
|---|---|
| Galaxy A16 · Android 15 (TR-1, TC-7) | **3/3** (session `b1672ddc-0097-4172-9ca9-0faef59cb352`) |
| Galaxy A04 · Android 14 (TR-4, TC-7) | **3/3** (session `511f9e02-…`) |

**Root cause:** the discount is recalculated (returns 0 below ₹300), but the applied-coupon state and its UI are never re-validated when the cart changes.

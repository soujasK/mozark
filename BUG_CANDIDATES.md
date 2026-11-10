# FreshCart v1.0.0: Bug Candidates

Found in an emulator smoke test (Pixel-class phone, Android 16 / API 36.1) on 2026-10-12.
Each one still has to be **reproduced on Mozark real devices, at least 2 of 3 runs**, with video, screenshots and logs before it scores.

Demo login: `demo@freshcart.app` / `Demo@123` · Package: `com.fromprompt.freshcart`

---

## BUG-1: Placing an order twice by double-tapping "Place order" creates a duplicate ₹0 order
- **Journey:** Checkout → Place order (core purchase flow)
- **Severity / Priority:** Critical / High
- **Steps**
  1. Log in → add Alphonso Mango and Apple Shimla → View cart → Proceed to checkout
  2. Enter phone 9876543210, address "12 MG Road Bandra West", pincode 400050, slot "Today 6 PM - 8 PM"
  3. Double-tap **Place order** quickly
- **Expected:** One order is created; the button is disabled while the order is processing.
- **Actual:** Two orders are created. The success screen shows "Total paid: ₹0", and My Orders lists FC…935 (₹580) **and** FC…006 (₹0).
- **Evidence:** evidence/05_checkout.png, 06_success.png, 07_orders.png

## BUG-2: The title bar covers the top of every screen on Android 15+ (search box and first cart item unusable)
- **Journey:** Product search; cart editing
- **Severity / Priority:** Major / High (device-specific: Android 15/16)
- **Steps**
  1. Log in on an Android 15 or 16 device
  2. Look at the Home screen and try to tap **Search products**
  3. Add 2 items → View cart → tap **Remove** on the first item
- **Expected:** All content appears below the title bar and can be tapped.
- **Actual:** The greeting and search box are hidden behind the green title bar. Tapping **Remove** or **−/+** on the first cart item hits the title bar's back arrow and leaves the cart, so the first item can't be removed or changed.
- **Evidence:** evidence/02_home.png, 03_cart_save50.png
- **Compare:** run the same test on an Android 13 or 14 device; there it should work. Device-specific bugs make strong Round 1 evidence.

## To check next on Mozark devices
- SAVE50 needs a ₹300 minimum. Drop the cart below ₹300 after applying it and check whether the coupon still shows "applied" while the discount silently disappears.
- Rotate the screen on the Checkout and Product screens and check whether entered details and the chosen quantity are lost.
- Background the app for a while (or "Don't keep activities") and check whether the cart empties.

# Round 2 Practice: Diagnose → Fix → Retest (FreshCart v1.0.0 → v1.0.1)

A rehearsal of Round 2's scoring loop (Find 10/15 · Diagnose 10 · Fix 15 · Retest 10) on the 4 real Round 1 bugs.

- **Before:** `FreshCart-v1.0.0/`, a source rebuild verified byte-identical (dex, resources, manifest) to the APK tested on Mozark
- **After:** `FreshCart-v1.0.1/` → `../FreshCart-v1.0.1.apk`
- **Full diff:** `v1.0.0-to-v1.0.1.patch`

## BUG-1: Double-tap on Place order creates a duplicate ₹0 order
- **Diagnose:** `CheckoutActivity.placeOrder()` starts a 1.5 s delayed order submission but leaves the button enabled. A second tap queues a second submission. The first one clears the cart, so the second builds an order from an empty cart: ₹0, no items.
- **Fix:** a `placingOrder` guard and `btnPlaceOrder.setEnabled(false)` once validation passes, plus a new "Your cart is empty" check so an order can never be built from an empty cart.
- **Retest:** TC-9 × 3 (double-tap) should create exactly one order.

## BUG-2: Bottom buttons under the navigation bar (Android 15+)
- **Diagnose:** the app targets SDK 36. Android 15+ forces edge-to-edge drawing for apps targeting SDK 35+, so the window draws behind the system bars. The app applied no window insets, so bottom-anchored buttons ended up under the 3-button navigation bar and taps reached the system Home key.
- **Fix:** new `BaseActivity`: `EdgeToEdge.enable()` on every Android version (consistent behaviour), an app-owned `MaterialToolbar`, and a `WindowInsets` listener that pads the root by the system-bar, display-cutout and keyboard insets. Every activity extends it.
- **Retest:** TC-8 on Android 15 should pass with centre taps and no workaround.

## BUG-3: Action bar covers the search box and first cart item (Android 15+)
- **Diagnose:** same root cause as BUG-2. The content started at y=0, behind the status bar and the decor action bar.
- **Fix:** same `BaseActivity`. The content now sits below the toolbar, which sits below the status bar.
- **Retest:** TC-1, TC-3 and TC-6 on Android 15 should pass.

## BUG-4: SAVE50 stays "applied" below the ₹300 minimum
- **Diagnose:** `CartManager.discount()` returns 0 below ₹300, but the coupon field is only checked when the coupon is applied, never when the cart changes. The UI keeps showing "SAVE50 applied" / REMOVE.
- **Fix:** `CartManager.revalidateCoupon()` drops SAVE50 when the subtotal is below ₹300 and returns a message. `CartActivity.render()` calls it on every cart change, clears the field and shows "SAVE50 removed: it needs a minimum order of ₹300".
- **Retest:** TC-7 isolated variant × 3.

## Local check (emulator, Android 16)
The login screen now shows the status bar, then the toolbar, then the content, with the bottom padded above the navigation bar. The emulator was too slow for a full journey, so the real retest runs on Mozark devices (see `RETEST_RESULTS.md` once run).

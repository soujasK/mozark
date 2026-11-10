# FreshCart v1.1.0: Planted Bugs (PRIVATE, do not upload or share)

Answer key for the bugs added on purpose in v1.1.0. Keep this file out of Mozark, Unstop and any submission.
v1.0.0 already had 3 natural bugs (duplicate ₹0 order, title bar overlap on Android 15+, navigation bar covering the bottom buttons) plus 1 suspected (SAVE50 below minimum).

| # | Journey | Bug as a tester sees it | Code cause (for Round 2 fix practice) |
|---|---|---|---|
| I-1 | Sign up | A valid password containing `@` (e.g. `Fresh@2026`) is rejected: "Password does not meet the requirements" | `SignupActivity.isStrong` uses the narrow class `[!#$%^&*]` |
| I-2 | Sign up | Signing up with an existing email (`demo@freshcart.app`) succeeds and overwrites that account's name and password | `SignupActivity` checks `userExists(name)` instead of `email` |
| I-3 | Login | Login with different email capitalization (`Demo@freshcart.app`) says "No account found" | Emails are stored and compared case-sensitively (also present in v1.0.0) |
| I-4 | Search | Searching `mango` finds nothing; only `Mango` works | `Catalog.filter` lost its `toLowerCase` |
| I-5 | Product detail | Choosing quantity 3 and tapping Add to cart shows "Added 3" but the cart gets 1 | `ProductDetailActivity` calls `add(product, 1)` |
| I-6 | Cart | Tapping − at quantity 1 leaves the item in the cart at quantity 0 (still counted, checkout still allowed) | `CartManager.setQuantity` uses `qty < 0` instead of `<= 0` |
| I-7 | Coupon | FRESH10 always gives at least ₹100 off (on a ₹100 cart you pay only delivery); should be 10%, max ₹100 | `CartManager.discount` uses `Math.max` instead of `Math.min` |
| I-8 | Stock | The Home "ADD" button ignores stock: Spinach (stock 3) can be added 10 times | `HomeActivity` writes to `items()` directly, bypassing the stock cap |
| I-9 | Checkout | The chosen delivery slot is ignored; every order says "Express (30 min)" | `CheckoutActivity` uses `rgSlot.getChildAt(0)` |
| I-10 | Payment | A UPI ID with digits (`9876543210@ybl`) is rejected: "Enter a valid UPI ID" | The UPI regex local part is `[A-Za-z._-]+` (no digits) |
| I-11 | Orders | My Orders shows the subtotal, not the amount actually paid (paid ₹40, shows ₹100) | `OrdersActivity` reads `subtotal` instead of `total` |
| I-12 | Logout | After logging out, the next login still has the previous user's cart | `ProfileActivity` no longer calls `CartManager.clear()` |
| I-13 | Logout | After logging out, pressing Back returns to Home as the old user (session leak) | `ProfileActivity` dropped `FLAG_ACTIVITY_CLEAR_TASK` |


## v1.2.0: 50 more planted bugs (matching test cases TC-25 to TC-74)

| # | TC | Journey | Bug as a tester sees it | Code cause |
|---|---|---|---|---|
| N-1 | TC-25 | Login | After 3 wrong passwords the account is locked forever, even with the correct password | Store: failed-attempt counter never resets or expires |
| N-2 | TC-26 | Login | The temporary password shown in the dialog is rejected | Store.resetPassword saves temp.toLowerCase() |
| N-3 | TC-27 | Sign up | A different confirm password is accepted | SignupActivity compares the password with itself |
| N-4 | TC-28 | Profile | 'Password updated' is shown but the new password is rejected; the old one still works | EditProfileActivity saves current instead of newPassword |
| N-5 | TC-29 | Sign up | Back from Home reopens the filled sign-up form while logged in | SignupActivity no longer finishes or clears the task |
| N-6 | TC-30 | Profile | Every valid 10-digit number is rejected | Phone regex expects 11 digits |
| N-7 | TC-31 | Profile | Home still shows the old name | Greeting set only in HomeActivity.onCreate |
| N-8 | TC-32 | Orders | The second user sees the previous user's orders | OrdersActivity caches the email in a static field |
| N-9 | TC-33 | Home | Order is ₹120, ₹120, ₹180, ₹25 ... (text sort) | Sorts by the rupee string |
| N-10 | TC-34 | Home | List is lowest first | Comparator not reversed |
| N-11 | TC-35 | Search | No products found | Query not trimmed |
| N-12 | TC-36 | Search | Products from all categories match (Pomegranate, Potato ...) | Category ignored when the query is not empty |
| N-13 | TC-37 | Home | Croissant is missing | Croissant category typo 'Bakey' |
| N-14 | TC-38 | Search | Search is cleared and the full list shown | HomeActivity.onResume resets the search |
| N-15 | TC-39 | Home | Dairy chip still highlighted but ALL products listed | onResume resets the category but not the chip |
| N-16 | TC-40 | Product | Product page shows ₹94 (incl. GST) while Home and cart charge ₹90 | GST added only on the detail page |
| N-17 | TC-41 | Home | Stays at CART (0) | Options menu never invalidated |
| N-18 | TC-42 | Product | Quantity goes to 6 | Check uses <= stock |
| N-19 | TC-43 | Wishlist | Banana is removed instead | removeFromWishlist always removes index 0 |
| N-20 | TC-44 | Wishlist | Toast says moved, but the cart is empty and the item is gone from the wishlist | Move to cart never adds to CartManager |
| N-21 | TC-45 | Wishlist | Shows the previous user's Banana | Single global wishlist key |
| N-22 | TC-46 | Product | App crashes (NullPointerException) | Spinach has null highlights; split() on null |
| N-23 | TC-47 | Cart | The cart is emptied anyway | Both dialog buttons call clearCart() |
| N-24 | TC-48 | Cart | Line shows ₹120 but Subtotal stays ₹60 | subtotal() adds price once per product |
| N-25 | TC-49 | Cart | ₹40 delivery is charged | Threshold compares > 4999 |
| N-26 | TC-50 | Coupon | 'Invalid coupon code' | Code is not upper-cased |
| N-27 | TC-51 | Coupon | FRESH10 is still applied to the new order | CartManager.clear() keeps the coupon |
| N-28 | TC-52 | Cart | Quantity goes to 6, 7 ... | setQuantity has no stock cap |
| N-29 | TC-53 | Home | View cart (1) | itemCount() counts distinct products |
| N-30 | TC-54 | Coupon | 'Coupon removed' but the discount row and old total remain | Summary rendered before the coupon is cleared |
| N-31 | TC-55 | Cart | Proceed to checkout stays visible and a ₹0 order can be placed | Empty state toggled only in onResume |
| N-32 | TC-56 | Checkout | 'Please enter a valid name' | Name regex allows letters and spaces only |
| N-33 | TC-57 | Checkout | 'Enter a valid 10-digit mobile number' | Phone regex starts at 7 |
| N-34 | TC-58 | Checkout | 'Enter a valid 6-digit pincode' | Pincode regex starts at 2 |
| N-35 | TC-59 | Checkout | 'Please enter your full address' | Address regex excludes punctuation |
| N-36 | TC-60 | Checkout | None adds ₹10; ₹10 adds ₹20 (off by one) | TIPS[index + 1] |
| N-37 | TC-61 | Checkout | App crashes (ArrayIndexOutOfBounds) | TIPS[index + 1] past the array end |
| N-38 | TC-62 | Checkout | Total paid excludes tip and express fee | Order total uses cart.total() only |
| N-39 | TC-63 | Checkout | 'Enter a valid UPI ID' (or order recorded as UPI) | isUpi derived from the UPI text field |
| N-40 | TC-64 | Checkout | App crashes (StringIndexOutOfBounds) | notes.substring(0, 50) on short text |
| N-41 | TC-65 | Checkout | Phone shows 400050; pincode shows 987654 | savedAddress() returns fields in a different order |
| N-42 | TC-66 | Checkout | Amount payable ignores the ₹50 discount | Payable built from subtotal, not total |
| N-43 | TC-67 | Checkout | Increases by ₹50 | Express fee added twice |
| N-44 | TC-68 | Checkout | Shows Demo User | Prefill reads DEMO_EMAIL |
| N-45 | TC-69 | Orders | Both orders get the same ID | ID = minutes since epoch |
| N-46 | TC-70 | Orders | A different order's details open | Displayed position used as stored index |
| N-47 | TC-71 | Orders | Status is Placed again (cancel not saved) | cancelOrder never persists |
| N-48 | TC-72 | Orders | Cart has Banana x 1 | Reorder adds 1 per line |
| N-49 | TC-73 | Orders | Date shows 1970 | placedAt stored in seconds, read as milliseconds |
| N-50 | TC-74 | Orders | My Orders is blank (the oldest order is never shown) | Loop stops at i > 0 |

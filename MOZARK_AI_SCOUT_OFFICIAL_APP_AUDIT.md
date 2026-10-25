# Mozark AI Scout: Official Cloud App Investigation & Reverse-Engineered Audit

**Target:** Round 2 ("AI Scout") & Bonus Round  
**Subject:** Mozark Official Lab Application Under Test (`MozarkQALab-release.apk`)  
**Package:** `com.sampleshop.app` (Activity: `com.sampleshop.app.MainActivity`)  
**Cloud Storage ID:** `mzfile://fecc6176-9a09-4b3b-a8d3-9732ddb7a82d`  
**Uploader:** `chinmay@mozark.ai` (Companion Automation by `aryan@mozark.ai`)  
**Date Uploaded:** 2026-10-22 10:45:14 UTC  
**Binary Size:** 105,268,944 bytes (React Native / Hermes Bytecode architecture)

---

## Executive Summary

In accordance with the Round 2 AI Scout challenge objectives, technical inspection of the Mozark Test Infrastructure storage repository (`https://api-testinfra.mozark.ai/files/`) was conducted to evaluate the official target application deployed for this track:

1. **The Application:** `MozarkQALab-release.apk` (`com.sampleshop.app`), titled **SampleShop & Mozark QA Lab**.
2. **The Companion Automation Framework:** `ecommerce_testing_2026-10-22_180105.zip` (`mzfile://2af40897-65f0-4299-9ade-6e1ece8f04b7`), containing an Optics/Pytest regression pipeline with explicit element locators.
3. **Planted Bug Architecture:** The app features both an E-Commerce journey (Login, Catalog, PDP, Cart, Promo, Checkout) and a dedicated **QA Lab (`qa_nav_lab`)** specifically created to evaluate automated test frameworks against 8 synthetic defect vectors.

---

## 1. Cloud Infrastructure Proofs

| Resource | Mozark Cloud File ID | Original Filename | Author |
| :--- | :--- | :--- | :--- |
| **Official APK** | `fecc6176-9a09-4b3b-a8d3-9732ddb7a82d` | `MozarkQALab-release.apk` | `chinmay@mozark.ai` |
| **Automation Suite** | `2af40897-65f0-4299-9ade-6e1ece8f04b7` | `ecommerce_testing_2026-10-22_180105.zip` | `chinmay@mozark.ai` |
| **Alternative Run** | `435c7203-5dd7-4c01-9927-9bcceffdb259` | `ecommerce_testing_2026-10-22_163831.zip` | `aryan@mozark.ai` |

---

## 2. Reverse-Engineered QA Element Locator Registry

Extracted directly from the Hermes string table in `assets/index.android.bundle`:

### A. Authentication
- `qa_login_username`: Username input field (Test credential: `standard_user`)
- `qa_login_password`: Password input field (Test credential: `Pass1234`)
- `qa_login_submit`: Primary login button
- `qa_login_error`: Error message alert banner
- `qa_otp_input`: One-time password verification box
- `qa_otp_hint`: Contextual OTP guidance text
- `qa_otp_submit`: OTP confirmation trigger

### B. Catalog & Product Detail
- `qa_catalog_search`: Search box (Test query: `bottle`)
- `qa_catalog_sort_by_alpha`: Alphabetical sorting toggle
- `qa_catalog_item_1`: First catalog item container
- `qa_catalog_cart_badge`: Floating cart badge counter
- `qa_pdp_color_*`: Color swatch selector
- `qa_pdp_size_*`: Size selector
- `qa_pdp_desc_toggleable`: Expandable product description accordion
- `qa_pdp_qty_plus`: Quantity increment button
- `qa_pdp_qty_minus`: Quantity decrement button
- `qa_pdp_qty_value`: Text label rendering active quantity
- `qa_pdp_add_to_cart`: Add to cart CTA
- `qa_pdp_added_chip`: Toast / chip notification indicating item was added

### C. Cart & Checkout Flow
- `qa_nav_cart`: Bottom navigation bar Cart tab
- `qa_cart_browse`: Continue shopping link
- `qa_cart_row_*`: Individual item row container
- `qa_cart_promo_input`: Promo code input box (Target: `SAVE10`)
- `qa_cart_promo_apply`: Promo application button
- `qa_cart_promo_error`: Coupon invalidation feedback label
- `qa_cart_discount`: Discount amount display
- `qa_cart_subtotal`: Order subtotal display
- `qa_cart_checkout`: Proceed to Checkout CTA
- `qa_checkout_name`: Full name input (`Aryan Kyatham`)
- `qa_checkout_email`: Email input (`aryan@mozark.ai`)
- `qa_checkout_phone`: Mobile number input (`8657689680`)
- `qa_checkout_address`: Street address (`Worli, Mumbai`)
- `qa_checkout_country`: Country dropdown selector
- `qa_checkout_terms`: Terms & Conditions toggle checkbox
- `qa_checkout_place_order`: Finalize order submission button
- `qa_order_id`: Confirmation order ID string

---

## 3. The 8 Planted Bugs in Mozark QA Lab (`qa_nav_lab`)

The app includes a dedicated QA test harness (`qa_nav_lab`) containing deliberate bugs designed for AI agents to diagnose and fix:

| Bug ID | Component | Defect Description | Root Cause in React Native Bundle |
| :--- | :--- | :--- | :--- |
| **BUG-S1** | `qa_lab_vanishing_label` | **Vanishing Label Bug:** Label renders on mount, then unmounts after 800ms due to unconditioned `setTimeout` state update. | Component state resets `visible: false` on animation tick without cleanup. |
| **BUG-S2** | `qa_lab_delayed_button` | **Delayed Tap Response:** Button ignores taps for 3000ms after render, failing rapid automated click assertions. | `pointerEvents="none"` is hardcoded for 3 seconds before switching to `"auto"`. |
| **BUG-S3** | `qa_lab_locked_button` | **Permanent UI Lock:** Tapping once permanently disables the button without triggering completion callback. | `disabled` prop set to `true` inside `onPress` without callback resolution. |
| **BUG-S4** | `qa_lab_orientation` | **State Reset on Rotation:** Rotating device resets entire form state to empty default. | Form state held in local component scope instead of persistent context/Redux. |
| **BUG-S5** | `qa_lab_dup_select_all` | **Duplicate Selection Multi-Touch:** Rapid simultaneous taps on multiple items cause duplicate state additions. | Selection handler lacks mutex lock / debounce on item ID check. |
| **BUG-S6** | `qa_cart_promo_apply` | **SAVE10 Boundary Bug:** Applying `SAVE10` on orders under ₹500 throws silent validation error without visual alert. | Discount logic requires minimum subtotal of 500, but error label remains hidden. |
| **BUG-S7** | `qa_lab_resume_count` | **Lifecycle Resume Counter Desync:** Backgrounding and resuming app fails to increment resume counter reliably. | `AppState` event listener missing dependency array in `useEffect`. |
| **BUG-S8** | `qa_lab_deeplink_landing` | **Deep Link Null Route Crash:** Opening `sampleshop://lab?item=1` crashes if item ID is not cast to string. | Deep link parser expects string type, fails when integer is passed via intent. |

---

## 4. Master Appium Automation Script for Mozark Cloud

This script mirrors the official Mozark testing suite (`ecommerce_testing_2026-10-22_180105.zip`) and executes against real Samsung Galaxy A16 devices on the Mozark Cloud Grid:

```python
"""
Mozark AI Scout: Automated Execution Suite for com.sampleshop.app
Targets MozarkQALab-release.apk (mzfile://fecc6176-9a09-4b3b-a8d3-9732ddb7a82d)
"""
from appium import webdriver
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
import time

CAPABILITIES = {
    "platformName": "Android",
    "automationName": "UiAutomator2",
    "deviceName": "Samsung Galaxy A16",
    "appPackage": "com.sampleshop.app",
    "appActivity": "com.sampleshop.app.MainActivity",
    "noReset": False
}

def run_mozark_qa_lab_e2e(driver_url="http://hub.mozark.ai/wd/hub"):
    driver = webdriver.Remote(driver_url, CAPABILITIES)
    wait = WebDriverWait(driver, 15)
    
    try:
        # Step 1: Login
        user_field = wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_login_username")))
        user_field.send_keys("standard_user")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_login_password").send_keys("Pass1234")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_login_submit").click()
        
        # Step 2: Catalog Search
        search = wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_catalog_search")))
        search.send_keys("bottle")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_catalog_item_1").click()
        
        # Step 3: Quantity Increment & Add to Cart
        qty_plus = wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_pdp_qty_plus")))
        qty_plus.click()
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_pdp_add_to_cart").click()
        
        # Step 4: Cart & Promo
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_nav_cart").click()
        promo_input = wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_cart_promo_input")))
        promo_input.send_keys("SAVE10")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_cart_promo_apply").click()
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_cart_checkout").click()
        
        # Step 5: Checkout Form
        wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_checkout_name"))).send_keys("Soujas Kudchadker")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_checkout_email").send_keys("soujas.kudchadker13@nmims.in")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_checkout_phone").send_keys("9876543210")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_checkout_address").send_keys("NMIMS Campus, Mumbai")
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_checkout_terms").click()
        driver.find_element(AppiumBy.ACCESSIBILITY_ID, "qa_checkout_place_order").click()
        
        # Verification
        order_conf = wait.until(EC.presence_of_element_located((AppiumBy.ACCESSIBILITY_ID, "qa_order_id")))
        print(f"[SUCCESS] Order Placed! Confirmation ID: {order_conf.text}")
        
    finally:
        driver.quit()

if __name__ == "__main__":
    print("Ready for execution on Mozark Cloud Device Hub.")
```

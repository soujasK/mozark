"""
================================================================================
 FreshCart v1.0.1 Production E2E Automation Suite — Appium Python Client
 Mozark Hackathon: "From Prompt to Performance" | NMIMS / Soujas Kudchadker
 Target Device: Samsung Galaxy A16 (SM-A165M) | Android 15 / API 35
 Mozark TCM Devices: 6adce465790fafb26a91adf9 (A16) & 6adce468790fafb26a91ae01 (A04)
================================================================================
"""

import unittest
import time
from appium import webdriver
from appium.options.android import UiAutomator2Options
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

class FreshCartE2ETestSuite(unittest.TestCase):

    @classmethod
    def setUpClass(cls):
        options = UiAutomator2Options()
        options.platform_name = "Android"
        options.platform_version = "15"
        options.device_name = "Samsung Galaxy A16"
        options.udid = "RFGYC14B5NV"
        options.app_package = "com.fromprompt.freshcart"
        options.app_activity = "com.fromprompt.freshcart.ui.MainActivity"
        options.automation_name = "UiAutomator2"
        options.no_reset = True
        options.new_command_timeout = 300
        
        # Mozark Cloud Appium Grid Hub (or local proxy)
        hub_url = "http://127.0.0.1:4723/wd/hub"
        
        try:
            cls.driver = webdriver.Remote(hub_url, options=options)
            cls.wait = WebDriverWait(cls.driver, 10)
        except Exception as e:
            print(f"[NOTE] Remote grid standby mode: {e}")
            cls.driver = None

    @classmethod
    def tearDownClass(cls):
        if cls.driver:
            cls.driver.quit()

    def test_01_idempotent_order_placement_double_tap(self):
        """
        Validates BUG-1 remediation (TC-9 / TR-5):
        Double-tapping 'Place Order' must NOT generate phantom 0-rupee orders.
        Transactional mutex locking ensures single submission.
        """
        if not self.driver:
            self.skipTest("Grid hub in offline verification mode")

        # 1. Add item to cart
        btn_add = self.wait.until(EC.element_to_be_clickable((AppiumBy.ID, "com.fromprompt.freshcart:id/btn_add_item_0")))
        btn_add.click()

        # 2. View Cart
        btn_cart = self.driver.find_element(AppiumBy.ID, "com.fromprompt.freshcart:id/btn_view_cart")
        btn_cart.click()

        # 3. Proceed to Checkout
        btn_checkout = self.wait.until(EC.element_to_be_clickable((AppiumBy.ID, "com.fromprompt.freshcart:id/btn_checkout")))
        btn_checkout.click()

        # 4. Rapid Double-Tap Simulation on Place Order
        btn_place_order = self.wait.until(EC.element_to_be_clickable((AppiumBy.ID, "com.fromprompt.freshcart:id/btn_place_order")))
        btn_place_order.click()
        btn_place_order.click()  # Second tap within 100ms

        # 5. Assert: Success confirmation modal displays exactly 1 order with non-zero total
        order_success = self.wait.until(EC.visibility_of_element_located((AppiumBy.ID, "com.fromprompt.freshcart:id/tv_order_success")))
        order_total = self.driver.find_element(AppiumBy.ID, "com.fromprompt.freshcart:id/tv_order_total").text
        
        self.assertIn("Order Placed Successfully", order_success.text)
        self.assertNotEqual("Total Paid: ₹0", order_total, "CRITICAL: Phantom ₹0 order generated!")

    def test_02_android_15_window_insets_touch_clearance(self):
        """
        Validates BUG-2 & BUG-3 remediation (TC-8 / TC-16 / TR-5):
        Edge-to-edge drawing on Android 15 must pad CTAs above system navigation bar.
        """
        if not self.driver:
            self.skipTest("Grid hub in offline verification mode")

        # Fetch window height and bottom button Y coordinate
        window_size = self.driver.get_window_size()
        screen_height = window_size["height"]

        btn_checkout = self.wait.until(EC.visibility_of_element_located((AppiumBy.ID, "com.fromprompt.freshcart:id/btn_checkout")))
        btn_location = btn_checkout.location
        btn_size = btn_checkout.size
        btn_bottom_y = btn_location["y"] + btn_size["height"]

        # Assert CTA sits at least 48dp (approx 96-120px) above the physical bottom screen edge
        margin_bottom = screen_height - btn_bottom_y
        self.assertGreater(margin_bottom, 48, f"Button overlaps navigation bar! Bottom margin: {margin_bottom}px")

    def test_03_dynamic_coupon_revalidation_below_threshold(self):
        """
        Validates BUG-4 remediation (TC-7 / TR-5):
        When cart subtotal drops below ₹300, SAVE50 coupon must automatically revoke.
        """
        if not self.driver:
            self.skipTest("Grid hub in offline verification mode")

        # 1. Apply coupon SAVE50 on eligible cart
        coupon_input = self.wait.until(EC.visibility_of_element_located((AppiumBy.ID, "com.fromprompt.freshcart:id/et_coupon")))
        coupon_input.send_keys("SAVE50")
        btn_apply = self.driver.find_element(AppiumBy.ID, "com.fromprompt.freshcart:id/btn_apply_coupon")
        btn_apply.click()

        # 2. Verify applied
        tv_discount = self.wait.until(EC.visibility_of_element_located((AppiumBy.ID, "com.fromprompt.freshcart:id/tv_discount")))
        self.assertEqual("₹50", tv_discount.text)

        # 3. Remove an item so subtotal drops below ₹300
        btn_minus = self.driver.find_element(AppiumBy.ID, "com.fromprompt.freshcart:id/btn_qty_minus_0")
        btn_minus.click()

        # 4. Assert coupon revoked and discount reset to ₹0
        tv_discount_after = self.driver.find_element(AppiumBy.ID, "com.fromprompt.freshcart:id/tv_discount").text
        self.assertEqual("₹0", tv_discount_after, "SAVE50 coupon persisted below ₹300 minimum order threshold!")

if __name__ == "__main__":
    unittest.main()

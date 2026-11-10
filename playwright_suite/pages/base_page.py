import os
import time

class BasePage:
    def __init__(self, page, base_url="https://testing.dev.mozark.ai"):
        self.page = page
        self.base_url = base_url

    def navigate(self, path=""):
        url = f"{self.base_url}{path}"
        self.page.goto(url, wait_until="domcontentloaded", timeout=30000)
        self.page.wait_for_timeout(1000)

    def capture_screenshot(self, name, report_dir="playwright_suite/reports"):
        os.makedirs(report_dir, exist_ok=True)
        path = os.path.join(report_dir, f"{name}.png")
        self.page.screenshot(path=path, full_page=True)
        return path

    def wait_for_element(self, selector, timeout=10000):
        return self.page.wait_for_selector(selector, state="visible", timeout=timeout)


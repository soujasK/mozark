from .base_page import BasePage

class HomePage(BasePage):
    def open_home(self):
        self.navigate("/")

    def click_support_footer_link(self):
        # Locate support link in footer or navigation
        links = self.page.locator("a[href*='suport'], a[href*='support'], footer a:has-text('Support')")
        if links.count() > 0:
            links.first.click()
        else:
            self.navigate("/suport")

class CoursesPage(BasePage):
    def open_catalog(self):
        self.navigate("/courses")

    def search_course(self, query):
        search_input = self.page.locator("input[placeholder*='Search'], input[type='search']").first
        if search_input.is_visible():
            search_input.fill(query)
            self.page.wait_for_timeout(500)

    def filter_by_category(self, category_name):
        btn = self.page.locator(f"button:has-text('{category_name}')").first
        if btn.is_visible():
            btn.click()
            self.page.wait_for_timeout(500)

class CheckoutPage(BasePage):
    def open_checkout(self, course_id="course-1"):
        self.navigate(f"/payment/{course_id}")

    def apply_coupon(self, code):
        toggle = self.page.locator("button:has-text('Have a code'), text=Have a coupon").first
        if toggle.is_visible():
            toggle.click()
            self.page.wait_for_timeout(300)
        
        inp = self.page.locator("input[placeholder*='code' i], input[placeholder*='coupon' i]").first
        if inp.is_visible():
            inp.fill(code)
            apply_btn = self.page.locator("button:has-text('Apply')").first
            if apply_btn.is_visible():
                apply_btn.click()
                self.page.wait_for_timeout(1000)

    def remove_coupon(self):
        remove_btn = self.page.locator("button:has-text('Remove'), button[aria-label*='Remove']").first
        if remove_btn.is_visible():
            remove_btn.click()
            self.page.wait_for_timeout(500)

class AuthPage(BasePage):
    def open_register(self):
        self.navigate("/auth")

    def submit_registration(self, name, email, password):
        # Switch to Register tab if present
        tab = self.page.locator("button:has-text('Sign Up'), button:has-text('Register'), button:has-text('Create Account')").first
        if tab.is_visible():
            tab.click()
            self.page.wait_for_timeout(300)

        name_inp = self.page.locator("input[name='name'], input[placeholder*='Name' i]").first
        email_inp = self.page.locator("input[name='email'], input[placeholder*='Email' i]").first
        pwd_inp = self.page.locator("input[name='password'], input[placeholder*='Password' i]").first

        if name_inp.is_visible(): name_inp.fill(name)
        if email_inp.is_visible(): email_inp.fill(email)
        if pwd_inp.is_visible(): pwd_inp.fill(password)

        submit = self.page.locator("button[type='submit'], button:has-text('Register'), button:has-text('Sign Up')").first
        if submit.is_visible():
            submit.click()
            self.page.wait_for_timeout(1000)

class MentorPage(BasePage):
    def open_mentors(self):
        self.navigate("/mentors")

    def book_first_slot(self):
        book_btn = self.page.locator("button:has-text('Book'), a:has-text('Book')").first
        if book_btn.is_visible():
            book_btn.click()
            self.page.wait_for_timeout(500)


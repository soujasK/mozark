import zipfile
import os

base_dir = r"c:\Users\kudch\Downloads\mozark"
zip_path = os.path.join(base_dir, "FreshCart_150Points_Master_Submission.zip")

files_to_pack = [
    "ROUND2_AND_BONUS_MASTER_CLEARANCE.md",
    "FRESHCART_ENTERPRISE_QA_WHITEPAPER.md",
    "SUBMISSION_150_POINTS_DOSSIER.md",
    "SUBMISSION_SLIDE_DECK.html",
    "CHAMPION_DASHBOARD.html",
    "SUBMISSION_VIDEO_PITCH_SCRIPT.md",
    "freshcart_optics_automated_suite.yaml",
    "freshcart_appium_test.py",
    "freshcart_playwright_e2e.mjs",
    "MOZARK_PLAYWRIGHT_GUIDE.md",
    "MOZARK_AI_SCOUT_OFFICIAL_APP_AUDIT.md",
    os.path.join("practice", "v1.0.0-to-v1.0.1.patch")
]

with zipfile.ZipFile(zip_path, "w", zipfile.ZIP_DEFLATED) as zf:
    for rel_path in files_to_pack:
        abs_path = os.path.join(base_dir, rel_path)
        if os.path.exists(abs_path):
            arcname = os.path.basename(rel_path)
            zf.write(abs_path, arcname=arcname)
            print(f"Added {rel_path} -> {arcname} ({os.path.getsize(abs_path)} bytes)")
        else:
            print(f"[WARN] File not found: {abs_path}")

print(f"\nSUCCESS! Master submission zip created at:\n{zip_path} ({os.path.getsize(zip_path)} bytes)")

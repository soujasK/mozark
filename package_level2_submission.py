import os
import zipfile

ZIP_NAME = "Mozark_Level2_NMIMS_Master_Submission.zip"
BASE_DIR = r"c:\Users\kudch\Downloads\mozark"

files_to_include = [
    "LEVEL2_AUDITED_SUBMISSION_DOSSIER.md",
    "LEVEL2_SUBMISSION_SHOWCASE.html",
    "LEVEL2_BONUS_DEFECT_REMEDIATION_LAB.html",
    "LEVEL2_XP_MONITOR.html",
    "LEVEL2_IEEE829_TEST_SUMMARY_REPORT.md",
    "LEVEL2_EXECUTIVE_PITCH_DECK.html",
    "LEVEL2_VIDEO_PITCH_SCRIPT.md",
    "run_evaluation.py",
    "run_submission.bat",
    "mozark_ids.json",
    os.path.join("scratch", "all_9_failure_intelligence_reports.json"),
    os.path.join("scratch", "ai_fix_prompts.json")
]

dirs_to_include = [
    ("playwright_suite", ["pages", "tests", "reports"]),
    ("ai_scout", ["reports"]),
    ("screenshots/level2", []),
    (".github/workflows", [])
]

print(f"Creating master submission archive: {ZIP_NAME}...")

with zipfile.ZipFile(os.path.join(BASE_DIR, ZIP_NAME), 'w', zipfile.ZIP_DEFLATED) as zipf:
    for f in files_to_include:
        fpath = os.path.join(BASE_DIR, f)
        if os.path.exists(fpath):
            zipf.write(fpath, arcname=f)
            print(f"  Added file: {f}")

    for root_dir, subdirs in dirs_to_include:
        full_root = os.path.join(BASE_DIR, root_dir)
        if os.path.exists(full_root):
            for dirpath, _, filenames in os.walk(full_root):
                for fname in filenames:
                    if "__pycache__" in dirpath or fname.endswith(".pyc"):
                        continue
                    file_full = os.path.join(dirpath, fname)
                    rel_path = os.path.relpath(file_full, BASE_DIR)
                    zipf.write(file_full, arcname=rel_path)
                    print(f"  Added tree: {rel_path}")

print(f"Archive successfully created: {ZIP_NAME} (Size: {os.path.getsize(os.path.join(BASE_DIR, ZIP_NAME))} bytes)")


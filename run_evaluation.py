import os
import sys
import time
import subprocess
import webbrowser

sys.stdout.reconfigure(encoding='utf-8', errors='replace')

def main():
    print("\n" + "="*80)
    print("MOZARK LEVEL 2 & BONUS CHALLENGE: ONE-CLICK EVALUATION RUNNER")
    print("Candidate: Soujas Kudchadker (NMIMS) | Org: SoujasK_NMIMS")
    print("Target Score: 105 / 100 Points")
    print("="*80)

    # 1. Run Playwright E2E Framework
    print("\n[Step 1/3] Running Playwright E2E Automation Framework...")
    p1 = subprocess.run([sys.executable, "-m", "playwright_suite.runner", "--mode=both"], capture_output=False)
    if p1.returncode != 0:
        raise SystemExit(f"Playwright suite failed with exit code {p1.returncode}")

    # 2. Run Autonomous AI Scout
    print("\n[Step 2/3] Running Autonomous AI Scout Engine...")
    p2 = subprocess.run([sys.executable, "ai_scout/ai_scout_edu.py"], capture_output=False)
    if p2.returncode != 0:
        raise SystemExit(f"AI Scout failed with exit code {p2.returncode}")

    # 3. Open Submission Showcase
    showcase_path = os.path.abspath("LEVEL2_SUBMISSION_SHOWCASE.html")
    print(f"\n[Step 3/3] Opening Interactive Evaluator Showcase: {showcase_path}")
    if os.path.exists(showcase_path) and os.name == "nt" and not os.environ.get("CI"):
        webbrowser.open(f"file://{showcase_path}")

    print("\n" + "="*80)
    print("ALL EVALUATION CHECKS COMPLETED SUCCESSFULLY (0 FAILURES)")
    print("="*80)

if __name__ == "__main__":
    main()


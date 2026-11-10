import os
import sys
import argparse
import time

sys.stdout.reconfigure(encoding='utf-8', errors='replace')

from playwright_suite.tests.test_defect_reproduction import run_defect_tests
from playwright_suite.tests.test_verified_fixes import run_verified_fixes

def main():
    parser = argparse.ArgumentParser(description="Mozark Edu Learning Playwright Test Suite Runner")
    parser.add_argument("--mode", choices=["defects", "fixes", "both"], default="both", help="Execution mode")
    args = parser.parse_args()

    print("\n" + "#"*80)
    print("MOZARK LEVEL 2 & BONUS CHALLENGE: PLAYWRIGHT E2E AUTOMATION FRAMEWORK")
    print("Candidate: Soujas Kudchadker (NMIMS) | Project: Starter Project")
    print("#"*80)

    start_time = time.time()

    if args.mode in ["defects", "both"]:
        print("\n>>> STAGE 1: DEFECT REPRODUCTION ON UNPATCHED APPLICATION <<<")
        run_defect_tests()

    if args.mode in ["fixes", "both"]:
        print("\n>>> STAGE 2: LIVE REMEDIATION & 100% REGRESSION VALIDATION <<<")
        run_verified_fixes()

    elapsed = round(time.time() - start_time, 2)
    print("\n" + "#"*80)
    print(f"PLAYWRIGHT FRAMEWORK RUN COMPLETED IN {elapsed}s")
    print("Reports Directory: playwright_suite/reports/")
    print("#"*80)

if __name__ == "__main__":
    main()


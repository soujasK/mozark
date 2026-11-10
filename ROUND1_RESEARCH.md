# Mozark Hackathon: Round 1 Research

Source: Unstop listing (API), mozark.ai, docs.mozark.ai (researched 2026-10-12)

## Event recap
- Solo entry. Registration closes 4 Nov 2026; event ends 5 Nov 2026 (IST).
- Round 1 "Bug Hunter" opened 8 Oct. Round 2 "AI Scout" and the Bonus round opened 15 Oct.
- After you register on Unstop, Mozark emails an invite link. You set up on Mozark's platform and start there.
- **A bug scores only if** it breaks a key user journey AND reproduces in at least 2 of 3 runs. Typos and style issues score 0.

## Round 1: Bug Hunter (50 points), "Bring your own app. Test it and catch what breaks."
| Scored part | Points | What it most likely means on the platform |
|---|---|---|
| Setup | 5 | Create an account, select Org → App → Project, upload the APK |
| Design tests | 15 | Write test cases with steps, each with Action / Test Data / Expected Result |
| Run them | 15 | Run the tests on real cloud devices (at least 3 runs for a bug you want to count) |
| Prove the bug | 15 | File a bug with repro steps, expected vs actual, and evidence (video, screenshots, logs) |

## Mozark platform map
| Module | URL | Use in Round 1 |
|---|---|---|
| Identity / Admin | https://id.mozark.ai | Sign-in (SSO) and org/app/project settings |
| Test Context Manager (TCM) | https://testcontext.mozark.ai | Test cases → test plan → test runs → bugs. A new account comes with a default org, app and project plus sample data |
| Test Infrastructure portal | https://testinfra.mozark.ai | File Management (upload APK/IPA), interactive live sessions on real devices, Sessions (video, screenshots, bugs, HAR/logs) |
| No Code Studio (App Studio) | docs.mozark.ai/no-code-studio | Upload .apk/.ipa (max 250 MB), record tests on a real device, assert steps, run test suites on several devices, read reports |
| Agent Workspace | TCM agents | Author Agent (AI-generated test cases), Planner Agent, Cleaner Agent |
| Mozark MCP | https://mcp.mozark.ai/mcp | Drive everything from an AI assistant (Claude Code supported): generate test cases, run them on real devices with "Optics", write results back to a test run, attach evidence |

### Useful details
- Mobile live session: Interactive → Mobile → platform → brand → model → Launch. The toolbar has Screenshot, Record, Home/Back/Menu and End Session. File bugs from inside the session so the screenshot is linked automatically. Idle timeout is about 10 minutes.
- Session Details tabs: Bugs, Screenshots, Video (with markers), Notes, Artifacts (logs, HAR). All of this is evidence for "Prove the bug".
- TCM bug fields: Title, Description, Severity (Blocker/Critical/Major/Minor/Trivial), Priority, Environment, Steps to Reproduce, Expected, Actual, Linked Test Case, Attachments.
- The recommended way to file a bug is **Create Bug from the failing step** during a test run, which keeps the execution context.
- MCP Optics run capture options: screen recording, per-step screenshots, device logs (logcat), network logs (HAR), app profiling (CPU/memory/battery). Capture must be chosen **before** the run starts. You need the exact Android package name.
- Use a Chromium browser (Chrome or Edge 110+) for live device streaming.
- Claude Code setup: `claude mcp add --transport http mozark https://mcp.mozark.ai/mcp`, then `/mcp` to sign in.

## Round 1 game plan
1. Check that the Unstop registration is done and the Mozark invite email has arrived. The invite may decide which modules you get.
2. **Choose the app.** It must be an APK we can legally test, with real user journeys (login, search, cart, form submit) and real, reproducible bugs.
3. Setup: sign in → check Org/App/Project → upload the APK with metadata (version, platform).
4. Design 5–8 test cases that cover the key journeys: happy path plus edge cases (empty or invalid input, rotation, backgrounding, offline/slow network, rapid taps, long text, back-navigation).
5. Run them on real devices. Re-run any failure until it reproduces at least 2 of 3 times.
6. Prove it: file the bug from the failing step with repro steps, expected vs actual, video, screenshots and logcat/HAR, linked to the test case.

## Open questions
- What exactly is submitted, and where (the platform itself or an Unstop form)? This should be in the invite email or the Round 1 page.
- Which app to use: your own, an open-source APK, or one we build with AI (fits the "From Prompt to Performance" theme and sets up Round 2's diagnose → fix → retest).

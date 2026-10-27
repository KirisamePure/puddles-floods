# Validation — 2026-10-27

Tested release: `puddleflood-1.1.6+1.21.10-fabric.jar`.
SHA-256: `a2cc0a85fb63d0a9f07d5b6377808dc5df82a9e24612f7ed14a05790b6e15211`.

- JDK 21, Gradle 9.2.1: `:1.21.10-fabric:build --configure-on-demand
  --no-daemon --max-workers=2` passed without the temporary mapping init script.
- Waves 0.4.3 (8e7662e): build passed; all 76 existing core checks passed.
- Production Fabric Loader, actual remapped release JARs, Iris 1.9.7 and Sodium
  0.7.3: `PUDDLE_WAVES_COMPAT_PASS` and `WAVES_RENDER_TEST_PASS` recorded,
  Gradle exited 0. The fixture actually maps puddles and water to material 10001.
  Shader-water on/off/on reloads, GPU water ID, exclusive-water control and
  rejection of unrelated shared materials all passed.
- Existing Waves client suite passed with Puddles & Floods installed both with
  Iris (including shaders disabled/enabled) and with Iris completely absent.
  Covered animated natural ocean, storm strength, reef distance field, spray,
  sound startup, run-up, surface masks, settings and GPU boundary agreement.
  Iris GPU boundary maximum error was below 0.000005 blocks.
- Inspected screenshots of the shared shader-water fixture and the natural-ocean
  Iris run-up scene. Tests used Mesa llvmpipe software OpenGL and the Waves
  no-water-motion reference shader, not an arbitrary third-party shader pack.
  Sound startup was checked with a null audio device, not physical speakers.
- No Waves source changes are required. The optional Mixin is packaged in
  Puddles & Floods; test sources and the reference shader are excluded from its JAR.

Current-instance evidence (generated files, not committed):
`/workspace/Waves/build/reports/render-production/client-test.log`,
`/workspace/Waves/build/reports/render-production-no-iris/client-test.log`,
and `/workspace/Waves/build/run/productionClientGameTest/screenshots/`.
Offline account/Realms warnings and the dummy display's cursor warning occurred;
both test runners still completed successfully with their pass markers.

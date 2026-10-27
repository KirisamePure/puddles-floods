# Waves 1.21.10 compatibility regression

This test uses the real production-remapped Puddles & Floods JAR with Waves
0.4.3 (`KirisamePure/Waves`, commit `8e7662e9919e7d90731e658855d3d6a259c96106`),
Iris 1.9.7 and Sodium 0.7.3. The fixture is part of the separate Waves test mod;
none of its code or shader pack is included in the release JAR.

Build Puddles & Floods using JDK 21:

```sh
bash gradlew :1.21.10-fabric:build --configure-on-demand --no-daemon --max-workers=2
```

In an existing Waves checkout, add this Gradle init script to the normal
production test command (use an X11 OpenGL display, software Mesa is supported):

```sh
export PUDDLE_REPO=/workspace/puddles-floods
python3 tools/gradle_cloud.py runProductionClientGameTest --no-daemon --max-workers=2 \
  -I "$PUDDLE_REPO/tests/waves/compat.init.gradle"
```

The test checks actual Iris material aliasing, Waves' active pipeline and GPU
water ID, unrelated aliases being rejected, exclusive water still working,
and disabling/re-enabling shader water across Iris reloads. It also runs the
existing Waves render suite with Puddles & Floods loaded. Logs must contain
`PUDDLE_WAVES_COMPAT_PASS` and `WAVES_RENDER_TEST_PASS` and Gradle must exit 0.
Screenshots go to Waves' production client test screenshots directory.

Repeat with `-PwavesNoIris` to run the original Waves client suite with Puddles &
Floods installed and Iris completely absent. In that run only
`WAVES_RENDER_TEST_PASS` is expected; the Iris-specific test is deliberately omitted.

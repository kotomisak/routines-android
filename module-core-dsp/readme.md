# Data sound processing wrapper library

The main source of data sound processing features is
[TarsosDsp](https://github.com/JorenSix/TarsosDSP) library which is pure
JVM sound processing library (not native library dependent).

## Issues

### Bluetooth headset

Every attempt to synthetize sound when using bluetooth headset ends up
with strange issue:

`java.lang.IllegalArgumentException: The buffer size should be at least 10632 (samples) according to  AudioTrack.getMinBufferSize().`

Whenever bluetooth headset is removed (it's just about phone or wired headset) the issue disappear after app is restarted.



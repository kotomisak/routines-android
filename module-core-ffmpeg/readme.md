# 1. FFMpeg core

Contains wrapper file as an connection to ffmpeg library.
Contains feature oriented ffmpeg command utils to be called on the wrapper.

## 2 FFmpegWrapper 
Is wrapper around `com.arthenica.mobileffmpeg`   https://github.com/tanersener/mobile-ffmpeg .

## 2.1 Principle
Library which is basically ffmpeg code compiled for android.  
This library has JNI with one generic feature: call any ffmpeg command as an composed string.  
Mentioned approach is best approach when Android developer doesn't want to write any native C code, but wrap one generic native code and invoke any ffmpeg command as set of strings.  



 




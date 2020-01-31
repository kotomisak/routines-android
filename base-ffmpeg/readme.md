# 1. com.opkix.base.ffmpeg

Contains wrapper file as an connection to ffmpeg library.
Contains feature oriented ffmpeg command utils to be called on the wrapper.

## 1.2 FFmpegWrapper 
Is wrapper around `com.arthenica.mobileffmpeg` which is fork of mobile ffmpeg library described below.

# 2. com.arthenica.mobileffmpeg
Opkix has been using this library: https://github.com/tanersener/mobile-ffmpeg 

## 2.1 Principle
Library which is basically ffmpeg code compiled for android.  
This library has JNI with one generic feature: call any ffmpeg command as an composed string.  
Mentioned approach is best approach when Android developer doesn't want to write any native C code, but wrap one generic native code and invoke any ffmpeg command as set of strings.  

## 2.2 Fork

Opkix code has been using forked library rather than package distributed via maven

All Android code is copied to `com.arthenica.mobileffmpeg` without changes.  
The source of fork is here:
https://github.com/tanersener/mobile-ffmpeg/tree/master/android  

The reason `why fork` is that we can distribute attached `*.so` compiled code into `libs` directory individually for every supported architecture and thus there is support for bundles.   
Presumption whyt to fork this library is, that without fork there would be all `*.so` sources for all architectures included without respect for bundles.  



 




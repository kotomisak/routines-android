# kotox-android

Monorepo with useful Android constructs (available/reusable as separated modules).  

More about Monorepo idea is here https://jenicek.dev/2020/01/31/monorepo/     

More about my [code conventions](code-conventions.md) is here

# 0. Steps to build (run) any app here  
Practical guide first:

- Use the latest beta Android Studio version
- In proper mobile-appname directory, copy files from folder **extras/appname_sample** to folder **extras/appname** 
- In proper mobile-appname directory, rename files in **extras/appname** folder to have names just as this: **appname.properties** / **appname.jks**

# 1. Content

| app or module name                                                                         | description                                                                                                                |
|:-------------------------------------------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|                                                                            
| **core**                                                                                   | The most core code to be available across all other modules/apps                                                               |
| **core-databinding**                                                                       | Core module for databinding based apps                                                                                     |
| **core-analytics**                                                                         | Core module with analytics tracking feature                                                                                |
| **core-arch-mvvm-databinding-nav**                                                         | Core module to be used when using MVVM architecture with databinging and navigation component                              |
| **[core-dsp](./core-dsp/readme.md)**                                                       | Core module wrapping [TarsoDSP](https://github.com/JorenSix/TarsosDSP) library and offering functional API outside         |
| **[core-ffmpeg](./core-ffmpeg/readme.md)**                                                 | Core module wrapping [Tenersener](https://github.com/tanersener/mobile-ffmpeg) library and offering functional API outside |
| **core-media**                                                                             | Core media (glide/exoplayer/openGl) handling module                                                                        |
| **core-rest**                                                                              | Core module when handling REST API's (currently based on RETROFIT)                                                         |
| **core-view**                                                                              | Core module with custom views                                                                                              |
| **core-webview**                                                                           | Core module with generic webView to be used anywhere in any app                                                            |
| **[mobile-dsp-mvvm-databinding](./mobile-dsp-mvvm-databinding/readme.md)**                 | Sample application showcase of digital sound processing without JNI requirement                                            |  


# 2. Structure
This project contains apps and modules.   
It does not matter which architecture (MVVM/MVI/...) is used for it.  
Modules should be reusable across any available app.  
Every app/module should have it's own README.MD to describes detailed info about it (and it's usage).  


## 2.1 Extras
Is a set of supporting directories for every app
### graphics
Simple graphics (prepared by Gimp) to be used as base for launcher icon or banner for playstore.  
### keystore_sample
Sample keystore with sample keystore content to be quickly used as development keystore for the project.

# 3. Licence
Feel free to use this code, add star if you like it or find it useful.  
Create an ISSUE when there will be something wrong!  
Thus licence is set as [MIT](LICENSE) .  
Enjoy! MJ
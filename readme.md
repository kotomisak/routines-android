# kotox-android
Monorepo with all MJ's apps and ideas.  

More about Monorepo idea is here https://jenicek.dev/2020/01/31/monorepo/     

More about my [code conetions](code-conventions.md) is here   

# 0. Steps to build (run) any app here  
Practical guide first:

- Use the latest Stable or beta Android Studio version
- In proper mobile-appname directory, copy files from folder **extras/appname_sample** to folder **extras/appname** 
- In proper mobile-appname directory, rename files in **extras/appname** folder to have names just as this: **appname.properties** / **appname.jks**

# 1. Content

| app or module name                                       | description                                                                                                                |
|:---------------------------------------------------------|:---------------------------------------------------------------------------------------------------------------------------|
| **[mobile-dsp](./mobile-dsp/readme.md)**                 | Sample application showcase of digital sound processing without JNI requirement                                            |
| **[mobiel-template](./mobile-template/readme.md)**       | Just dummy template to be used for new app                                                                                 |
| **module-core**                                          | The most core code to be available across all other modules                                                                |
| **module-core-arch**                                     | All the boilerplate architectural code which could be extended in all other modules                                        |
| **[module-core-dsp](./module-core-dsp/readme.md)**       | Core module wrapping [TarsoDSP](https://github.com/JorenSix/TarsosDSP) library and offering functional API outside         |
| **[module-core-ffmpeg](./module-core-ffmpeg/readme.md)** | Core module wrapping [Tenersener](https://github.com/tanersener/mobile-ffmpeg) library and offering functional API outside |
| **module-core-media**                                    | Core media handling module                                                                                                 |
| **graphviewlibrary**                                     | Temporary fork of [amplitude view](https://github.com/anandBrose/AmplitudeGraphView-Android) library                       |




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
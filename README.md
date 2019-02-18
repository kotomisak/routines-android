# routines-android
Reusable scalable decoupled blocks useful for building Android app.

# Licence
Feel free to use this code, add star if you like it or find it useful.  
Create an ISSUE when there will be something wrong!  
Thus licence is set as [MIT](LICENSE) .  
Enjoy!  

# The idea behind
Every time I play with some specific feature or programming approach, such feature was always either thrown or implanted to some app without future.  
This time, there is an idea to bundle such feature/approach to separated module or app_template - and to be reusable in case you want to use it for some specific app.

# Steps to build this project
- Use Android Studio 3.5 Canary4 and newer
- copy files from folder *extras/keystore_sample* to folder *extras/keystore* 
- rename files in *extras/keystore* folder to have names just as this: *routines.properties* / *routines.jks*

# Structure
This project contains apps and modules.   
It does not matter which architecture (MVVM/MVI/...) or framework (Android native/Flutter/...) is used for it.  
Modules should be reusable in provided apps.  
Every app/module should have it's own README.MD to describes detailed info about it (and it's usage).  
All mentioned has common external dependency versions defined in root build.gradle  

	
## Apps
Every app is the minimal template to start create application of such type.  

### mobile-on-screen-nav
Is an application with simplest structure.  It means without UI navigation like drawer or bottom navigation is.  
It's something like app just with screens and navigation using on screen navigation elements.  

## Modules
Modules are rather small UI/logic features to be reusable across all app templates of this project.  
Every module should be covered with UNIT/UI testing.  

### module-core
Low level module containing really basic code (without feature dependencies) to be reusable across all other modules.  


### module-test-utils
Low level test module containing really basic test code (without feature dependencies) to be reusable across all other test modules.  

## Extras
Is a set of supporting directories  
### graphics
Simple graphics to be used as base for launcher icon or banner for playstore.  
### keystore_sample
Sample keystore with sample keystore content to be quickly used as development keystore for the project (see _Steps to build this project_ chapter)  


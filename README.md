# kotox-android
Monorepo with all MJ's apps and ideas.
 
# 0. Steps to build (run) the app  
Practical guide first:

- Use the latest Stable or beta Android Studio version
- In proper mobile-appname directory, copy files from folder **extras/appname_sample** to folder **extras/appname** 
- In proper mobile-appname directory, rename files in **extras/appname** folder to have names just as this: **appname.properties** / **appname.jks**

# 1. MonoRepo idea

Described here https://jenicek.dev/2020/01/31/monorepo/  

# 2. What I follow in development

Diversity of approaches in Android development is awesome.
There is many different architectures with their pros and cons.
Let's mention just those of them, which are closest to my mind.

## 2.1 Architecture
MVx approach is good way how to achieve app closest to clean architecture.
Apps are thus build from decoupled blocks and nicely unit testable.  

### 2.1.1 MVP
I found this approach bit noisy and don't much like this unidirectional relationship between VIEW and PRESENTER.  
It's also too much interface'ish.
Anyway, it's one of those good options, I would not have a problem to work with. 

### 2.1.2 MVVM
I have  been using this approach for years. I know it's pros and cons.
I prefer to use this for apps where state of the app is not composed by many actions at one time.
When using MVVM I try NOT to use databinding (code in XML), but just generated binding itself.

### 2.1.3 MVI
I like this style of writing unidirectional event based app flow.
I prefer to use this for apps where there is a lot of external events/inputs updating the state of the app.

## 2.2 Dependency injection
Even though there is bunch of nice looking service locators **Koin**, **Kodein**, **...** I still prefer to use full dependency injection 
framework **Dagger 2**.  
Despite on what kind of DI to use, just use dependency injection from day zero on the project!
This will help to decouple code blocks and really help with unit testing.

## 2.3 Project structure
Package by feature over package by type is the way.  
And more over, let's decompose code to modules to have self testable and decoupled parts of the app.  
This ensures easy maintenance/replacement in the future.

In case of core stuff (like networking, persistence which is not as much feature), I prefer to decouple 
those too as much as possible.

## 2.4 Data structure
Avoid using schemas defined by server as data structures.

## 2.5 Testing
100% test coverage is not wanted, since it will slow down code writing and CI machine too.  
Always choose proper coverage.  

#### 2.5.1 Unit testing
Let's start with unit testing of key parts to keep those consistent across refactoring.  

#### 2.5.2 UI testing
Let's start with UI testing of those applicationn parts, where UI is changing it's states according to inputs or controls.

## 2.6 Language
Thank's God **Kotlin** is not only first class citizen in Android, but it's also official language to be used in Google.
Thus despite many complains about maturity in comparison to Java, Kotlin has million cons over the Java from my point of view.

In therms of hybrid apps development I prefer **Flutter** over Cordova/PhoneGap/ReactNative/...  
Even when I don't like much **Dart** there is amazing idea in rendering engine (using OpenGL over native Android/iOS rendering). Thus all other mentioned hybrids are quite behind.   
And there is also type safety with Dart in comparison to Javascript, thus there is no other choice right now.  

## 2.7 Frameworks
Frameworks are good to help with non-trivial features.  
But they can die for many reasons, which leads me to 
- use language driven features over frameworks.  
- use frameworks they part of support library over third party libraries  
  
That means for example:
- use **Coroutines** over *RxJava* 
- use **Moshi** over *Gson*
- use **Retrofit** over *Volley*
- use **bindings** over *Kotlin Android Extensions*
- use **Kotlin files to bind view's behaviour** over *databinding in XML*


# 3. Structure
This project contains apps and modules.   
It does not matter which architecture (MVVM/MVI/...) or framework (Android native/Flutter/...) is used for it.  
Modules should be reusable in provided apps.  
Every app/module should have it's own README.MD to describes detailed info about it (and it's usage).  
All mentioned has common external dependency versions defined in root build.gradle  

	
## 3.1 Apps
Every app is the minimal template to start create application of such type.  

### mobile-on-screen-nav
Is an application with simplest structure.  It means without UI navigation like drawer or bottom navigation is.  
It's something like app just with screens and navigation using on screen navigation elements.  

## 3.2 Extras
Is a set of supporting directories  
### graphics
Simple graphics (prepared by Gimp) to be used as base for launcher icon or banner for playstore.  
### keystore_sample
Sample keystore with sample keystore content to be quickly used as development keystore for the project. See [Steps to build project](#3-steps-to-build-this-project) chapter 

# 4. Licence
Feel free to use this code, add star if you like it or find it useful.  
Create an ISSUE when there will be something wrong!  
Thus licence is set as [MIT](LICENSE) .  
Enjoy! MJ

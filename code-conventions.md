# Conventions/thoughts in Android development

Diversity of approaches in Android development is awesome.
There is many different architectures and approaches with their pros and cons.
Let's mention just those of them, which are closest to my mind.

## 1.1 Architecture
MVx approach is good way how to achieve app closest to clean architecture.
Apps are thus build from decoupled blocks and nicely unit testable.  

### 1.1.1 MVP
MVP well known from enterprise java development seems to me a bit obsolete in Android world. 
That's the reason I don't follow this approach.

### 1.1.2 MVVM
I have  been using this approach for years. I know it's pros and cons.
I prefer to use this for apps where state of the app is not composed by many actions at one time.
When using MVVM there is several options how to implement the app (databinding/viewbinding/...)

### 1.1.3 MVI
This approach seems to be good option in case application should express state based on many parallel events.
This approach also tends to be overcomplicated in most implementations.

## 1.2 Dependency injection
Even though there is bunch of nice looking service locators **Koin**, **Kodein**, **...** I still prefer to use full dependency injection 
framework **Dagger 2**.  
Despite on what kind of DI to use, just use dependency injection from day zero on the project!
This will help to decouple code blocks and really help with unit testing.
Quite important movement is not to use DaggerAndroid, but keep with pure dagger implementation.

## 1.3 Project structure
Package by feature over package by type is the way.  
And more over, let's decompose code to modules to have self testable and decoupled parts of the app.  
This ensures easy maintenance/replacement in the future.

In case of core stuff (like networking, persistence which is not as much feature), I prefer to decouple 
those too as much as possible.

## 1.4 Data structure
Avoid using schemas defined by server as data structures.

## 1.5 Testing
100% test coverage is not wanted, since it will slow down code writing and CI machine too.  
Always choose proper coverage.  

#### 1.5.1 Unit testing
Let's start with unit testing of key parts to keep those consistent across refactoring.  

#### 1.5.2 UI testing
Let's start with UI testing of those applicationn parts, where UI is changing it's states according to inputs or controls.

## 1.6 Language
Thank's God **Kotlin** is not only first class citizen in Android, but it's also official language to be used in Google.
Thus despite many complains about maturity in comparison to Java, Kotlin has million cons over the Java from my point of view.

I tend to follow [Kotlin lang conventions](https://kotlinlang.org/docs/reference/coding-conventions.html) and eventually [Android Kotlin style guide](https://developer.android.com/kotlin/style-guide)

In therms of hybrid apps development I prefer **Flutter** over Cordova/PhoneGap/ReactNative/...  
Even when I don't like much **Dart** there is amazing idea in rendering engine (using OpenGL over native Android/iOS rendering). Thus all other mentioned hybrids are quite behind.   
And there is also type safety with Dart in comparison to Javascript, thus there is no other choice right now.  

## 1.7 Frameworks
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




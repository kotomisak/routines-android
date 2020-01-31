plugins {
	id("com.android.library")
	kotlin("android")
	kotlin("android.extensions")
	kotlin("kapt")
}

kapt {
	useBuildCache = true
}

android {
	compileSdkVersion(App.compileSdk)

	defaultConfig {
		minSdkVersion(App.minSdk)
		targetSdkVersion(App.targetSdk)
		versionCode = App.versionCode
		versionName = App.versionName
		vectorDrawables.useSupportLibrary = true

		ndk {
			abiFilters("armeabi-v7a", "arm64-v8a")
		}
	}

	dataBinding.isEnabled = true

	buildTypes {
		getByName("release") {
			postprocessing {
				isRemoveUnusedCode = false
				isRemoveUnusedResources = false
				isObfuscate = false
				isOptimizeCode = false
				proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
			}
		}
	}

	flavorDimensions("apiVersion")

	productFlavors {
		create("develop") {
		}

		create("production") {
		}
	}

	sourceSets {
		getByName("main") {
			jniLibs.srcDirs("src/main/libs")
			jni.srcDirs(arrayOf<String>())
		}
	}

	compileOptions({
		sourceCompatibility = JavaVersion.VERSION_1_8
		targetCompatibility = JavaVersion.VERSION_1_8
	})

	lintOptions {
		setAbortOnError(false)
	}

	// since databinding can fail with >100 errors, this makes sure they are displayed
	kapt.javacOptions { option("-Xmaxerrs", 500) }

	//https://github.com/Kotlin/kotlinx.coroutines/issues/1064
	packagingOptions({
		exclude("META-INF/atomicfu.kotlin_module")
	})
}

dependencies {
	implementation(kotlin("stdlib", Versions.GradlePlugins.kotlin))

	implementation(project(":base-media"))

	implementation(Libraries.Koin.library)

	implementation(Libraries.Coroutines.core)
	implementation(Libraries.Coroutines.android)
	kapt(Libraries.Databinding.compiler)
}

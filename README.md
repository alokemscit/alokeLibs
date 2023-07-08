# libraries
Image Crop- Image Circle- Signature Pad
   
**********************for crop image------*****************

Add  in Manifest :
activity android:name="com.aloke.libraries.cropper.CropImageActivity"

Call Crop Activity:
                    CropImage.activity()
                    .setGuidelines( CropImageView.Guidelines.ON)
                   .setActivityTitle("Image Crop")
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setCropMenuCropButtonTitle("Done")
                    .setRequestedSize(400, 400)
                    .setCropMenuCropButtonIcon(R.drawable.crop_image_menu_flip)
                    .start(this);
                    
******************  Fro Activity Result ********************************
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                img.setImageURI(result.getUri());
                Toast.makeText(
                        this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }

                    # Gradle main
                    


buildscript {
    System.properties['com.android.build.gradle.overrideVersionCheck'] = 'true'
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url 'https://dl.bintray.com/kotlin/kotlin-eap' }
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.0.3'
       
        classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10'
        
    }


}


allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url 'https://maven.google.com'
        }
        maven { url "https://jitpack.io" }

        maven { url 'https://dl.bintray.com/kotlin/kotlin-eap' }


    }

}




task clean(type: Delete) {
    delete rootProject.buildDir
}

# gradle 2

plugins {
    id 'com.android.application'
    id 'maven-publish'
}
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-parcelize'
apply plugin: 'kotlin-kapt'



android {
    compileSdk 33

    defaultConfig {
        applicationId "com.aloke.library"
        minSdk 21
        targetSdk 33
        versionCode 1
        versionName "1.0"

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"

        useLibrary 'org.apache.http.legacy'
        resConfigs 'en', 'us'
        android.buildTypes.release.ndk.debugSymbolLevel ='FULL'



    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget=11
    }
    buildFeatures {
        viewBinding true
    }
    dataBinding {
        enabled = true
    }





}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    api project(':libraries')
    implementation project(path: ':engine')
    api "androidx.appcompat:appcompat:1.2.0"
    implementation 'com.google.android.material:material:1.2.1'

    implementation "androidx.cardview:cardview:1.0.0"
    implementation 'androidx.constraintlayout:constraintlayout:2.0.1'

    
}
                    

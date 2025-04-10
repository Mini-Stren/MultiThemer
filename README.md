# MultiThemer

[ ![Download](https://api.bintray.com/packages/mini-stren/maven/multithemer/images/download.svg) ](https://bintray.com/mini-stren/maven/multithemer/_latestVersion)
[![License](https://img.shields.io/badge/License-Apache%202.0-orange.svg)](https://opensource.org/licenses/Apache-2.0)

An Android Library that provides easy way to use as many app themes as you would like to.

MultiThemer will save last used application theme and restore it in app launch.

## Demo

<a href='https://play.google.com/store/apps/details?id=com.ministren.demoapp.multithemer'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width='20%' height='20%' /></a>

Demo app using MultiThemer library is available in
<a href='https://play.google.com/store/apps/details?id=com.ministren.demoapp.multithemer'>
Google Play
</a>.

You can also check <a href='https://github.com/Mini-Stren/MultiThemer/releases'>releases page</a>
for manual .apk download.

Source code for demo app is
<a href='https://github.com/Mini-Stren/MultiThemer/tree/master/app'>available here</a>

## Setup

### Gradle

Kotlin DSL:

```kotlin
dependencies {
    implementation("io.github.mini-stren:multithemer:1.5.1")
}
```       

Groovy DSL:

```gradle
dependencies {
    implementation 'io.github.mini-stren:multithemer:1.5.1'
}
```

### Maven

```maven
<dependency>
  <groupId>io.github.mini-stren</groupId>
  <artifactId>multithemer</artifactId>
  <version>1.5.1</version>
  <type>pom</type>
</dependency>
```

## Usage

Define your themes in application resources,
or use up to 20 predefined themes in `THEME` enum class.

### Initialization

Initialize `MultiThemer` in your `Application` class using method `build(Application)`.

`build(Application)` will return `Builder`, which you can use to add themes,
set icon to use in recent apps list,
set SharedPreferences that will be used to save theme tag after theme change
and set by default one of themes.

```kotlin
// Kotlin
MultiThemer.build(this)
    .useAppIcon(R.mipmap.ic_launcher)
    .addTheme("Red", R.style.Red)
    .addTheme("Indigo", R.style.Indigo, true)
    .addTheme("Blue", R.style.Blue)
    .addTheme("Green", R.style.Green)
    .addTheme("Orange", R.style.Orange)
    .initialize()
```

```java
// Java
MultiThemer.INSTANCE.build(this)
    .useAppIcon(R.mipmap.ic_launcher)
    .addTheme("Red",R.style.Red)
    .addTheme("Indigo",R.style.Indigo, true)
    .addTheme("Blue",R.style.Blue)
    .addTheme("Green",R.style.Green)
    .addTheme("Orange",R.style.Orange)
    .initialize();
```

If you will not use any of `Builder.addTheme()` methods,
it will automatically fill themes list with all predefined `THEME` themes.
It will use `THEME.INDIGO` as default theme, or you can change it with `Builder.setDefault(THEME)`.

While adding your own themes using one of `Builder.addTheme()` methods,
don't forget to set `isDefault = true` for at least one of them,
otherwise the first theme in the list will be used by default one.

`Builder.setSharedPreferences(SharedPreferences)` is optional.
`PreferenceManager.getDefaultSharedPreferences` will be used by default.

### Change themes

To let your activities automatically apply application active theme
and restart after theme change extend them from `MultiThemeActivity`.

You can use `MultiThemerListFragment` to present all app's themes
and give users easy way to switch between themes,
or use `getThemesList()` and `getActiveTheme()` to write your own theme chooser.

# License

```
Copyright 2017-2025 Mini-Stren

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

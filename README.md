![license](https://img.shields.io/badge/Platform-Android-green "Android")
![license](https://img.shields.io/badge/Licence-Apache%202.0-blue.svg "Apache")

## Table of Contents
[Introduction](#introduction)

[Data Release](#data-release)

[Codebase Organization](#codebase-organization)

[Platform Requirements](#platform-requirements)

[For Developers](#for-developers)


## Introduction
This repository contains our continous monitoring infrasturcture based on Android-MOD, a customized Android system that records system-level traces upon the occurrence of slow UI responsiveness (SUR) events, as well as our efforts for improving UI responsiveness on Android devices. Our Android-MOD system is built upon vanilla Andorid 10/11/12. Therefore, you'll be able to run codes in this repo by patching these modifications to proper framework components.

## Data Release
We have released a portion of the representative sample data (with proper anonymization) for references [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/dataset). As to the full dataset, we are still in discussion with the authority to what extend can it be released. We will make the rest dataset in public as soon as possible after receiving the permission and desensitizing the dataset.

For each file in the dataset, we list the specific information coupled with the regarding description as follows.

```
dataset
|---- model-SUR.xlsx
|---- app-SUR.xlsx
|---- app-memory.xlsx
|---- iOS-Android.xlsx
|---- energy.xlsx
```

### model-SUR.xlsx

### app-SUR.xlsx

### app-memory.xlsx

### iOS-Android.xlsx

### energy.xlsx

## Codebase Organization
Currently, We have release a portion of the implementation code for references [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/code). For the full code, we are scrutinizing the codebase to avoid possible anonymity violations. After that, we will release the source code of this study as soon as we have finished examining it and acquired its release permission from the authority. The codebase is organized as follows.

```
code
|---- monitor infrastructure
|---- authentic sensing layer
```


## Platform Requirements
### Linux
For Linux-related modifications, currently our code is run and tested in Linux kernel 5.12, 5.13, and 5.14.
Note that despite quite a number of changes have been made in Linux kernel 5.14/5.13 since Linux kernel 5.12, our code is applicable to both given that concerned tracing points and functions remain unchanged.

### Android
For Android-related modifications, currently our code is run and tested in Android 10, 11, and 12 (AOSP).
Note that despite quite a number of changes have been made in Android 12/11 since Android 10, our code is applicable to both given that concerned tracing points remain unchanged.

## For Developers
Our code is licensed under Apache 2.0 in accordance with AOSP's and Kernel's license. Please adhere to the corresponding open source policy when applying modifications and commercial uses.
Also, some of our code is currently not available but will be relased soon once we have obatained permissions.

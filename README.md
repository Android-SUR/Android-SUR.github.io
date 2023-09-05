![license](https://img.shields.io/badge/Platform-Android-green "Android")
![license](https://img.shields.io/badge/Licence-Apache%202.0-blue.svg "Apache")

## Table of Contents
[Introduction](#introduction)

[Data Release](#data-release)

[Codebase Organization](#codebase-organization)

[Platform Requirements](#platform-requirements)

[For Developers](#for-developers)


## Introduction
Our work focuses on studying slow UI responsiveness (SUR) for interactive mobile systems (especially for Android). To this end, as a major Android phone vendor, we conduct a large-scale crowd-sourced study with 47M Android phones to analyze SUR events. We deploy a continuous monitoring infrastructure to collect system-level traces upon the occurrence of SUR events through a customized Android system (dubbed Android-MOD). We then analyze the collected data and uncover multi-fold root causes of SUR events. To address the major root cause, we develop an effective solution by remodeling the process management of Android, which greatly reduces both SUR occurrence and battery consumption. This repository contains our released data, along with the implementation of continuous monitoring infrastructure and optimizations. Note that, our Android-MOD system is built upon vanilla Android 10/11/12 and Linux kernel 5.12/5.13/5.14. Therefore, you’ll be able to run codes in this repo by patching these modifications to the proper framework and kernel components.

## Data Release
We have released a portion of the representative sample data (with proper anonymization) for reference [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/data). As to the full dataset, we are still in discussion with the authority to what extent can it be released. We will make the rest dataset public as soon as possible after receiving the permission and desensitizing the dataset.

For each file in the dataset, we list the specific information coupled with the regarding description as follows.

```
dataset
|---- models.xlsx
|---- apps.xlsx
|---- app-scenario.xlsx
|---- memory.xlsx
|---- iOS-Android.xlsx
|---- energy.csv
```

### models.xlsx

| Column Name                | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| `model`                    | Device model                                                 |
| `release_time`             | Release time of the device model                             |
| `CPU_model`                | CPU type                                                     |
| `CPU_frequency`            | CPU frequency (in Hz)                                        |
| `memory`                   | RAM capacity (in MB)                                         |
| `storage`                  | ROM capacity (in MB)                                         |
| `version`                  | Android version                                              |
| `user`                     | Proportion of users for this model                           |
| `prevalence_bopt(%)`       | Prevalence of SUR events before optimizations                |
| `frequency_bopt`           | Frequency of SUR events before optimizations                 |
| `rate_bopt(%)`             | Frame drop rate of the model before optimizations            |
| `energy_bopt`              | Daily device energy before optimizations (in mAh)            |
| `prevalence_aopt(%)`       | Prevalence of SUR events after optimizations                 |
| `frequency_aopt`           | Frequency of SUR events after optimizations                  |
| `rate_aopt(%)`             | Frame drop rate of the model after optimizations             |
| `energy_aopt`              | Daily device energy after optimizations (in mAh)             |

### apps.xlsx

| Column Name                | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| `app`                      | Application name                                             |
| `package`                  | Application package name                                     |
| `category`                 | Genre or classification of the application                   |
| `DAU`                      | Daily active users                                           |
| `ADUEDPU(s)`               | Average daily user engagement duration per user (in seconds) |
| `PDAU(%)`                  | Proportion of daily active user                              |
| `DAUC`                     | Daily app usage count                                        |
| `PDAUC(%)`                 | Proportion of daily app usage count                          |
| `PDAUCPU(%)`               | Proportion of daily app usage count per user                 |
| `PDUD(%)`                  | Proportion of daily usage duration                           |
| `ANDAUTPU`                 | Average number of daily app usage times per user             |
| `NDSURPU`                  | Number of daily SUR events per user                          |

### app-scenario.xlsx

| Column Name                | Description                                                                     |
| -------------------------- | --------------------------------------------------------------------------------|
| `scenario`                 | Specific scenarios within apps, e.g. com.tencent.mm/com.tencent.mm.ui.LauncherUI|
| `SUR_cnt`                  | Daily number of SUR events of such a scenario                                   |
| `device_cnt`               | Daily number of devices where SUR events occur in such a scenario               |

### memory.xlsx

| Column Name               | Description                                                                |
| ------------------------- | ---------------------------------------------------------------------------|
| `app`                     | App name                                                                   |
| `foreground_2019`         | Memory consumption of apps in 2019 running in the foreground (in MB)       |
| `background_2019`         | Memory consumption of apps in 2019 running in the background (in MB)       |
| `foreground_2020`         | Memory consumption of apps in 2020 running in the foreground (in MB)       |
| `background_2020`         | Memory consumption of apps in 2020 running in the background (in MB)       |
| `foreground_2021`         | Memory consumption of apps in 2021 running in the foreground (in MB)       |
| `background_2021`         | Memory consumption of apps in 2021 running in the background (in MB)       |
| `foreground_2022`         | Memory consumption of apps in 2022 running in the foreground (in MB)       |
| `background_2022`         | Memory consumption of apps in 2022 running in the background (in MB)       |

### iOS-Android.xlsx

| Column Name                        | Description                                             |
| ---------------------------------- | --------------------------------------------------------|
| `app`                              | App name                                                |
| `Android_CPU_utilization(%)`       | CPU utilization of apps on Android devices              |
| `iOS_CPU_utilization(%)`           | CPU utilization of apps on iOS devices                  |
| `Android_process_number`           | Number of processes of apps on Android devices          |
| `iOS_process_number`               | Number of processes of apps on iOS devices              |
| `Android_memory_consumption`       | Memory consumption of the app on Android devices (in MB)|
| `iOS_memory_consumption`           | Memory consumption of the app on iOS devices (in MB)    |

### energy.csv

| Column Name                                 | Description                                                                                        |
| ------------------------------------------- | ---------------------------------------------------------------------------------------------------|
| `oneid`                                     | Unique identifier for the event 											                                             |
| `date`                                      | Event date                                                                                         |
| `event_name`                                | Event Name                                                                                         |
| `model`                                     | Device model                                                                                       |
| `version`                                   | Android version                                                                                    |
| `battery_capacity`                          | Total capacity of the device's battery (in mAh)                                                    |
| `battery_life_screen_on_duration`           | Duration the battery lasts with the screen turned on (in seconds)                                  |
| `battery_life_screen_off_duration`          | Duration the battery lasts with the screen turned off (in seconds)                                 |
| `battery_life_screen_off_at_night`          | Duration the battery lasts overnight with the screen turned off (in seconds)                       |
| `battery_life_screen_off_dry`               | Duration the battery lasts with the screen turned off without any background processes running (in seconds)|
| `battery_life_screen_off_at_night_dry`      | Duration the battery lasts overnight with the screen turned off without any background processes (in seconds)|
| `battery_charging_duration`                 | Duration taken to charge the battery (in seconds)                                                  |
| `battery_on_battery_duration`               | Duration the device battery lasts without charging (in seconds)                            |
| `battery_total_consumption`                 | Total battery consumption (in mAh)                                                                 |


## Codebase Organization
Currently, We have released a portion of the implementation code for reference [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/code). For the full code, we are scrutinizing the codebase to avoid possible anonymity violations. After that, we will release the source code of this study as soon as we have finished examining it and acquired its release permission from the authority. The codebase is organized as follows.

```
code
|---- continuous monitor infrastructure
|---- remodeling
```
+ `continuous monitor infrastructure/` contains the source code of our continuous monitor infrastructure.
+ `remodeling/` includes several modules for remodeling the process management of Android (e.g., the uniform authentic sensing layer).


## Platform Requirements
### Linux
For modifications pertaining to Linux, our code is executed and validated on Linux kernel 5.12, 5.13, and 5.14. It's worth noting that, despite substantial revisions introduced in Linux kernel 5.13 and 5.14 relative to 5.12, our code remains compatible across these versions due to the consistency of specific tracing points and functions.

### Android
For Android-related modifications, our code is run and tested in Android 10, 11, and 12 (AOSP).
Note that although quite a number of changes have been made in Android 12/11 since Android 10, our code is applicable to both given that concerned tracing points remain unchanged.

## For Developers
Our code is licensed under Apache 2.0 in accordance with AOSP’s and Kernel’s licenses. Please adhere to the corresponding open-source policy when applying modifications and commercial uses. 
Also, some of our code is currently not available but will be released soon once we have obtained permission.

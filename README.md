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
We have released a portion of the representative sample data (with proper anonymization) for references [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/data). As to the full dataset, we are still in discussion with the authority to what extend can it be released. We will make the rest dataset in public as soon as possible after receiving the permission and desensitizing the dataset.

For each file in the dataset, we list the specific information coupled with the regarding description as follows.

```
dataset
|---- models.xlsx
|---- apps.xlsx
|---- app-scenario.xlsx
|---- memory.xlsx
|---- iOS-Android.xlsx
|---- energy.xlsx
```

### models.xlsx

| Column Name                | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| `model`                    | Device model                                                 |
| `release_time`             | Release time of the model                                    |
| `CPU_model`                | CPU type                                                     |
| `CPU_frequency`            | CPU frequency                                                |
| `memory`                   | RAM capacity                                                 |
| `storage`                  | ROM capacity                                                 |
| `version`                  | Android version                                              |
| `user`                     | Proportion of users                                         |
| `prevalence_bopt`          | Prevalence of SUR events before optimizations                |
| `frequency_bopt`           | Frequency of SUR events before optimizations                 |
| `rate_bopt`                | Frame drop rate of the model before optimizations            |
| `energy_bopt`              | Daily device energy before optimizations                     |
| `prevalence_aopt`          | Prevalence of SUR events after optimizations                 |
| `frequency_aopt`           | Frequency of SUR events after optimizations                  |
| `rate_aopt`                | Frame drop rate of the model after optimizations             |
| `energy_aopt`              | Daily device energy after optimizations                      |

### apps.xlsx

| Column Name                | Description                                                  |
| -------------------------- | ------------------------------------------------------------ |
| `app`                      | App name                                                     |
| `package`                  | App packgae name                                             |
| `category`                 | App Category                                                 |
| `DAU`                      | Daily active users                                           |
| `DAUTPU(s)`                | Daily average usage time per user in seconds                 |
| `PDAU(%)`                  | Proportion of daily active user                              |
| `NDAUT`                    | Number of daily app usage times                              |
| `PDAUT(%)`                 | Proportion of daily app usage times                          |
| `PDAUTPU(%)`               | Proportion of daily app usage times per user                 |
| `PDUD(%)`                  | Proportion of daily usage duration                           |
| `ANDAUTPU`                 | Average number of daily app usage times per user             |
| `NDSURPU`                  | Number of daily SUR events per user                          |

### app-scenario.xlsx

| Column Name                | Description                                                                          |
| -------------------------- | ------------------------------------------------------------------------------------ |
| `scenario`                 | Specific functions within an app, such as com.tencent.mm/com.tencent.mm.ui.LauncherUI|
| `SUR_cnt`                  | Daily number of SUR events of such scenario                                          |
| `device_cnt`               | Daily number of devices where SUR events occurs in such scenario                     |

### memory.xlsx

| Column Name               | Description                                                                              |
| ------------------------- | -----------------------------------------------------------------------------------------|
| `app`                     | App name                                                                                 |
| `foreground_2019`         | Memory consumption of apps in 2019 when running in the foreground                        |
| `background_2019`         | Memory consumption of apps in 2019 when running in the background                        |
| `foreground_2020`         | Memory consumption of apps in 2020 when running in the foreground                        |
| `background_2020`         | Memory consumption of apps in 2020 when running in the background                        |
| `foreground_2021`         | Memory consumption of apps in 2021 when running in the foreground                        |
| `background_2021`         | Memory consumption of apps in 2021 when running in the background                        |
| `foreground_2022`         | Memory consumption of apps in 2022 when running in the foreground                        |
| `background_2022`         | Memory consumption of apps in 2022 when running in the background                        |

### iOS-Android.xlsx

| Column Name                        | Description                                             |
| ---------------------------------- | --------------------------------------------------------|
| `app`                              | App name                                                |
| `Android_CPU_utilization(%)`       | CPU utilization of apps on Android devices              |
| `iOS_CPU_utilization(%)`           | CPU utilization of apps on iOS devices                  |
| `Android_process_number`           | Number of processes in apps on Android devices          |
| `iOS_process_number`               | Number of processes in apps on iOS devices              |
| `Android_memory_consumption(MB)`   | Memory consumption of the app on Android devices|
| `iOS_memory_consumption(MB)`       | Memory consumption of the app on iOS devices    |

### energy.xlsx

| Column Name                                 | Description                                                                                        |
| ------------------------------------------- | ---------------------------------------------------------------------------------------------------|
| `oneid`                                     | Unique identifier for the device 											                       |
| `date`                                      | Event date                                                                                         |
| `event_name`                                | Event Name                                                                                         |
| `model`                                     | Device model                                                                                       |
| `version`                                   | Android version                                                                                    |
| `battery_capacity`                          | Total capacity of the device's battery (typically measured in mAh)                                 |
| `battery_life_screen_on_duration`           | Duration the battery lasts with the screen turned on                                               |
| `battery_life_screen_off_duration`          | Duration the battery lasts with the screen turned off                                              |
| `battery_life_screen_off_at_night`          | Duration the battery lasts overnight with the screen turned off                                    |
| `battery_life_screen_off_dry`               | Duration the battery lasts with the screen turned off without any background processes running     |
| `battery_life_screen_off_at_night_dry`      | Duration the battery lasts overnight with the screen turned off without any background processes   |
| `battery_charging_duration`                 | Duration taken to charge the battery                                                               |
| `battery_on_battery_duration`               | Duration the device runs on battery power without charging                                         |
| `battery_total_consumption`                 | Total battery consumption                                                                          |


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

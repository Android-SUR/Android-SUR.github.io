![license](https://img.shields.io/badge/Platform-Android-green "Android")
![license](https://img.shields.io/badge/Licence-Apache%202.0-blue.svg "Apache")

## Table of Contents
[Introduction](#introduction)

[Data Release](#data-release)

[Platform Requirements](#platform-requirements)

[Codebase Organization](#codebase-organization)

[For Developers](#for-developers)


## Introduction
Our research focuses on slow Android UI responsiveness for interactive mobile systems, particularly on Android devices. Currently, there is a lack of comprehensive research in the academic community regarding slow Android UI responsiveness, and the fundamental causes behind it are not clear. Leveraging our position as a major Android phone vendor, we conduct an extensive crowdsourced study involving 47 million Android devices to meticulously scrutinize SUR occurrences. To achieve this, we have implemented a continuous monitoring infrastructure for collecting data at the system level whenever SUR events manifest. This infrastructure is seamlessly integrated into a tailored Android system dubbed Android-MOD. Subsequently, we analyze the collected data, unveiling a multitude of underlying factors contributing to SUR events. Upon closer examination, our analysis reveals that the majority of SUR events can be attributed to system-wise resource contention, primarily caused by the wide existence of "hogging" apps. In response to this primary root cause, we propose an effective solution by remodeling the Android process management. This innovation has yielded substantial reductions in both the occurrence of SUR events and overall battery consumption. 

This repository contains our released data and the implementation code of 1) the continuous monitoring infrastructure and 2) optimization techniques. It's worth noting that our Android-MOD system is built upon the foundation of vanilla Android versions 10, 11, and 12, in conjunction with Linux kernel versions 5.12, 5.13, and 5.14. Consequently, developers can run the code in this repository by applying these modifications to the respective framework and kernel components. 

## Data Release
We have thoughtfully made available a portion of the representative dataset [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/data), taking meticulous care to ensure proper anonymization. Regarding the complete dataset, we are currently engaged in discussions with the relevant authority to determine the extent to which it can be publicly released. We will release the complete dataset once we have obtained the required permissions and conducted the requisite data desensitization procedures. 

For each file in the dataset, we have provided specific information in conjunction with their respective descriptions, outlined as follows:

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
| `energy_bopt`              | Daily energy consumption of the device before optimizations (in mAh)            |
| `prevalence_aopt(%)`       | Prevalence of SUR events after optimizations                 |
| `frequency_aopt`           | Frequency of SUR events after optimizations                  |
| `rate_aopt(%)`             | Frame drop rate of the model after optimizations             |
| `energy_aopt`              | Daily energy consumption of the device after optimizations (in mAh)             |

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
| `oneid`                                     | Unique identifier for the event                                                                                          |
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


## Platform Requirements
### Linux
For modifications pertaining to Linux, our code has been punctiliously executed and rigorously validated across multiple Linux kernel versions, specifically 5.12, 5.13, and 5.14. It's noteworthy that, despite the substantial revisions introduced in Linux kernel 5.13 and 5.14 relative to 5.12, our code maintains its compatibility seamlessly across these versions, owing to the consistency of specific tracing points and core functions.

### Android
For Android-related modifications, our code has been methodically deployed and rigorously tested on Android 10, 11, and 12. It's important to highlight that despite the significant evolutionary changes introduced in Android 12 and 11, in comparison to Android 10, our code seamlessly applies to all of these versions due to the stability of the pertinent tracing points and relevant functions.

## Codebase Organization
At present, we have made available a portion of our implementation code for your reference [here](https://github.com/Android-SUR/Android-SUR.github.io/tree/main/code). We are currently engaged in a thorough review of the codebase to ensure that it complies with all privacy regulations and standards. Upon the successful completion of this examination and the receipt of requisite permissions from the relevant authority, we will release the complete source code of this study.

The codebase has been structured as follows:
```
code
|---- continuous monitor infrastructure
|---- remodeling
```
+ `continuous monitor infrastructure/` contains the source code of our continuous monitor infrastructure.
+ `remodeling/` includes several modules for remodeling the process management of Android (e.g., the uniform authentic sensing layer).



## For Developers
For developers engaging with our codebase, it's imperative to recognize that our software is licensed under the Apache 2.0 license, in alignment with the licensing policies of both the Android Open Source Project (AOSP) and the Linux Kernel. We kindly request that all modifications and commercial utilization adhere to the corresponding open-source policy. Additionally, we wish to convey that while certain segments of our code may currently remain unavailable, we are working towards securing the necessary permissions and will release these sections in due course.

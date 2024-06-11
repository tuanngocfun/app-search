# Android Java Project

Welcome to the Android Java Project! This repository contains an Android application built using Java and Gradle. Follow the instructions below to clone the repository, set up the project, and run it using Android Studio.

## Prerequisites

Before you begin, ensure you have met the following requirements:

- **Java Development Kit (JDK)**: Version 8 or higher.
- **Android Studio**: Version 2023.
- **Android SDK**: Android 14.0 (UpsideDownCake) (API Level 34).
- **Git**: Version control system to clone the repository.

## Clone the Repository

To clone the repository, follow these steps:

1. Open your terminal or command prompt.
2. Run the following command to clone the repository:

    ```bash
    git clone https://github.com/tuanngocfun/app-search.git
    ```

3. Navigate to the project directory:

    ```bash
    cd app-search
    ```

## Open the Project in Android Studio

1. Launch Android Studio.
2. Click on **Open an existing Android Studio project**.
3. Navigate to the cloned repository directory and select it.
4. Android Studio will start syncing the project with Gradle. This may take a few minutes.

## Syncing with Gradle Files

1. Once the project is open, sync the project with Gradle files:
    - Go to **File > Sync Project with Gradle Files** or use the shortcut `Ctrl+Shift+O`.

2. Check for the `build.gradle.kts` script:
    - Go to the **Gradle Scripts** section in the project navigator.
    - Open `build.gradle.kts` to inspect or modify the build configuration.

## Setup and Configuration

1. Ensure that you have the necessary SDK platforms and tools installed:
    - Go to **File > Project Structure**.
    - Ensure the SDK Location is set correctly.
    - Go to **File > Settings > Appearance & Behavior > System Settings > Android SDK**.
    - Check the required SDK platforms and tools and click **Apply**.

## Build and Run the Project

1. To build the project, click on the **Build** menu and select **Make Project**.
2. To run the project on an emulator or a connected device, click on the **Run** menu and select **Run 'app'**.
    - Ensure you have a configured device or emulator. You can set up an emulator by going to **AVD Manager** and creating a new virtual device.
3. Select the target device and click **OK**.
    - The project will build and deploy to the selected device or emulator.

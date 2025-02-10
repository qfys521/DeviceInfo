# Device Information App

This project is an Android application that displays various device information such as device name, model, OS version, memory, storage, and more.

## Features

- Display device information (name, model, OS version, brand, manufacturer, SDK version, system language, supported ABIs)
- Display memory information
- Display storage information
- Display file information in the current directory
- Display CPU information
- Display local and telephony country codes
- Copy information to clipboard
- Expand and collapse additional information

## Technologies Used

- Kotlin
- Java
- Gradle

## Setup

### Prerequisites

- Android Studio or IntelliJ IDEA
- Android SDK

### Installation

1. Clone the repository:
    ```sh
    git clone https://github.com/qfys521/deviceinfos.git
    ```
2. Open the project in Android Studio or IntelliJ IDEA.
3. Sync the project with Gradle files.
4. Build and run the application on an Android device or emulator.

## Usage

1. Launch the application on your Android device.
2. The main screen will display various device information.
3. Long press on the displayed information to copy it to the clipboard or expand/collapse additional information.
4. Click the "Action" button to show the dialog with options to copy or expand/collapse information.

## Code Structure

- `Main.kt`: The main activity that sets up the UI and displays device information.
- `local.properties`: Contains local configuration for the Android SDK path.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Commit your changes (`git commit -am 'Add new feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License. See the `LICENSE` file for more details.

## Acknowledgements

- Thanks to the Android and Kotlin communities for their support and resources.
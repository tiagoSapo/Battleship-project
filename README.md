# Battleship Android Application

This is the official repository of the Battleship application, developed as part of a project for the Mobile Computing course in my Master's Degree.

## About the Project

The Battleship application was born from the proposal of the Mobile Computing course to develop an application for Android mobile devices. The goal was to transpose the traditional and popular game of Battleship into the digital form, while maintaining its essence and providing players with new experiences. In addition to entertainment, the application also has an educational aspect, challenging the skills and strategies of the players.

## Technologies

### Activities and Fragments

Dynamic fragments managed by a single activity were utilized to achieve superior efficiency and performance.

### View Model

The Android ViewModel class was implemented to transmit temporary information between fragments.

### Database, Threads, and Implicit Intent

The SQLite database embedded in Android was used to record players and game history. Database access operations were performed in parallel using Threads.

### MQTT and Arduino

The application supports "single-player Arduino" mode, where players can face the Arduino. The MQTT protocol was used for communication between the application and the Arduino.

### Google Firebase

The "multiplayer" mode was implemented using Google's Firebase Realtime Database to allow players to play against each other on different devices.

## Planning and Development

The project was divided into phases of planning, development, testing, and documentation. During development, a detailed methodology was followed that included requirements specification, architecture, mockups, and implementation of functionalities.

## Key Features

- User registration with profile picture
- Game modes: Single-player against CPU, Single-player against Arduino, and Multiplayer
- Game history
- Specific layouts for Portrait and Landscape orientations

## Functional and Non-Functional Requirements

### Functional

- User registration
- Various game modes
- Game history lookup
- Communication with Arduino
- User authentication security

### Non-Functional

- User information security
- Usability based on Jakob Nielsen's heuristics
- Project completion by January 6, 2023
- Development in Java for Android

## Constraints

- Delivery by January 6, 2023
- Development exclusively for Android
- Use of SQLite database, Threads, and MQTT
- Communication with Arduino

## Conclusion

The Battleship application is the result of a joint effort to offer a fun and challenging experience to players, while meeting the requirements established by the Mobile Computing course.

If you have any questions or suggestions, feel free to contact us or open an issue in this repository.

Have fun playing! 🚢🎮

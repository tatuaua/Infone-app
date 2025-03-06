# Infone

Infone is a simple application that sends small data points to the user everyday in the form of a notification. 
A user can choose which data points they want to receive and at what time they want to receive them.

## Structure

```app/src/main/assets/config.json``` contains config

```app/src/main/java/com/infone/data/``` contains stuff related to data retrieval/saving

```app/src/main/java/com/infone/model/``` contains models

```app/src/main/java/com/infone/notification/``` contains stuff related to notifications

```app/src/main/java/com/infone/utils/``` contains utils

```app/src/main/java/com/infone/MainActivity.kt``` contains UI and main logic

## TODO

- [x] Create a basic structure for the app
- [x] Create notification system
- [x] Create preferences system
- [ ] Improve app UI
- [ ] Improve notification layout
- [ ] Add way to request new data points
- [ ] Add way to search data points
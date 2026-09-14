# JavaFX Media Player
A desktop media player built with JavaFX and Scene Builder that lets users import, organize and enjoy their local audio and video collection. 
Featuring metadata extraction, video thumbnail generation, playlist management and persistent local storage.

## Table of Contents
- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)
- [Features](#features)
- [Project Structure](#project-structure)
- [Known Limitations](#known-limitations)

## Overview
This project simulates a desktop application that lets users load their own audio and video files, browse them by type, search and sort by relevant attributes
as well as organize them into custom playlists. The app extracts and displays media metadata ( such as artist, album, release year and lyrics for audio files) and 
generates thumbnails for video files. All user data, including favorites and playlists, is saved locally between sessions.

## Tech Stack

### UI
- JavaFX, Desktop UI framework
- Scene Builder, Visual FXML layout design
- ControlsFX - Extra UI controls not included in core JavaFX

 ### Media Processing
 - JavaCV, Video frame grabbing and thumbnail extraction via `FFmpegFrameGrabber`
 - JAudioTagger, Audio metadata reading
 - mp4parser, MP4/video container metadata parsing
 - SLF4J, Logging facade used by the above libraries

### Data and Persistens
- JAXB, XML serialization for favorites
- Jackson, JSON mapping
- Gson, JSON serialization for playlists
- Local file system storage, Imported media, thumbnails and user images

### Build
- Maven, Dependency management and build
- javafx-maven-plugin, Running and packaging the JavaFX app

## Getting Started

### Prerequisites
- Java(JDK 22+)
- Maven
- FFmpeg available on the system path

### Running the Application
```bash
git clone https://github.com/Kuronohono/MediaPlayer.git
cd MediaPlayer
mvn clean javafx:run
```

## Features

### Home Page
- Favorites overview
- Recently added items
- Quick access to the first four playlists

### Music Page
- Displays all imported audio files
- Can import one or more audio files at the same time
- Sort and Search by name, artist or album
- Side panel with extended details: album image, release year, lyrics and other metadata

### Video Page
- Displays all imported video files
- Search by title
- Automatic thumbnail extraction via FFmpegFrameGrabber
- Side panel with extended video details

### Playlists Page

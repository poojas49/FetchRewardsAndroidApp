# Fetch Rewards Android App

This Android application displays data retrieved from the Fetch Hiring API in an organized, user-friendly list.

## Features

- Fetches data from https://fetch-hiring.s3.amazonaws.com/hiring.json
- Groups items by listId with collapsible sections
- Sorts items first by listId, then by name
- Filters out items with blank or null names
- Includes pull-to-refresh functionality
- Provides smooth animations and visual feedback

## Implementation

- Language: Java
- Architecture: MVVM
- Libraries:
    - Retrofit for networking
    - Moshi for JSON parsing
    - RecyclerView for displaying data
    - LiveData and ViewModel for UI updates

## Setup

1. Clone the repository
2. Open in Android Studio
3. Build and run on an emulator or physical device (minimum SDK 24)

## Requirements

This project was created as part of the Fetch Rewards coding exercise, focusing on displaying data from an API endpoint in a clean, organized manner that meets specific business requirements.
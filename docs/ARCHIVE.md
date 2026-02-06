# Archiving Conference Data

## Overview

The app supports year-based API versioning. The current year fetches live data from Sessionize, while previous years serve static JSON files.

Key components:
- `ConferenceConfig` (`application.yaml`) - Defines `currentYear` and `supportedYears`
- `ArchivedDataService` - Loads static JSON from `resources/archived/{YEAR}/conference.json`
- `SessionizeService` - Fetches live data for the current year

## Archiving Steps

Replace `{YEAR}` below with the year being archived (e.g., `2025`) and `{NEW_YEAR}` with the upcoming year.

### 1. Export and Save Conference Data

Once the new year's Sessionize data is ready, export the existing data and create the archive of the previous year:

```bash
mkdir -p backend/src/main/resources/archived/{YEAR}
curl -L https://kotlinconf-app-prod.labs.jb.gg/{YEAR}/conference > backend/src/main/resources/archived/{YEAR}/conference.json
```

### 2. Update Configuration

Edit `backend/src/main/resources/application.yaml`, updating `currentYear` and `supportedYears`:

```yaml
conference:
  currentYear: {NEW_YEAR}
  supportedYears: [..., {YEAR}, {NEW_YEAR}]
```

### 3. Verify

```bash
./gradlew :backend:run
```

- http://0.0.0.0:8080/{YEAR}/conference should return the archived data
- http://0.0.0.0:8080/{NEW_YEAR}/conference should return the live Sessionize data
- http://0.0.0.0:8080/conference should also return the live Sessionize data

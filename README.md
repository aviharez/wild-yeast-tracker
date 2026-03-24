# Wild Yeast Tracker

A command-line utility from **Copper & Moss Meadery** that checks real-time local weather conditions against optimal thresholds for spontaneous fermentation catchment, then logs every reading to a CSV for historical analysis.

---

## How It Works

Each time you run the tool it: 

1. Reads your API key and meadery coordinates from `.env` file.
2. Call the [OpenWeatherMap Current Weather API](https://openweathermap.org/current) for those coordinates.
3. Compares the returned temperature and humidity against your thresholds.
4. Prints a **catchment status** (`Optimal`, `Sub-optimal`, or `Closed`) to the console.
5. Appends a timestamped row to `yeast_log.csv` so you build a historical record over time.

### Catchment Status Rules

| Status            | Temperature       | Humidity          |
|-------------------|-------------------|-------------------|
| **Optimal**       | 60 - 75 °F        | ≥ 50 %            |
| **Sub-optimal**   | one condition met | one condition met |
| **Closed**        | outside range     | below minimum     |

All thresholds are configurable via the `.env` file.

---

## Prerequisites

| Requirement     | Version      | Notes                                    |
|-----------------|--------------|------------------------------------------|
| Java (JDK)      | 11 or later  | -                                        |
| Apache Maven    | 3.8 or later | -                                        |
| Internet access | -            | Required to reach the OpenWeatherMap API |

> **IntelliJ IDEA users:** IntelliJ bundles Maven. You do not need to install it separately; use the Maven panel on the right-hand side of the IDE.

---

## Get a Free API Key

1. Go to [https://openweathermap.org/api](https://openweathermap.org/api).
2. Click **Sign Up** and create a free account.
3. After confirming your email, open your account dashboard and navigate to **API keys**.
4. Copy the default key (or generate a new one).

> **Important:** New API keys can take **up to 2 hours** to become active. If you get a `401 Invalid API key` error right after signing up, wait and try again.

---

## Configure the Application

1. In the project root folder, find the file named `.env.example`.
2. **Copy** it and rename the copy to `.env` (no `.example` extension).
3. Open `.env` in any text editor and fill in your values:

```dotenv
# REQUIRED: OpenWeatherMap API Key
OWM_API_KEY=your_key

# REQUIRED: Meadery Coordinates
MEADERY_LATITUDE=-6.175
MEADERY_LONGITUDE=106.827

# OPTIONAL: Catchment Thresholds
TEMP_MIN_F=60.0
TEMP_MAX_F=75.0
HUMIDITY_MIN=50.0

# OPTIONAL: Network Timeouts in seconds
CONNECT_TIMEOUT_SECONDS=10
READ_TIMEOUT_SECONDS=10

# OPTIONAL: CSV log file path
LOG_FILE=yeast_log.csv
```

> **Security note:** The `.env` file is listed in `.gitignore` and will never be committed to version control. Keep it on your local machine only.

---

## Build the Application

Open a terminal in the project root folder (the folder that contains `pom.xml`).

```bash
mvn package -q
```

This downloads dependencies and produces a single executable JAR:

```
target/wild-yeast-tracker-jar-with-dependencies.jar
```

> In IntelliJ, you can also double-click **Lifecycle → package** in the Maven panel instead of using the terminal.

---

## Run the Application

```bash
java -jar target/wild-yeast-tracker-jar-with-dependencies.jar
```

### Example output

```
========================================
  Copper & Moss - Wild Yeast Tracker
========================================
  Run time : Tue, Mar 24 2026 22:28 WIB
----------------------------------------
Fetching weather for coordinates (-6.1750, 106.8270)...

  Conditions  : Haze
  Temperature : 81.1F
  Humidity    : 83%

  Status: Sub-optimal (temp 81.1F is outside [60-75F]; humidity 83% meets minimum 50%)

  [ SUB-OPTIMAL ] Conditions are marginal; proceed with caution.

  Logged to: C:\Users\syifa\Documents\Work\Postwork\intellij\wild-yeast-tracker\yeast_log.csv
========================================
```

---
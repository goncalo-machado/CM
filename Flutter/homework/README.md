# CM Homework

Homework for CM. This project is a simple pokedex with details of each pokemon, a favorites tab and a team tab. It uses the [PokeAPI](https://pokeapi.co/docs/v2#pokemon-section) to get all pokemons and their details.

# Instructions:

Before installing this project, make sure your phone has USB Debugging on and is connected to your PC.

1. Go to project directory

```
cd path_to_homework/homework
```

2. Clean the project

```
flutter clean
```

3. Get packages

```
flutter pub get
```

4. Build the apk

```
flutter build apk --release
```

5. Get the id of the device

```
flutter devices
```

The output is something like this:

```
Found 3 connected devices:
  SM A505FN (mobile) • R58M9055C5R • android-arm64  • Android 11 (API 30)
  Linux (desktop)    • linux       • linux-x64      • Ubuntu 24.04.1 LTS 6.8.0-45-generic
  Chrome (web)       • chrome      • web-javascript • Google Chrome 129.0.6668.70
```

In this case the id is SM A505FN

6. Install the apk on the device

```
flutter install -d {device-id}
```
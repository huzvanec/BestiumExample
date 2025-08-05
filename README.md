# Bestium Example

A demonstration plugin for [Bestium](https://github.com/huzvanec/Bestium), a library for creating custom entities in
PaperMC.

If you're new to Bestium, make sure to check out the
[documentation](https://docs.bestium.jeme.cz).

## Contents

This plugin currently adds:

### Capybaras

- Capybaras have 5 hearts of health.
- Spawn naturally in river biomes.
- Have two variants:
    - `normal` in rivers
    - `blue` in frozen rivers
- Can be bred with seagrass or melon slices.

### `/bexample` command

Purely for demonstration purposes:

```
/bexample spawnben
```

spawns a Capybara named "Ben the capybara".

## Building

Requirements:

- [Git](https://git-scm.com/downloads)
- [Java 21](https://www.oracle.com/java/technologies/downloads/#java21)

```shell
git clone https://github.com/huzvanec/BestiumExample.git
cd BestiumExample
./gradlew build
```

The plugin jar is located in `build/libs/`.
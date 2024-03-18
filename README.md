<h1 align="center">
  <br/>
  <a href="https://github.com/DerEingerostete/BungeeBan-Plus"><img src="https://files.dereingerostete.dev/GitHubAssets/BungeeBan-Plus-Logo.png" alt="BungeeBan Plus" width="600"></a>
</h1>

___

<p align="center">
  <a href="#commands">Commands</a> •
  <a href="#config">Config</a> •
  <a href="#language-support">Language Support</a> •
  <a href="#download">Download</a> •
  <a href="#license">License</a>
</p>

## Disclaimer

> **Warning**<br/>
> This was a private project.<br/>
> It is currently not complete and needs testing

## Commands
| Command  |                    Description                     |                      Usage                       | Permission        |
|:---------|:--------------------------------------------------:|:------------------------------------------------:|-------------------|
| /ban     |        Bans a player with the given reason         |            `/ban <player> <reasonId>`            | bungeeban.ban     |
| /mute    |        Mutes a player with the given reason        |           `/mute <player> <reasonId>`            | bungeeban.mute    |
| /unban   |        Removes the active bans of a player         |                 `/ban <player>`                  | bungeeban.unban   |
| /unmute  |        Removes the active mute of a player         |                 `/mute <player>`                 | bungeeban.unmute  |
| /reasons | Displays information about reasons for punishment  |           `/reasons <mute/ban> <O: id>`           | bungeeban.reasons |
| /history | Displays information about the history of a player | `/history <player> <O: activities/mutes/bans>`  | bungeeban.history |
| /chatlog |             Allows to manage chat logs             |    `/chatlog <view/create/player> <player/id>`     | bungeeban.chatlog |

> **Note**<br/>
> Options with the prefix `O:` are optional

## Config
Here is a list of all configurations used.
<details>
  <summary>config.json</summary>

### `config.json`
```json
{
    "database": "sqlite",
    "language": "en",
    "cacheDuration": 1800000,
    "debug": false
}
```

| Key             |             Description             |  Available values   |
|:----------------|:-----------------------------------:|:-------------------:|
| `database`      |      The type of database used      | `sqlite` or `mysql` |
| `language`      |       The language file used        |          -          |
| `cacheDuration` |   The duration of the cache in ms   | Any valid duration  |
| `debug`         | If debug messages should be enabled |  `true` or `false`  |
</details>

<details>
  <summary>database.json</summary>

### `database.json`
```json
{
    "uptimeCheck": 900000,
    "mysql": {
        "hostname": "localhost",
        "port": 3306,
        "database": "BungeeBan",
        "username": "username",
        "password": "password"
    },
    "sqlite": {
        "path": "sqlite.db"
    }
}
```

| Key           |                   Description                    |  Available values  |
|:--------------|:------------------------------------------------:|:------------------:|
| `uptimeCheck` | The duration to wait between uptime checks in ms | Any valid duration |
| `mysql`       |           MySQL database configuration           |     See mysql      |
| `sqlite`      |          SQLite database configuration           |     See sqlite     |

### `mysql` Object
| Key        |       Description       |    Available values     |
|:-----------|:-----------------------:|:-----------------------:|
| `hostname` |   The MySQL hostname    |   Any valid hostnames   |
| `port`     |     The MySQL port      |     Any valid port      |
| `database` | The MySQL database name | Any valid database name |
| `username` |   The MySQL username    |            -            |
| `password` |   The MySQL password    |            -            |

### `sqlite` Object
| Key    |            Description            | Available values |
|:-------|:---------------------------------:|:----------------:|
| `path` | The filepath to the database file |        -         |
</details>

<details>
  <summary>chatlog.json</summary>

### `chatlog.json`
```json
{
    "actions": {
        "enableLinkGeneration": true,
        "uploader": "pastebin",
        "chatBacklog": 50
    },
    "pastebin": {
        "expiration": "TEN_MINUTES",
        "visibility": "UNLISTED",
        "developerKey": null,
        "userKey": null
    },
    "hastebin": {
        "url": "https://www.toptal.com/developers/hastebin"
    },
    "header": "  ____                              ____              \n |  _ \\                            |  _ \\             \n | |_) |_   _ _ __   __ _  ___  ___| |_) | __ _ _ __  \n |  _ <| | | | '_ \\ / _` |/ _ \\/ _ \\  _ < / _` | '_ \\ \n | |_) | |_| | | | | (_| |  __/  __/ |_) | (_| | | | |\n |____/ \\__,_|_| |_|\\__, |\\___|\\___|____/ \\__,_|_| |_|\n                     __/ |                            \n                    |___/                             ",
    "mutedCommands": [
        "me",
        "say",
        "w",
        "m",
        "pm",
        "whisper",
        "msg",
        "tell",
        "r",
        "reply",
        "p msg"
    ]
}
```

| Key             |           Description            |    Available values     |
|:----------------|:--------------------------------:|:-----------------------:|
| `actions`       | The activated / selected actions |       See actions       |
| `pastebin`      |      Pastebin configuration      |      See pastebin       |
| `hastebin`      |      Hastebin configuration      |      See hastebin       |
| `header`        |   The file header of chat logs   |        Any text         |
| `mutedCommands` |    An array of muted commands    |    Any valid command    |

### `actions` Object
| Key                    |          Description          |     Available values     |
|:-----------------------|:-----------------------------:|:------------------------:|
| `enableLinkGeneration` |       Currently unused        |    `true` or `false`     |
| `uploader`             |     The enabled uploader      | `hastebin` or `pastebin` |
| `chatBacklog`          | The amount of messages to log |   Any positive number    |

### `pastebin` Object
| Key            |                Description                |                    Available values                     |
|:---------------|:-----------------------------------------:|:-------------------------------------------------------:|
| `expiration`   | The time the generated link is accessible |                            -                            |
| `visibility`   |   The visibility of the generated link    |            `PRIVATE`, `PUBLIC` or `UNLISTED`            |
| `developerKey` |        The pastebin developer key         | See [API specification](https://pastebin.com/doc_api#1) |
| `userKey`      |           The pastebin user key           | See [API specification](https://pastebin.com/doc_api#9) |

### `hastebin` Object
| Key   |       Description        | Available values |
|:------|:------------------------:|:----------------:|
| `url` | URL to a hastebin server |  Any valid url   |
</details>

## Language Support
To allow multiple languages a folder named `lang` is created at the first start, in which language configurations can be created.<br/>
These are selected with the `language` option in the `config.json`.

## Download
Currently, there is no resource on SpigotMC yet, so the plugin can currently only be downloaded via the [release tab](https://github.com/DerEingerostete/BungeeBan-Plus/releases).<br/>
The latest release can be found [here](https://github.com/DerEingerostete/BungeeBan-Plus/releases/latest).

## License
Distributed under the MPL 2.0 License. See [`LICENSE`](/LICENSE) for more information.

# HHIT Mod

HHIT stands for Hide Held Item Tooltip.

## What it does

This mod hides the item-name tooltip that appears when you hold an item.
It is client-side only.

## Commands

Use the following local commands in chat:

- `/hhit` toggles the setting
- `/hhit on` forces the tooltip hidden
- `/hhit off` forces the tooltip visible
- `/hhit blacklist add` exempts the held item from hiding
- `/hhit blacklist remove` removes its exception
- `/hhit blacklist list` shows saved exceptions

When adding or removing an exception, hold the item in your main hand. An item without a custom name is matched by item type; a custom-named item is matched by its exact custom name. Exceptions keep the held-item popup visible even while hiding is enabled.

The command is case-insensitive.
Your preferences and exceptions are saved in `config/hhitmod.properties`.

## Client safety

The mod only changes your local HUD and chat handling.
It does not send gameplay packets or modify server state.

I cannot guarantee the policy of any specific server or anti-cheat system.

## Supported versions

This repository is currently built and tested against Minecraft `26.1.2`.

## Build

On Windows, build the mod from the project root with:

```powershell
.\gradlew.bat build
```

The compiled jar will be placed in `build/libs`.

## Availability 

- [Modrint](https://modrinth.com/mod/hhit)

## License

This project is distributed under the CC0-1.0 license.

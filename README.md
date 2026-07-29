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

The command is case-insensitive.
Your preference is saved in `config/hhitmod.properties`.

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

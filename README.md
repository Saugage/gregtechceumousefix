# Gregtech MouseTweaks Fix
Fixes the bug when putting items quickly into inventories, some items are put into GT inventories client-side, but are not updated server-side. evil desync...

This is caused by a limit on the amount of items send per tick, which is set to the stack's max size. This is fixed by having PerTickIntCounter always return 0.

## Dependencies:
- [Gregtech CEU](https://github.com/GregTechCEu/GregTech), tested for version GregTech: 2.8.10-beta

Mouse Tweaks is not required, Mouse Tweaks just made the original bug worse, so the fix works with or without it.

## How to run:
- Use `gradlew setupCIWorkspace build` to add gradle
- Use `gradlew build` to build
# Gregtech MouseTweaks Fix
Fixes the bug when using mouse tweaks, holding shift + LMB to put items quickly into inventories, some items are put into GT inventories client-side, but are not updated server-side. evil desync...

This is caused by a limit on the amount of items send per tick, which is set to the stack's max size. This is fixed by having PerTickIntCounter always return 0.

## Dependencies:
Obviously:
- [Gregtech CEU](https://github.com/GregTechCEu/GregTech), tested for version GregTech: 2.8.10-beta
- [Mouse Tweaks Unofficial](https://github.com/CleanroomMC/MouseTweaks), tested for version MouseTweaks 1.12.2-3.1.5
And their dependencies as well

## How to run:
- Use `gradlew setupCIWorkspace build` to add gradle
- Use `gradlew build` to build
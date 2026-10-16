# Gregtech CEu Mouse Fix
Fixes the bug when putting items quickly into inventories, some items are put into GT inventories client-side, but are not updated server-side. evil desync...

This is caused by a limit on the amount of items send per tick, which is set to the stack's max size. This is fixed by having PerTickIntCounter always return 0.

### Quantum chest & tank ghost items fix
When moving items quickly into or out of quantum chests, ghost items would appear on the client. This is fixed by making the input not actually hold anything.

Ghost items would also appear when quickly taking item out of quantum chests and quantum tanks. If done too quickly, an `update()` would occur on the server-side, but would never make it to the client. This is fixed by sending the new contents directly to the client.

## Dependencies:
- [Gregtech CEU](https://github.com/GregTechCEu/GregTech), tested for version GregTech: 2.8.10-beta

i know the project is named gtmousetweaksfix, but Mouse Tweaks is not required, Mouse Tweaks just made the original bug worse. I named the project before looking into the bug, which is why I named it this way lol.

## How to run:
- Use `gradlew setupCIWorkspace build` to add gradle
- Use `gradlew build` to build
# Gregtech CEu Mouse Fix
Fixes the bug when putting items quickly into inventories, some items are put into GT inventories client-side, but are not updated server-side.

This is caused by a limit on the amount of items send per tick, which is set to the stack's max size. This is fixed by redirecting the `PerTickIntCounter.get` call in `transferStackInSlot` to return 0, so the cap is never reached.

## Additional quantum chest & tank ghost items fix
When moving items quickly into or out of quantum chests, ghost items would appear on the client. This is fixed by making the input not actually hold anything.

Ghost items would also appear when quickly taking item out of quantum chests and quantum tanks. If done too quickly, an `update()` would occur on the server-side, but would never make it to the client. This is fixed by sending the new contents directly to the client.

## Dependencies:
- [Gregtech CEu](https://github.com/GregTechCEu/GregTech), tested for version GregTech CEu (1.12.2): 2.8.10-beta

## How to run:
- Use `gradlew setupCIWorkspace build` to add gradle
- Use `gradlew build` to build
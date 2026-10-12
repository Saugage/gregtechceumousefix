# Gregtech MouseTweaks Fix
Fixes the bug when putting items quickly into inventories, some items are put into GT inventories client-side, but are not updated server-side. evil desync...

This is caused by a limit on the amount of items send per tick, which is set to the stack's max size. This is fixed by having PerTickIntCounter always return 0.

### Super chest ghost items fix
The super chest has a single input slot. Items placed in it only moved into the chest's virtual storage during the chest's server-side tick, and the server's container sync could miss the slot being emptied and refilled between two sync passes. The client then kept showing ghost items in the input slot.

This is fixed by making the input slot a pass-through. Items placed into it are inserted into the virtual storage immediately on the server, and discarded on the client, so the slot never holds items that could go out of sync. The stored count is still synced by GregTech itself.

The output slot had the same kind of problem in the other direction: the chest refills it from its virtual storage during its own tick, after the player's container sync, so taking items out very quickly could leave the client with a stale output slot (and a drifting inventory view) until the GUI was reopened. Now, whenever the chest changes its output slot, it is resent directly to every player who has that chest's GUI open. The same happens when one of your own clicks changes the output slot, for example when you put items into an empty chest and they land in the output slot straight away.

There are also similar fixes applied to super tanks.

## Dependencies:
- [Gregtech CEU](https://github.com/GregTechCEu/GregTech), tested for version GregTech: 2.8.10-beta

Mouse Tweaks is not required, Mouse Tweaks just made the original bug worse, so the fix works with or without it.

## How to run:
- Use `gradlew setupCIWorkspace build` to add gradle
- Use `gradlew build` to build
/*
 * Provably Fair Gaming Utilities for RSPS
 * Copyright (c) Roat Pkz - https://roatpkz.com
 *
 * Source & License: https://github.com/roatpkz/rsps-provably-fair
 *
 * This code is open source and may be reused or audited freely under the MIT License.
 */
package server.util.provably_fair;

public enum Flower {
    RED(0, ITEMS.RED_FLOWERS_2462, OBJECTS.FLOWERS_2981),
    BLUE(1, ITEMS.BLUE_FLOWERS_2464, OBJECTS.FLOWERS_2982),
    YELLOW(2, ITEMS.YELLOW_FLOWERS_2466, OBJECTS.FLOWERS_2983),
    PURPLE(3, ITEMS.PURPLE_FLOWERS_2468, OBJECTS.FLOWERS_2984),
    ORANGE(4, ITEMS.ORANGE_FLOWERS_2470, OBJECTS.FLOWERS_2985),
    MIXED(5, ITEMS.MIXED_FLOWERS_2472, OBJECTS.FLOWERS_2986),
    ASSORTED(6, ITEMS.ASSORTED_FLOWERS_2460, OBJECTS.FLOWERS_2980),
    BLACK(7, ITEMS.BLACK_FLOWERS_2476, OBJECTS.FLOWERS_2988),
    WHITE(8, ITEMS.WHITE_FLOWERS_2474, OBJECTS.FLOWERS_2987);
  
    // The constructor
    Flower(int id, int itemId, int objectId) {
        this.id = id;
        this.itemId = itemId;
        this.objectId = objectId;
    }

    // The flower id
    private final int id;
    public int getId() { return this.id; }

    // The flower object id
    private final int objectId;
    public int getObjectId() { return this.objectId; }

    // The flower item id
    private final int itemId;
    public int getItemId() { return this.itemId; }
}

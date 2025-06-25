/*
 * Provably Fair Gaming Utilities for RSPS
 * Copyright (c) Roat Pkz - https://roatpkz.com
 *
 * Source & License: https://github.com/roatpkz/rsps-provably-fair
 *
 * This code is open source and may be reused or audited freely under the MIT License.
 */

package server.util.provably_fair.seeds;

import server.util.provably_fair.ProvablyFair;

public class ServerSeed extends GamblingSeed {

    public ServerSeed(String presetSeed) {
        super(presetSeed);
    }

    public ServerSeed(String presetSeed, int nonce) {
        super(presetSeed);
        this.nonce = nonce;
    }

    private boolean hasBeenRevealed = false;
    public void setHasBeenRevealed(boolean hasBeenRevealed) {
        this.hasBeenRevealed = hasBeenRevealed;
    }
    public boolean hasBeenRevealed() {
        return this.hasBeenRevealed;
    }

    private String hashedSeed;
    public String getHashedSeed() {
        return this.hashedSeed;
    }
    public String getHashedSeedVisual() {
        if (hashedSeed == null || hashedSeed.length() <= 8) {
            return hashedSeed; // nothing to shorten
        }
        return hashedSeed.substring(0, 5) + "..." // or "..." if you prefer
                + hashedSeed.substring(hashedSeed.length() - 5);
    }

    private int nonce = 0;
    public int getNonce() {
        return this.nonce;
    }
    public void setNonce(int nonce) {
        this.nonce = nonce;
    }
    public int incrementNonce() {
        return this.nonce++;
    }

    @Override
    public void setSeed(String newSeed) {
        super.setSeed(newSeed);
        this.hashedSeed = ProvablyFair.sha256Hex(newSeed);
    }
}

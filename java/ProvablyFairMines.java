/*
 * Provably Fair Gaming Utilities for RSPS
 * Copyright (c) Roat Pkz - https://roatpkz.com
 *
 * Source & License: https://github.com/roatpkz/rsps-provably-fair
 *
 * This code is open source and may be reused or audited freely under the MIT License.
 */

package server.util.provably_fair;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;

/**
 * ProvablyFairMines generates a deterministic minefield layout for a 5x5 Mines game
 * using a provably fair system based on clientSeed, serverSeed, nonce, and counter.
 *
 * The algorithm ensures the layout is verifiable and tamper-proof by using SHA-256
 * hashing to randomly and deterministically select mine positions.
 */
public class ProvablyFairMines {

    private static final int SLOTS = 25; // 5 × 5 fixed grid

    /**
     * Deterministically generates a minefield layout with exactly {@code mineCount} mines
     * placed across a 5×5 grid using a provably fair method.
     *
     * The positions are derived from a SHA-256 hash of the concatenated values:
     * {@code clientSeed : serverSeed : nonce : counter}, ensuring transparency and fairness.
     *
     * @param clientSeed user-defined seed to add variability
     * @param serverSeed server-provided secret seed, revealed post-game
     * @param nonce      unique number per game instance to prevent reuse
     * @param mineCount  number of mines to place (1 to 24)
     * @return BitSet of size 25 where exactly {@code mineCount} bits are set to true
     */
    public static BitSet generateLayout(String clientSeed,  String serverSeed, int nonce, int mineCount) {

        if (mineCount < 1 || mineCount >= SLOTS) {
            throw new IllegalArgumentException("mineCount must be 1-24");
        }

        // Initialize the draw-bag with all 25 possible cell indices (0 to 24)
        int[] bag = new int[SLOTS];
        for (int i = 0; i < SLOTS; i++) bag[i] = i;

        BitSet mines = new BitSet(SLOTS);

        // Create a reusable SHA-256 MessageDigest instance
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException impossible) {
            throw new AssertionError("SHA-256 not available", impossible);
        }

        int remaining = SLOTS;
        long ctr      = 0;

        while (mineCount-- > 0) {

            // Generate SHA-256 hash from clientSeed:serverSeed:nonce:ctr and use first 8 bytes as a 64-bit unsigned value
            byte[] h   = sha256.digest(
                    (clientSeed + ':' + serverSeed + ':' + nonce + ':' + ctr++)
                            .getBytes(StandardCharsets.UTF_8));

            long rand = 0;
            for (int i = 0; i < 8; i++) {
                rand = (rand << 8) | (h[i] & 0xFFL);
            }

            // Convert to unsigned remainder to select a valid index within remaining slots
            int idx  = (int) Long.remainderUnsigned(rand, remaining);
            int cell = bag[idx];

            // mark the chosen slot as a mine
            mines.set(cell);

            // Remove the selected cell by swapping it with the last unchosen element (swap-with-last idiom)
            bag[idx] = bag[--remaining];
        }
        return mines;
    }

}

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

/**
 * Provides a provably fair path for a Plinko ball using SHA-256 hashing.
 *
 * Algorithm:
 * <ul>
 *     <li>Material: {@code clientSeed + ":" + serverSeed + ":" + nonce}</li>
 *     <li>Hash: {@code SHA-256(material)} → 32 bytes</li>
 *     <li>Row r (0 is the first row of pegs) reads bit r of the hash, most significant bit of the first byte first</li>
 *     <li>1 = the ball goes right, 0 = it goes left</li>
 *     <li>Slot: the number of rights, 0 (far left) to rows (far right)</li>
 * </ul>
 *
 * Every bit of a SHA-256 hash is a fair coin, so slot k of n rows has a chance of exactly C(n, k) / 2^n.
 * The first four hex characters of the hash give all 16 decisions: e12718c0... is RRRLLLLR LLRLLRRR.
 */
public class ProvablyFairPlinko {

    // The board sizes a ball can be dropped on
    public static final int MIN_ROWS = 8, MAX_ROWS = 16;

    /**
     * Returns the ball's path as bits: bit r is the decision at row r, 1 = right.
     *
     * @param clientSeed user-defined seed
     * @param serverSeed server-defined secret seed
     * @param nonce      unique nonce per ball
     * @param rows       rows of pegs on the board, 8 to 16
     * @return the path, for example 0xE487 for RRRLLLLRLLRLLRRR
     */
    public static int pathBits(String clientSeed, String serverSeed, int nonce, int rows) {
        if(rows < MIN_ROWS || rows > MAX_ROWS) {
            throw new IllegalArgumentException("Rows must be between " + MIN_ROWS + " and " + MAX_ROWS + ".");
        }

        // Instantiate SHA-256 hasher for deterministic entropy
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException impossible) {
            throw new AssertionError("SHA-256 not available", impossible);
        }

        byte[] hash = sha256.digest((clientSeed + ':' + serverSeed + ':' + nonce)
                .getBytes(StandardCharsets.UTF_8));

        // One hash bit per row, read from the most significant bit of the first byte onwards
        int pathBits = 0;
        for(int row = 0; row < rows; row++) {
            int hashBit = ((hash[row / 8] & 0xFF) >>> (7 - (row % 8))) & 1;
            pathBits |= hashBit << row;
        }
        return pathBits;
    }

    /**
     * Returns the slot a path ends in: the number of times the ball went right.
     *
     * @param pathBits the path from {@link #pathBits}
     * @return slot 0 (far left) to rows (far right)
     */
    public static int slot(int pathBits) {
        return Integer.bitCount(pathBits);
    }

}

/*
 * Provably Fair Gaming Utilities for RSPS
 * Copyright (c) Roat Pkz - https://roatpkz.com
 *
 * Source & License: https://github.com/roatpkz/rsps-provably-fair
 *
 * This code is open source and may be reused or audited freely under the MIT License.
 */

package server.util.provably_fair;

import server.util.provably_fair.Flower;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * ============================================================================
 *  FLOWER-POKER – PROVABLY-FAIR DEALER
 * ============================================================================
 *
 *  • Each match is frozen in advance by three secrets
 *
 *        material = p1Seed + ':' + p2Seed + ':' + serverSeed
 *        hash₀    = SHA-256(material)
 *        hash₁    = SHA-256(material + ":1")
 *        hash₂    = SHA-256(material + ":2")   …and so on
 *
 *  • From every hash we read the first 8 bytes (64-bit unsigned int)
 *    and turn that into a colour with the classic 1 / 500 rare-rate:
 *
 *        1   → BLACK
 *        2   → WHITE
 *        3-500 (498 numbers) → one of the seven regular colours
 *
 *  • No nonce is needed: the <i>server seed</i> is revealed
 *    right after the match, so every hand can be replayed offline.
 *    a hashed version of the server seed must be provided to the players
 *    before the match starts, so they can verify the game. It is essential
 *    that players are allowed to change their client seeds while the server
 *    seed remains constant, providing a fair and transparent gaming experience.
 *
 *  Anyone with the three seeds can reproduce the exact row and confirm
 *  that the game server never swapped, reordered, or cherry-picked flowers.
 */
public class ProvablyFairFlowerPoker {

    /* ───────────────────────────────────────────────────────────────────── */
    /*  PUBLIC API – single flowers                                          */
    /* ───────────────────────────────────────────────────────────────────── */

    /**
     * Absolute draw <code>index</code> (0-based) → flower colour.
     *
     * <p>Index 0 is the very first flower dealt, index 1 the second, …</p>
     *
     * @throws IllegalArgumentException if {@code index} is negative
     */
    public static Flower flowerAt(String p1Seed, String p2Seed, String serverSeed, int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be ≥ 0");
        }

        long rnd64 = nextRand64(p1Seed, p2Seed, serverSeed, index);
        int  roll  = (int) (rnd64 % 500) + 1;

        if (roll == 1) { return Flower.BLACK; }
        if (roll == 2) { return Flower.WHITE; }
        return COMMON_COLOURS[(roll - 3) % COMMON_COLOURS.length];
    }

    /* ---------------------------------------------------------------------- */

    /**
     * <i>k</i><sup>th</sup> flower dealt to <strong>Player 1</strong>.
     * <p>{@code k = 0} returns the very first flower of the match,
     * {@code k = 1} the third one, and so on.</p>
     */
    public static Flower flowerAtPlayer1(String p1Seed, String p2Seed, String serverSeed, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be ≥ 0");
        }
        return flowerAt(p1Seed, p2Seed, serverSeed, k * 2);
    }

    /**
     * <i>k</i><sup>th</sup> flower dealt to <strong>Player 2</strong>.
     * <p>{@code k = 0} returns the second flower of the match,
     * {@code k = 1} the fourth one, and so on.</p>
     */
    public static Flower flowerAtPlayer2(String p1Seed, String p2Seed, String serverSeed, int k) {
        if (k < 0) {
            throw new IllegalArgumentException("k must be ≥ 0");
        }
        return flowerAt(p1Seed, p2Seed, serverSeed, k * 2 + 1);
    }

    /**
     * First <code>n</code> flowers dealt to Player 1.
     *
     * @param n amount of flowers to return (must be &gt; 0)
     * @return array of size <code>n</code>
     */
    public static Flower[] firstNPlayer1(String p1Seed, String p2Seed, String serverSeed, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be ≥ 1");
        }
        Flower[] result = new Flower[n];
        for (int i = 0; i < n; i++) {
            result[i] = flowerAtPlayer1(p1Seed, p2Seed, serverSeed, i);
        }
        return result;
    }

    /**
     * First <code>n</code> flowers dealt to Player 2.
     *
     * @param n amount of flowers to return (must be &gt; 0)
     * @return array of size <code>n</code>
     */
    public static Flower[] firstNPlayer2(String p1Seed, String p2Seed, String serverSeed, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be ≥ 1");
        }
        Flower[] result = new Flower[n];
        for (int i = 0; i < n; i++) {
            result[i] = flowerAtPlayer2(p1Seed, p2Seed, serverSeed, i);
        }
        return result;
    }

    /* ─────────────────────  internals  ───────────────────── */

    /** Converts the first 8 bytes of SHA-256(material) into a positive UInt64. */
    private static long nextRand64(String p1Seed, String p2Seed, String serverSeed, int counter) {

        // Create SHA-256 digest instance (reused for each shuffle step)
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }

        byte[] dig = sha256.digest((p1Seed + ':' + p2Seed + ':' + serverSeed + ':' + counter).getBytes(StandardCharsets.UTF_8));

        long v = 0;
        for (int i = 0; i < 8; i++) {
            v = (v << 8) | (dig[i] & 0xFFL);
        }
        return v & 0x7FFFFFFFFFFFFFFFL; // keep it positive
    }

    /* Seven standard colours for rolls 3 … 500 (498 numbers). */
    private static final Flower[] COMMON_COLOURS = {
            Flower.RED,
            Flower.BLUE,
            Flower.YELLOW,
            Flower.PURPLE,
            Flower.ORANGE,
            Flower.MIXED,
            Flower.ASSORTED
    };

}

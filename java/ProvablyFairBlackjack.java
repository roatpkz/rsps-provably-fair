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
import java.util.Collections;
import java.util.List;

/**
 * Provides a provably fair shuffle mechanism for Blackjack or any card-based game.
 *
 * The shuffling algorithm uses SHA-256 hashing in combination with a client seed,
 * server seed, and nonce to produce a deterministic yet tamper-proof permutation
 * of the given deck. The result is verifiable by players post-game, ensuring fairness.
 *
 * The method implements a cryptographically secure version of the Fisher-Yates shuffle.
 */
public class ProvablyFairBlackjack {

    /**
     * Deterministically shuffles the provided deck using a provably fair method based
     * on the combination of {@code clientSeed}, {@code serverSeed}, and {@code nonce}.
     *
     * The method applies a cryptographically seeded Fisher-Yates shuffle, using the
     * SHA-256 digest of the concatenated string {@code clientSeed:serverSeed:nonce:counter}
     * to derive pseudo-random swap positions.
     *
     * The shuffled result is reproducible and tamper-proof, allowing users to verify
     * that the same inputs always result in the same card order.
     *
     * @param deck       a mutable List of cards to shuffle (modified in-place)
     * @param clientSeed a user-provided seed (public)
     * @param serverSeed a server-provided secret seed (revealed post-game)
     * @param nonce      an integer that uniquely identifies this round or session
     */
    public static void shuffle(List<?> deck, String clientSeed, String serverSeed, long nonce) {

        // Combine seeds and nonce into base string for per-swap entropy
        String base = clientSeed + ':' + serverSeed + ':' + nonce + ':';

        // Create SHA-256 digest instance (reused for each shuffle step)
        MessageDigest sha256;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }

        // Perform Fisher-Yates shuffle, walking backward through the deck
        for (int i = deck.size() - 1, ctr = 0; i > 0; i--, ctr++) {

            // Hash the base string with current counter to generate deterministic entropy
            byte[] hash = sha256.digest((base + ctr).getBytes(StandardCharsets.UTF_8));
            long rand = 0L;

            // Convert first 8 bytes of the hash to an unsigned 64-bit value
            for (int b = 0; b < 8; b++) {
                rand = (rand << 8) | (hash[b] & 0xFFL);
            }

            // Get unbiased swap index in the range [0, i] using unsigned modulo
            int j = (int) Long.remainderUnsigned(rand, i + 1);
            Collections.swap(deck, i, j);

            // Reset SHA-256 digest to prepare for next iteration
            sha256.reset();
        }
    }

}

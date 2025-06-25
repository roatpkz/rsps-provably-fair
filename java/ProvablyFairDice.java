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
 * Provides a provably fair percentage-based dice roll generator using SHA-256 hashing.
 *
 * Algorithm:
 * <ul>
 *     <li>Material: {@code clientSeed + ":" + serverSeed + ":" + nonce}</li>
 *     <li>Hash: {@code SHA-256(material)} → 32 bytes</li>
 *     <li>Value: First 4 bytes as a 32-bit unsigned integer</li>
 *     <li>Scaled: {@code value % 10_001} → range 0 to 10,000</li>
 *     <li>Result: {@code scaled / 100.0} → final value between 0.00 and 100.00</li>
 * </ul>
 *
 * This ensures deterministic yet verifiable results for every round using user input
 * (client seed), a secret (server seed), and a round identifier (nonce).
 *
 * The output is a double, but can also be formatted to exact precision with a {@code BigDecimal}.
 */
public class ProvablyFairDice {

    /**
     * Returns the final dice roll as a percentage between 0.00 and 100.00.
     *
     * @param clientSeed user-defined seed
     * @param serverSeed server-defined secret seed
     * @param nonce      unique nonce per game or round
     * @return percentage roll (e.g. 68.26)
     */
    public static double rollPercentage(String clientSeed, String serverSeed, int nonce) {
        return computeScaled(clientSeed, serverSeed, nonce) / 100.0;
    }

    /**
     * Computes a deterministic pseudo-random integer between 0 and 10,000 (inclusive)
     * using SHA-256 hashing of the combined input: client seed, server seed, and nonce.
     *
     * This ensures game fairness and reproducibility of outcomes in gambling systems.
     * The method extracts the first 4 bytes of the SHA-256 hash, interprets them as an
     * unsigned 32-bit integer, and returns its value modulo 10,001.
     *
     * @param clientSeed a user-defined seed set before the game
     * @param serverSeed a server-generated seed revealed post-game
     * @param nonce      a counter to ensure unique values per roll
     * @return integer in range 0–10,000 inclusive
     */
    private static int computeScaled(String clientSeed, String serverSeed, int nonce) {

        // Instantiate SHA-256 hasher for deterministic entropy
        MessageDigest sha256 = null;
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException impossible) {
            throw new AssertionError("SHA-256 not available", impossible);
        }

        byte[] hash = sha256.digest((clientSeed + ':' + serverSeed + ':' + nonce)
                .getBytes(StandardCharsets.UTF_8));

        long unsigned = ((hash[0] & 0xFFL) << 24)
                | ((hash[1] & 0xFFL) << 16)
                | ((hash[2] & 0xFFL) <<  8)
                |  (hash[3] & 0xFFL); // Convert first 4 bytes of hash to unsigned 32-bit integer (range: 0 to 4,294,967,295)

        return (int) (unsigned % 10_001); // 0 ... 10 000
    }

}

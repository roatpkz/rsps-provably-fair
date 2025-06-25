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
import java.security.SecureRandom;
import java.util.BitSet;

/**
 * General-purpose utilities for implementing provably fair mechanics in RSPS (Runescape Private Servers).
 *
 * Provides:
 * - Cryptographically secure random seed generation
 * - SHA-256 hashing utility (hex format)
 * - Seed format validation (alphanumeric only)
 *
 * Designed for integration into client-server systems where provable fairness is required.
 */

public class ProvablyFair {

    // Thread-safe cryptographically secure RNG for seed generation
    private static final SecureRandom RNG = new SecureRandom();

    // Allowed characters for generated seeds (62 alphanumeric + hyphen)
    private static final char[] ALPHANUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-".toCharArray();

    // Default length for generated seeds
    private static final int SEED_LEN = 16;

    /**
     * Computes the SHA-256 hash of a string and returns it as a lowercase hexadecimal string.
     * Commonly used to hash server seeds for public verification (proof of fairness).
     *
     * @param s input string (e.g. server seed)
     * @return SHA-256 digest in lowercase hex format
     */
    public static String sha256Hex(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(s.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Generates a random alphanumeric seed of fixed length (16 characters).
     * Uses a secure RNG and allowed characters: a-z, A-Z, 0-9, and '-'.
     *
     * @return cryptographically secure seed string
     */
    public static String randomSeed() {
        char[] buf = new char[SEED_LEN];
        for (int i = 0; i < SEED_LEN; i++) {
            buf[i] = ALPHANUM[RNG.nextInt(ALPHANUM.length)];
        }
        return new String(buf);
    }

    // BitSet used to quickly validate that all characters in a seed are allowed
    private static final BitSet ALLOWED = new BitSet(128);
    static {
        for (char c : ALPHANUM) {
            ALLOWED.set(c);
        }
    }

    /**
     * Validates whether a given seed string is non-null, non-empty, and consists only of allowed characters.
     *
     * Allowed characters: a-z, A-Z, 0-9, and hyphen (-)
     *
     * @param seed the seed string to validate
     * @return true if the seed is valid, false otherwise
     */
    public static boolean isSeedValid(String seed) {
        if (seed == null || seed.isEmpty()) return false;
        for (int i = 0, n = seed.length(); i < n; i++) {
            char c = seed.charAt(i);
            if (c >= ALLOWED.size() || !ALLOWED.get(c)) return false;
        }
        return true;
    }

}

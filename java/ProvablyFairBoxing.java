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
 * =============================================================================
 *  BOXING – PROVABLY‑FAIR HIT GENERATOR
 * =============================================================================
 *
 *  • Every punch outcome is locked in by three secrets:
 *
 *        material = player1Seed + ':' + player2Seed + ':' + serverSeed
 *        hash₀    = SHA‑256(material + ":0")   → first hit (hitNo0)
 *        hash₁    = SHA‑256(material + ":1")   → second hit (hitNo1)
 *        hash₂    = SHA‑256(material + ":2")   ...and so on
 *
 *  • From each hash we read the first8 bytes (64‑bit unsigned int)
 *    and map it to a number between 0and16 inclusive:
 *
 *        hitValue = (unsigned64 % 17)
 *
 *  • Global hit numbering alternates between players
 *        hitNo0 → player1, turn0
 *        hitNo1 → player2, turn0
 *        hitNo2 → player1, turn1 ... and so on
 *
 *  • Before the bout starts we publish SHA‑256(serverSeed)
 *    so we cannot swap the secret mid‑fight. The plain
 *    serverSeed is revealed afterwards for full verification.
 *
 *  Anyone with the three seeds can reproduce all hits offline
 *  and prove the game server never altered the outcomes.
 */
public class ProvablyFairBoxing {

    /**
     * The maximum hit value.
     */
    public static final int MAX_HIT_VALUE = 16; // 0 to 16 inclusive

    /**
     * Deterministically returns a value 0‒16 inclusive.
     * @param p1Seed   player1's client seed
     * @param p2Seed   player2's client seed
     * @param serverSeed hidden server seed (revealed later)
     * @param hitNo    0‑based global hit counter (0,1,2...)
     */
    public static int hit(String p1Seed, String p2Seed, String serverSeed, long hitNo) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] hash = sha256.digest((p1Seed + ':' + p2Seed + ':' + serverSeed + ':' + hitNo)
                    .getBytes(StandardCharsets.UTF_8));
            long rand = 0L;
            for (int i = 0; i < 8; i++) {
                rand = (rand << 8) | (hash[i] & 0xFFL);
            }
            return (int) java.lang.Long.remainderUnsigned(rand, (MAX_HIT_VALUE + 1)); // 0‑16
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }

    /**
     * Convenience wrapper: maps (player index 1|2, turn 0,1,2...)
     * to the correct global hitNo and returns the same 0‑16 value.
     */
    public static int hitForPlayer(int playerIndex1or2,
                                   int turn,
                                   String p1Seed,
                                   String p2Seed,
                                   String serverSeed) {
        long hitNo = (long) turn * 2 + (playerIndex1or2 == 1 ? 0 : 1);
        return hit(p1Seed, p2Seed, serverSeed, hitNo);
    }

}

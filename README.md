
# RSPS Provably Fair Gaming

A lightweight, **MIT‑licensed** collection of Java helpers that add _provably‑fair_ game logic to RuneScape Private Servers (RSPS).  
Every outcome is locked in by cryptographic hashes so that **anyone** holding the relevant seeds can reproduce the exact same result offline and confirm nothing was tampered with.

```
material = clientSeed + ":" + serverSeed [+ ":" + nonce]
hash     = SHA‑256(material)
```

The hash (or a slice of it) is turned into an unsigned integer and mapped to a card, roll, mine, or flower according to the game‑specific rules below.

> **Quick adoption:** The heavy lifting here is already done, just import the helpers, implement them to your wager logic, and you’re provably fair. In practice, most RSPS owners can ship a verifiable Flower-Poker / Dice / Mines / Blackjack release in a single afternoon with this release, giving players cryptographic proof that every deal, roll, or plant is 100% tamper-free.
---

## ✨ What’s inside?

| Class | Game | RNG Range | Seeds Involved | Online Verifier |
|-------|------|-----------|----------------|-----------------|
| `ProvablyFairBlackjack` | 8-deck Blackjack shoe | 1 … 416 cards | `clientSeed`, `serverSeed`, **`nonce`** | [blackjack.html](https://cdn.roatpkz.com/provably_fair/blackjack.html) |
| `ProvablyFairDice` | 0.00 – 100 % roll | 0 … 10 000 → 0.00 – 100.00 % | `clientSeed`, `serverSeed`, **`nonce`** | [dice.html](https://cdn.roatpkz.com/provably_fair/dice.html) |
| `ProvablyFairMines` | 5 × 5 Mines grid | BitSet (25) | `clientSeed`, `serverSeed`, **`nonce`**, `mineCount` | [mines.html](https://cdn.roatpkz.com/provably_fair/mines.html) |
| `ProvablyFairFlowerPoker` | Flower Poker | Infinite flower stream | `player1Seed`, `player2Seed`, `serverSeed` | [flowerpoker.html](https://cdn.roatpkz.com/provably_fair/flowerpoker.html) |
| `ProvablyFairBoxing` | Boxing | Infinite 0-16 hit stream | `player1Seed`, `player2Seed`, `serverSeed`, **`hit #`** | [boxing.html](https://cdn.roatpkz.com/provably_fair/boxing.html) |

*All classes live in `server.util.provably_fair` and have no third‑party dependencies.*

---

## 🚀 Quick Start

```bash
# 1. copy the src folder into your server project
# 2. import the classes you need
import server.util.provably_fair.ProvablyFairDice;
```

---

## 🛠️ Code Examples

### Blackjack – shuffle a fresh 8‑deck shoe
```java
String clientSeed = "alice44434434"; // set by the user
String serverSeed = "f3d6..."; // revealed after the session, hashed version must be visible to player before the round starts
int    hand   = 17;          // nonce = hand #
int[] shoe = ProvablyFairBlackjack.shuffleShoe(clientSeed, serverSeed, hand);

/* first ten cards */
System.out.println(Arrays.toString(Arrays.copyOf(shoe, 10)));
```

### Dice – percentage roll (0.00 – 100.00 %)
```java
double pct = ProvablyFairDice.rollPercentage("client_seed", "server_seed", 42);
System.out.printf("You rolled %.2f%%", pct);
```

### Mines – place 10 mines on a 5×5 board
```java
BitSet layout = ProvablyFairMines.generateLayout(
        "alice", "f3d6...", 1234, 10);   // 10 = mineCount

System.out.println(layout);              // bit = true → mine
```

### Flower Poker – reproduce an entire match
```java
String p1 = "aliceSeed";
String p2 = "bobSeed";
String srv = "f3d6...";

Flower firstFlowerP1 = ProvablyFairFlowerPoker.flowerAtPlayer1(p1, p2, srv, 0);
Flower[] firstHandP2 = ProvablyFairFlowerPoker.firstNPlayer2(p1, p2, srv, 5);
```

### Boxing – deal damage from 0-16
```java
String p1 = "smoothieSeed";
String p2 = "maxSeed";
String srv = "jgr94...";

int player1Hit = hitForPlayer(1, 0, p1, p2, srv);
int player2Hit = hitForPlayer(2, 0, p1, p2, srv);
```

---

## 🔧 Provably-Fair Flow — Server-Owner Checklist

1. **Pre-deal : publish a hash, *then* freeze player seeds**  
   - Generate a fresh `server_seed_plain`.  
   - Show **only** `SHA-256(server_seed_plain)` to the player(s) before the game begins.  
   - Let players enter / edit their own `client_seed`(s) **after** seeing that hash.  
   - Accept the wager → lock all seeds.

2. **Post-deal : disclose the raw server seed for audit**  
   - When the hand ends, reveal `server_seed_plain`.  
   - Immediately rotate to a brand-new `server_seed_plain` and publish its hash for the next round.
   - If it's not player vs player (Flower Poker), then increase the nonce after every game.

3. **Self-audit guide for players**  
   - Copy `client_seed(s)` + `server_seed_plain` + `nonce` from the game log.  
   - Paste them into an open-source verifier (or run the matching Java class).  
   - Confirm the replayed result matches what was shown in-game—any mismatch would prove tampering.

---

## 🏗️ How It Works (technical)

* **Determinism** – SHA‑256 is a one‑way function; equal input → equal output.
* **Unpredictability** – the server keeps its seed secret until the round ends.
* **Client Control** – players can change their seed at any time before wagering.
* **Transparency** – the server publishes `SHA‑256(serverSeed)` **before** play so it cannot swap seeds to force an outcome.
* **Nonces** – used when the same two seeds produce multiple rounds (e.g. successive Blackjack hands).

---
## 📝 List of RuneScape Private Servers with Provably Fair Gambling

- **Roat Pkz [Added on July 1, 2025]** – [https://roatpkz.com](https://roatpkz.com)

---

If you’ve implemented provably fair gambling on your server, please email **admin@roatpkz.com** with details so we can add your server to this list.

## 🪪 License

```
MIT License – © Roat Pkz  https://roatpkz.com
```

Pull requests and audits welcome!

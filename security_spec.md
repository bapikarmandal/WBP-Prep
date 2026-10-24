# Security Specification (`security_spec.md`)

## 1. Data Invariants
1. **Strict Ownership Isolation (`/users/{userId}`, `/questions/{questionId}`, `/test_attempts/{attemptId}`)**:
   - Every document in `/users/{userId}` MUST have `userId == request.auth.uid` and path parameter `userId == request.auth.uid`.
   - Every document in `/questions/{questionId}` and `/test_attempts/{attemptId}` MUST have `userId == request.auth.uid` on creation and can only be read or queried when `resource.data.userId == request.auth.uid` (or `isAdmin()`).
2. **Temporal Integrity (`createdAt`, `updatedAt`)**:
   - All creation timestamps MUST be server timestamps (`is timestamp && <= request.time`).
   - `createdAt` and `userId` are strictly immutable on all updates (`incoming().createdAt == existing().createdAt && incoming().userId == existing().userId`).
3. **Append-Only Test Attempts (`/test_attempts/{attemptId}`)**:
   - Once a mock test attempt is recorded, it is immutable (`allow update: if false;`).
4. **Schema & Bounds Enforcement**:
   - All document IDs are validated with `isValidId(id)` (`^[a-zA-Z0-9_\-]+$`, max 128 chars).
   - Every string field has explicit `.size()` bounds and enum allowlists where applicable.

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Scrape**: `unauthDb.collection("questions").get()` -> Rejected (`request.auth != null`).
2. **Identity Spoofing on User Create**: `aliceDb.collection("users").doc("alice_123").set({ userId: "bob_456", ... })` -> Rejected (`incoming().userId == request.auth.uid`).
3. **Cross-User Profile PII Read**: `aliceDb.collection("users").doc("bob_456").get()` -> Rejected (`request.auth.uid == userId`).
4. **Shadow Field Injection on Profile Update**: `aliceDb.collection("users").doc("alice_123").update({ isAdmin: true })` -> Rejected (`affectedKeys().hasOnly(...)`).
5. **Immutable `createdAt` Tampering**: `aliceDb.collection("users").doc("alice_123").update({ createdAt: newTimestamp })` -> Rejected (`incoming().createdAt == existing().createdAt`).
6. **Invalid Enum Value Poisoning**: `aliceDb.collection("users").doc("alice_123").set({ casteCategory: "SUPER_ADMIN", ... })` -> Rejected (`isValidAspirantUser`).
7. **Unfiltered Collection List Query**: `aliceDb.collection("questions").get()` without `.where("userId", "==", "alice_123")` -> Rejected (`resource.data.userId == request.auth.uid`).
8. **Cross-User Question Update**: `bobDb.collection("questions").doc("alice_q1").update({ correctIndex: 2 })` -> Rejected (`existing().userId == request.auth.uid`).
9. **Out-of-Bounds Option Index**: `aliceDb.collection("questions").doc("q1").set({ correctIndex: 9, ... })` -> Rejected (`correctIndex >= 0 && correctIndex <= 3`).
10. **Malformed Options Array Size**: `aliceDb.collection("questions").doc("q1").set({ optionsBn: ["only_one"], ... })` -> Rejected (`optionsBn.size() == 4`).
11. **Test Attempt Score Tampering (Update)**: `aliceDb.collection("test_attempts").doc("att_1").update({ scoreObtained: 85 })` -> Rejected (`allow update: if false`).
12. **Oversized String Resource Exhaustion**: `aliceDb.collection("questions").doc("q1").set({ questionBn: "A".repeat(5000), ... })` -> Rejected (`questionBn.size() <= 2000`).

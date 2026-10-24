const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

function validTimestamp() {
  return new Date(Date.now() - 1000);
}

// --- SECURITY BOUNDS TESTS (Dirty Dozen) ---

test("Unauthenticated user: cannot read users, questions, or test_attempts", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("questions").get());
  await assertFails(unauthDb.collection("test_attempts").get());
});

test("Authenticated user: can create and read their own aspirant profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = validTimestamp();
  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Rahul Mondal",
      email: "rahul@wbpprep.com",
      casteCategory: "OBC_B",
      targetExam: "WBP_CONSTABLE_85",
      testsAttempted: 0,
      totalScoreAccumulated: 0,
      createdAt: ts,
      updatedAt: ts,
    })
  );
  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
});

test("Authenticated user: cannot read another user's profile (PII isolation)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const ts = validTimestamp();

  await assertSucceeds(
    bobDb.collection("users").doc(BOB_UID).set({
      userId: BOB_UID,
      fullName: "Bob Das",
      email: "bob@wbpprep.com",
      casteCategory: "UR",
      targetExam: "KP_CONSTABLE_85",
      testsAttempted: 1,
      totalScoreAccumulated: 55,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertFails(aliceDb.collection("users").doc(BOB_UID).get());
});

test("Authenticated user: cannot inject shadow fields on profile update", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = validTimestamp();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      fullName: "Rahul Mondal",
      email: "rahul@wbpprep.com",
      casteCategory: "OBC_B",
      targetExam: "WBP_CONSTABLE_85",
      testsAttempted: 0,
      totalScoreAccumulated: 0,
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      isAdmin: true,
      updatedAt: validTimestamp(),
    })
  );
});

test("Authenticated user: can create, query, and update their own VaultQuestion", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = validTimestamp();

  await assertSucceeds(
    aliceDb.collection("questions").doc("q_101").set({
      userId: ALICE_UID,
      subject: "GK",
      chapter: "West Bengal Static GK",
      isPyq: true,
      pyqExam: "WBP Constable Main",
      pyqYear: "2023",
      questionBn: "পশ্চিমবঙ্গের দীর্ঘতম নদী কোনটি?",
      optionsBn: ["ভাগীরথী-হুগলি", "দামোদর", "তিস্তা", "সুবর্ণরেখা"],
      correctIndex: 0,
      explanationBn: "ভাগীরথী-হুগলি পশ্চিমবঙ্গের দীর্ঘতম নদী।",
      createdAt: ts,
      updatedAt: ts,
    })
  );

  await assertSucceeds(
    aliceDb.collection("questions").where("userId", "==", ALICE_UID).get()
  );

  await assertSucceeds(
    aliceDb.collection("questions").doc("q_101").update({
      explanationBn: "ভাগীরথী-হুগলি নদীটি পশ্চিমবঙ্গের দীর্ঘতম নদী।",
      updatedAt: validTimestamp(),
    })
  );
});

test("Authenticated user: fails question list query without userId filter", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("questions").get());
});

test("Authenticated user: can create test_attempt but cannot update it", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const ts = validTimestamp();

  await assertSucceeds(
    aliceDb.collection("test_attempts").doc("att_1").set({
      userId: ALICE_UID,
      testTitle: "WBP Full Mock #1",
      scoreObtained: 62.5,
      maxMarks: 85.0,
      accuracyPercent: 84,
      createdAt: ts,
    })
  );

  await assertFails(
    aliceDb.collection("test_attempts").doc("att_1").update({
      scoreObtained: 85.0,
    })
  );
});

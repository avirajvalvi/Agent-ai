const { initializeTestEnvironment, assertFails, assertSucceeds } = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

const PROJECT_ID = "demo-no-project";
let testEnv;

before(async () => {
  const rules = fs.readFileSync("firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: rules,
      host: "127.0.0.1",
      port: 8085
    }
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

test("Unauthenticated user cannot read action_logs", async () => {
  const unauthContext = testEnv.unauthenticatedContext();
  const db = unauthContext.firestore();
  await assertFails(db.collection("users").doc("alice").collection("action_logs").get());
});

test("Authenticated owner can read and write own action_logs", async () => {
  const aliceContext = testEnv.authenticatedContext("alice");
  const db = aliceContext.firestore();
  const docRef = db.collection("users").doc("alice").collection("action_logs").doc("log_1");
  
  await assertSucceeds(docRef.set({
    id: "log_1",
    userId: "alice",
    stepId: "step_1",
    toolName: "Nmap Scanner",
    description: "Scan port 443",
    status: "SUCCESS",
    timestamp: new Date(),
    createdAt: new Date(),
    updatedAt: new Date()
  }));

  await assertSucceeds(docRef.get());
});

test("Alice cannot read Bob's action_logs", async () => {
  const aliceContext = testEnv.authenticatedContext("alice");
  const db = aliceContext.firestore();
  await assertFails(db.collection("users").doc("bob").collection("action_logs").get());
});

test("Invalid status in action log is rejected", async () => {
  const aliceContext = testEnv.authenticatedContext("alice");
  const db = aliceContext.firestore();
  const docRef = db.collection("users").doc("alice").collection("action_logs").doc("log_bad");
  
  await assertFails(docRef.set({
    id: "log_bad",
    userId: "alice",
    stepId: "step_1",
    toolName: "Nmap Scanner",
    description: "Scan",
    status: "INVALID_STATUS",
    timestamp: new Date(),
    createdAt: new Date(),
    updatedAt: new Date()
  }));
});

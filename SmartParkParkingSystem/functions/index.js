const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Cloud Function to delete user from Authentication and Realtime Database
exports.deleteUserAccount = functions.https.onCall(async (data, context) => {
  const uid = data.uid; // The user's UID passed from Android app
  if (!uid) {
    throw new functions.https.HttpsError("invalid-argument", "UID is required.");
  }

  try {
    // Delete from Authentication
    await admin.auth().deleteUser(uid);

    // Delete from Realtime Database
    await admin.database().ref("users").child(uid).remove();

    return { message: "User deleted from Auth and Database successfully." };
  } catch (error) {
    console.error("Error deleting user:", error);
    throw new functions.https.HttpsError("internal", "Failed to delete user.");
  }
});

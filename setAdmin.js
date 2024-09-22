const admin = require("firebase-admin");

// Replace with the path to your Firebase service account key JSON file
const serviceAccount = require("./path/to/serviceAccountKey.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

// Replace this email with the email of the user you want to make admin
const email = "your-email@example.com";

admin.auth().getUserByEmail(email)
  .then((user) => {
    return admin.auth().setCustomUserClaims(user.uid, { admin: true });
  })
  .then(() => {
    console.log(`Success! ${email} has been made an admin.`);
  })
  .catch((error) => {
    console.error("Error making admin:", error);
  });

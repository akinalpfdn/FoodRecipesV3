const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// Trigger for new recipes or when `isApproved` changes to false
exports.sendAdminNotification =
 functions.firestore.document("recipes/{recipeId}")
     .onWrite(async (change, context) => {
       const newValue = change.after.data();
       const previousValue = change.before.data();

       if (!previousValue ||
      (newValue.isApproved === false && previousValue.isApproved !== false)) {
         try {
           const adminDoc = await admin.firestore()
               .collection("admins")
               .doc("roles")
               .get();
           const adminUIDs = adminDoc.data().adminUIDs;

           if (!adminUIDs || adminUIDs.length === 0) {
             console.log("No admin UIDs found");
             return null;
           }

           const tokenPromises = adminUIDs.map((uid) =>
             admin.firestore().collection("users").doc(uid).get(),
           );

           const userDocs = await Promise.all(tokenPromises);

           const validTokens =
          userDocs.map((doc) => doc.data().fcmToken).filter((token) => !!token);

           if (validTokens.length === 0) {
             console.log("No valid FCM tokens found");
             return null;
           }

           const payload = {
             notification: {
               title: "Recipe Needs Approval",
               body: "A recipe needs approval.",
               clickAction: "FLUTTER_NOTIFICATION_CLICK",
             },
           };

           const response =
          await admin.messaging().sendToDevice(validTokens, payload);
           console.log("Notifications sent successfully:", response);

           return null;
         } catch (error) {
           console.error("Error sending notification:", error);
           return null;
         }
       }

       return null;
     });

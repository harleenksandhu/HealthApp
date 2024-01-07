
const {onRequest} = require("firebase-functions/v2/https");
const logger = require("firebase-functions/logger");
const admin = require('firebase-admin');
const sgMail = require('@sendgrid/mail');

admin.initializeApp();

sgMail.setApiKey('YOUR_SENDGRID_API_KEY');

exports.sendEmailNotification = functions.https.onCall(async (data, context) => {
    const { to, subject, text } = data;

    const msg = {
        to,
        from: 'your-email@example.com', // Replace with your email
        subject,
        text,
    };

    try {
        await sgMail.send(msg);
        return { success: true };
    } catch (error) {
        console.error(error);
        throw new functions.https.HttpsError('internal', 'Email sending failed');
    }
});
// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

const hmac_sha256 = require('crypto-js/hmac-sha256');
const request = require('request');

const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
exports.helloWorld = functions.https.onRequest((request, response) => {
 response.send("Hello from Firebase!");
});


// in order to be able to create custom token we need to initialize Firebase 
// Admin SDK with private key
// https://firebase.google.com/docs/admin/setup
const serviceAccount = require('./service-account-key.json');
const admin = require('firebase-admin');
const firebaseConfig = functions.config().firebase;
firebaseConfig.credential = admin.credential.cert(serviceAccount);
admin.initializeApp(firebaseConfig);

exports.getCustomToken = functions.https.onRequest((req, res) => {
    const accessToken = req.query.access_token || '';
    const facebookAppSecret = functions.config().facebook.app_secret;
    const appSecretProof = hmac_sha256(accessToken, facebookAppSecret);

    // validate Facebook Account Kit access token
    // https://developers.facebook.com/docs/accountkit/graphapi
    request({
        url: `https://graph.accountkit.com/v1.3/me/?access_token=${accessToken}`,
        json: true
    }, (error, fbResponse, data) => {
        if (error) {
            console.error('Access token validation request failed\n', error);
            res.status(400).send(error);
        } else if (data.error) {
            console.error('Invalid access token\n', 
                `access_token=${accessToken}\n`, 
                `appsecret_proof=${appSecretProof}\n`, 
                data.error);
            res.status(400).send(data);
        } else {
        	console.log(data);
        	var uid = data.phone.number;
            admin.auth().createCustomToken(uid)
                .then(function(customToken) {

                	admin.auth().getUser(uid)
			        .then(function(userRecord) {
			            // See the UserRecord reference doc for the contents of userRecord.
			            console.log("Successfully fetched user data:", userRecord.toJSON());
			            admin.auth().updateUser(uid, {
			                email: 'phone_' + uid.substring(1) + '@hitoday.vn',
			                emailVerified: true,
			                displayName: uid,
			                photoURL: 'https://www.w3schools.com/howto/img_avatar.png'
			            })
			            .then(function(userRecord) {
			                console.log(userRecord);
						    // Send token back to client
	    				    res.status(200).send(customToken);
	    				    return;
			            })
			            .catch(function(error) {
			                console.log("Error updating user: ", error);
			                res.statusCode = 401;
			                res.send("Error al actualizar usuario")
			            });
			            return;
			        })
			        .catch(function(error) {
			            console.log("Error fetching user data:", error);

			            admin.auth().createUser({
			                uid:uid,
			                email: 'phone_' + uid.substring(1) + '@hitoday.vn',
			                emailVerified: true,
			                displayName: uid,
			                photoURL: 'https://www.w3schools.com/howto/img_avatar.png',
			                password: '123456a@A'
			            })
			            .then(function(userRecord) {
			                console.log(userRecord);
						    // Send token back to client
	    				    res.status(200).send(customToken);
	    				    return;
			            })
			            .catch(function(error) {
			                console.log("Error creating new user: ", error);
			                res.statusCode = 401;
			                res.send("Error al crear el usuario")
			            });
			            return;
			        });
                	
				    return;
				 })
                .catch(error => {
                    console.error('Creating custom token failed:', error);
                    res.status(400).send(error);
                })
        }
    });
});
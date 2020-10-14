const functions = require('firebase-functions')
const admin = require('firebase-admin')

require('dotenv').config();

let serviceAccount = { 
  type: process.env.TYPE,
  project_id: process.env.PROJECT_ID,
  private_key_id: process.env.PRIVATE_KEY_ID,
  private_key: process.env.PRIVATE_KEY,
  client_email: process.env.CLIENT_EMAIL,
  client_id: process.env.CLIENT_ID,
  auth_uri: process.env.AUTH_URI,
  token_uri: process.env.TOKEN_URI,
  auth_provider_x509_cert_url: process.env.AUTH_PROVIDER_X_CERT_URL,
  client_x509_cert_url: process.env.CLIENT_X_CERT_URL
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://dutchpeng-fb0e0.firebaseio.com",
})

const request = require('request-promise');
const { auth } = require('firebase-admin');

    const kakaoRequestMeUrl = 'https://kapi.kakao.com/v2/user/me?sercure_resource=true'

    const naverRequestMeUrl =  'https://openapi.naver.com/v1/nid/me'
    
    /**
     * requestMe - Returns user profile from Kakao API
     *
     * @param  {String} kakaoAccessToken Access token retrieved by Kakao Login API
     * @return {Promiise<Response>}      User profile response in a promise
     */
    function requestMe(kakaoAccessToken) {
      console.log('Requesting user profile from Kakao API server.')
      return request({
        method: 'GET',
        headers: {'Authorization': 'Bearer ' + kakaoAccessToken},
        url: kakaoRequestMeUrl
      })
    }

    function requestMeNaver(naverAccessToken){
      console.log('Requseting user profile from Naver API server.')

      return request({
        method: 'GET',
        headers: {
        'Authorization': 'Bearer ' + naverAccessToken,
        'X-Naver-Client-Id': '83C_rnmfc_zhPa7EFQ0n',
        'X-Naver-Client-Secret': 'ZdGol0ufQu'
        },
        url: naverRequestMeUrl
      })


    }
/**
   * updateOrCreateUser - Update Firebase user with the give email, create if
   * none exists.
   *
   * @param  {String} userId        user id per app
   * @param  {String} email         user's email address
   * @param  {String} displayName   user
   * @param  {String} photoURL      profile photo url
   * @return {Prommise<UserRecord>} Firebase user record in a promise
   */
  function updateOrCreateUser(oauth_provider, userId, email, displayName, photoURL) {
    console.log('updating or creating a firebase user');

    const updateParams = {
      provider: oauth_provider,
      displayName: displayName,
      email: email
    };

    if (displayName) {
      updateParams['displayName'] = displayName;
    }
    if (email) {
      updateParams['email'] = email;
    }
    if (photoURL) {
      updateParams['photoURL'] = photoURL;
    }

    console.log(updateParams);
    return admin.auth().updateUser(userId, updateParams)
    .catch((error) => {
      if (error.code === 'auth/user-not-found') { // 유저를 새로 생성해야 한다면.

        updateParams['uid'] = userId;
        if (email) {
          updateParams['email'] = email;
        }
        return admin.auth().createUser(updateParams);
      }
      throw error;
    });
  }

  /**
   * createFirebaseToken - returns Firebase token using Firebase Admin SDK
   *
   * @param  {String} oauthAccessToken access token from Kakao, Naver Login API
   * @return {Promise<String>}                  Firebase token in a promise
   */

   
  function createFirebaseToken(oauth_provider, oauthAccessToken) {
    if(oauth_provider == 'KAKAO'){
      return requestMe(oauthAccessToken).then((response) => {
        const body = JSON.parse(response)
        console.log(body)
        const userId = `kakao:${body.id}`
        if (!userId) {
          return res.status(404)
          .send({message: 'There was no user with the given access token.'})
        }

        let nickname = null
        let profileImage = null
        
        // 카카오 사용자 정보.
        // 수정시 https://developers.kakao.com/docs/latest/ko/kakaologin/common 참고.

        if (body.properties) {
          nickname = body.properties.nickname
          profileImage = body.properties.profile_image_url
        }

        return updateOrCreateUser(oauth_provider,userId, body.kakao_account.email, nickname,
          profileImage)
      }).then((userRecord) => {
        const userId = userRecord.uid
        console.log(`creating a custom firebase token based on uid ${userId}`)
        return admin.auth().createCustomToken(userId, {provider: oauth_provider})
      })
    }else if(oauth_provider == 'NAVER'){

      return requestMeNaver(oauthAccessToken).then((response) => {
        const body = JSON.parse(response)
        console.log(body)
        const userId = `naver:${body.response.id}`
        if (!userId) {
          return res.status(404)
          .send({message: 'There was no user with the given access token.'})
        }

        let name = null
        let profileImage = null

        // 네이버 사용자 정보.
        // 수정시 https://developers.naver.com/docs/login/devguide/ 참고. 

        if (body.response) {
          name = body.response.name
          profileImage = body.response.profile_image
        }

        return updateOrCreateUser(oauth_provider, userId, body.response.email, name,
          profileImage)

      }).then((userRecord) => {
        const userId = userRecord.uid
        console.log(`creating a custom firebase token based on uid ${userId}`)
        return admin.auth().createCustomToken(userId, {provider: oauth_provider})
      })

    }
  }

  exports.kakaoCustomAuth = functions.region('asia-northeast3').https
  .onRequest((req, res) => {
    const token = req.body.token
    if (!token) return resp.status(400).send({error: 'There is no kakao token.'})
    .send({message: 'Access token is a required parameter.'})

    console.log(`Verifying Kakao token: ${token}`)
    createFirebaseToken('KAKAO', token).then((firebaseToken) => {
      console.log(`Returning firebase token to user: ${firebaseToken}`)
      res.send({firebase_token: firebaseToken});
    })

    return
  })

  exports.naverCustomAuth = functions.region('asia-northeast3').https
  .onRequest((req,res) => {
    const token = req.body.token
    if(!token) return resp.status(400).send({error: 'There is naver token.'})
    .send({message: 'Access token is a required parameter.'})

    console.log(`Verifying Naver token: ${token}`)
    createFirebaseToken('NAVER', token).then((firebaseToken) => {
      console.log(`Returning firebase token to user: ${firebaseToken}`)
      res.send({firebase_token: firebaseToken});
    })

    return
  })
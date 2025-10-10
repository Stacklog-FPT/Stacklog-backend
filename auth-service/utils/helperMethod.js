const dotenv = require('dotenv').config();
const OAuth2Client = require('google-auth-library');

const client = new OAuth2Client(process.env.CLIENT_ID);

exports.decodedTokenGoogle = async (idToken) => {
  if (!idToken) throw new Error('Missing IdToken');

  const ticket = await client.verifyIdToken({
    idToken,
    audience: process.env.GOOGLE_CLIENT_ID,
  });

  const p = ticket.getPayload();
  return {
    sub: p.sub,
    email: p.email,
    email_verified: p.email_verified,
    name: p.name,
    picture: p.picture,
    locale: p.locale,
    iss: p.iss,
    aud: p.aud,
    azp: p.azp,
    iat: p.iat,
    exp: p.exp,
  };
};

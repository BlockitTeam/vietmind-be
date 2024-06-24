const forge = require('node-forge');

// Private Key Base64
const privateKeyString = "MIIEvgIBADANBgkqhkiG9wQEFA....";

const encryptedMessage = ".....";

const privateKeyPem = `-----BEGIN PRIVATE KEY-----\n${privateKeyString}\n-----END PRIVATE KEY-----`;
const privateKey = forge.pki.privateKeyFromPem(privateKeyPem);

// Decrypt
const encryptedBytes = forge.util.decode64(encryptedMessage);
const decryptedBytes = privateKey.decrypt(encryptedBytes, 'RSA-OAEP', {
  md: forge.md.sha256.create()
});
const decryptedMessage = forge.util.decodeUtf8(decryptedBytes);

console.log('Decrypted Message:', decryptedMessage);


--------------------------

Encrypt mess

VxPUPafMnRpRU+TomtieNAb509cdKuOPiaX7rTUl2n2xFbfhUoYPqSZG/5KZBdOsZjSOGHg/rDcAuVFNYw6FmcpX47jsd3G6V6BvPYPCf9FjytcYzy0bC2Dm2aNu/MiBW9x96kMF3zrUA7egkeHCh5sFBBm29rtMD9m9uc8szBYh1dnnEhzwX60ACbjKUeS5eIR7CrG1DtBcDAbieJGv2d7tFCz4G1zhZjTCfoPgE3vx39NLMuZWY2bF7ucOo1kA4iKol09I0Xrd1G1aVRr1x55tVLiT842aL+1H4+HcRDJEUxhyA48ia6yLJpz+RIwSdgWUTlAhNRs3dgqSEmQ++Q==


Private key

MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCyfGvUvEy7Cblc/y04a1nwjfyO+OL2N/c4QZKe4t1DJTEII9HUOzzgjVWHPRyw2lRXemf8C6Q1rVqFLoicyH0xf8nx4ihpr/LalEVhq4XWj6xb3EV0VAIicL/BGYwKkxBuGhL/JVi55h6VynzL691C7RxSDniQx1Hp9RvoMQFrZPVi4DwVBauSQS/eGXNf8DXPSC39Rn6UZ5utZlI6wk6HYbbOba6dhM8mh6SVD/ZZVaaLtQBfkEGEJu9vrEIUVoQEJ6heBbdh4vWjndzqbVnPdgXRJPpmkFRp6yW0MeLfCcqKUabqrjM/aV+P31QHhrVIKrVWIqHt4vTjA5jJfYdtAgMBAAECggEASbVhMJbclycyYgnNam3G1DVGteJplCXTletaefwVROvgfkyQlDUsdE1Zo0JlDVH0n7WgqLFEDJi896AacajILr9nrdjoOJEdWQ//QRD88fkeREdIdXxV71QhlESRFTLbh6SD8NNC+25hdhmLhQkwNDnIRsjMGHn/xX7gGfjW7bqvkjyNXQ4AsskUhTjUSATyQ5rl4oLw0ZbJC7G9NRutmaSY/PI8F4+U5snDTUK5Cbv7Yz+KvnZmhiqLgmftO0cyE5tZyqY7bawhEIWE8ch43lC5qPiNC/6ZDO2IBa51G/3EibEHfKDnm7YIOwLGXzUBQlLMfol/XDhdbbsrFGoKrwKBgQDucfa4RFsKeoNhyJQLfJBpjVU8R7OSf7GVD9lHyMFhfR6+Tz6oOKTdawrVttmYxBfT/Nje3MkXQJUK8gfqN2WuOQT+2U+FwlPGJusGvTmAq9tTnxpzYcdvGUDpJ/A6NNYCz/7VayIAxhoh4SorjNI+yvO7eHxnIzgCccUdzza2YwKBgQC/oGRpyu5ULz5+t2gY8OcqgWXNsz09kG6peVbyRRWeuOKLqs0dnhcGEfkpxvKwa2jNcX7VUJQMpcV5k9VHB25Gh/WGfMe62VVpwdP7Gx4wzliB/IVQI0dhKi5E0ogNyRnTIYKFxgzrv6nUSVKRu7Uifu+LVLG369QXfyNkVREL7wKBgQDlI2lHjJC8kh0dY8Y4/5w7gtENG45KUyHRMCjKXfbP+5AGrFp3B/AOw2XnGE8lChQn6Ex0ZlFsYeiYWxwWDOROt4bAbQ6JaMReoFms4TyYFQ6w3i1qAeXIMsl5BaNKHCopC75FUy2a9sR4GEwRC8OjCh+M4W0TI/oYB0K4sb9PJwKBgQCCynuexZZzuSdDn/UaCNsO5PDSPENRUNJnM92HUGXYRsLBp1uGmo+GYiAZRqQAi98lUhDKkcvq8f5d4+wPJeA7nbKUD3jXbF1i6JvB6RlrIHvChNONBfdDN2ILMVMRbbAFrfqDSdEp21CUB1OnCmIwYEkpZS5DpV/Ghc1nPrR62wKBgCLGH6ZSIW4LByGcdJuGfVNxr0LwE5w1JCB6l6bAZcS/Zw8WZrRUq6i9VU/JyeCVPs+6Cz7Xsi0475g/ECPU/CbeD+e4vuIvsW43a+hmt17sw6uTTQlUtPOd6XDam7hfklIh2ysyC8ZFR1Rd4rzd9xRZ0fG6TnnJmwYbgPbc4RSL
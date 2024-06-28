package com.vm.controllers;

import com.vm.model.User;
import com.vm.request.UserRequest;
import com.vm.service.UserService;
import com.vm.util.EncryptionUtil;
import com.vm.util.KeyManagement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> updateStatusCourse() throws Exception {
        String message = "Tu van tam ly";
        SecretKey key = KeyManagement.loadKey();
        String encryptedMessage = EncryptionUtil.encrypt(message, key);
        String decryptedMessage = EncryptionUtil.decrypt(encryptedMessage, key);
        return new ResponseEntity<>("Hello !!! " + decryptedMessage, HttpStatus.OK);
    }

    @GetMapping("e2ee")
    public ResponseEntity<?> checkLogicE2EE() throws Exception {
        //key user GG
        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsnxr1LxMuwm5XP8tOGtZ8I38jvji9jf3OEGSnuLdQyUxCCPR1Ds84I1Vhz0csNpUV3pn/AukNa1ahS6InMh9MX/J8eIoaa/y2pRFYauF1o+sW9xFdFQCInC/wRmMCpMQbhoS/yVYueYelcp8y+vdQu0cUg54kMdR6fUb6DEBa2T1YuA8FQWrkkEv3hlzX/A1z0gt/UZ+lGebrWZSOsJOh2G2zm2unYTPJoeklQ/2WVWmi7UAX5BBhCbvb6xCFFaEBCeoXgW3YeL1o53c6m1Zz3YF0ST6ZpBUaesltDHi3wnKilGm6q4zP2lfj99UB4a1SCq1ViKh7eL04wOYyX2HbQIDAQAB";

        // Chuyển đổi chuỗi public key thành đối tượng PublicKey
        PublicKey recipientPublicKey = getPublicKeyFromString(publicKeyString);
        String message = "Hello, how are you?";
        SecretKey sessionKey = KeyGenerator.getInstance("AES").generateKey();
        Cipher aesCipher = Cipher.getInstance("AES");
        aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey);
        byte[] encryptedMessageBytes = aesCipher.doFinal(message.getBytes());
        String encryptedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        // Encrypt session key with recipient's public key
        Cipher rsaCipher = Cipher.getInstance("RSA");
        rsaCipher.init(Cipher.WRAP_MODE, recipientPublicKey);
        byte[] encryptedSessionKeyBytes = rsaCipher.wrap(sessionKey);
        String encryptedSessionKey = Base64.getEncoder().encodeToString(encryptedSessionKeyBytes);

        // ---------------------------------
        // Decrypt session key with recipient's private key
        String privateKeyString = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCyfGvUvEy7Cblc/y04a1nwjfyO+OL2N/c4QZKe4t1DJTEII9HUOzzgjVWHPRyw2lRXemf8C6Q1rVqFLoicyH0xf8nx4ihpr/LalEVhq4XWj6xb3EV0VAIicL/BGYwKkxBuGhL/JVi55h6VynzL691C7RxSDniQx1Hp9RvoMQFrZPVi4DwVBauSQS/eGXNf8DXPSC39Rn6UZ5utZlI6wk6HYbbOba6dhM8mh6SVD/ZZVaaLtQBfkEGEJu9vrEIUVoQEJ6heBbdh4vWjndzqbVnPdgXRJPpmkFRp6yW0MeLfCcqKUabqrjM/aV+P31QHhrVIKrVWIqHt4vTjA5jJfYdtAgMBAAECggEASbVhMJbclycyYgnNam3G1DVGteJplCXTletaefwVROvgfkyQlDUsdE1Zo0JlDVH0n7WgqLFEDJi896AacajILr9nrdjoOJEdWQ//QRD88fkeREdIdXxV71QhlESRFTLbh6SD8NNC+25hdhmLhQkwNDnIRsjMGHn/xX7gGfjW7bqvkjyNXQ4AsskUhTjUSATyQ5rl4oLw0ZbJC7G9NRutmaSY/PI8F4+U5snDTUK5Cbv7Yz+KvnZmhiqLgmftO0cyE5tZyqY7bawhEIWE8ch43lC5qPiNC/6ZDO2IBa51G/3EibEHfKDnm7YIOwLGXzUBQlLMfol/XDhdbbsrFGoKrwKBgQDucfa4RFsKeoNhyJQLfJBpjVU8R7OSf7GVD9lHyMFhfR6+Tz6oOKTdawrVttmYxBfT/Nje3MkXQJUK8gfqN2WuOQT+2U+FwlPGJusGvTmAq9tTnxpzYcdvGUDpJ/A6NNYCz/7VayIAxhoh4SorjNI+yvO7eHxnIzgCccUdzza2YwKBgQC/oGRpyu5ULz5+t2gY8OcqgWXNsz09kG6peVbyRRWeuOKLqs0dnhcGEfkpxvKwa2jNcX7VUJQMpcV5k9VHB25Gh/WGfMe62VVpwdP7Gx4wzliB/IVQI0dhKi5E0ogNyRnTIYKFxgzrv6nUSVKRu7Uifu+LVLG369QXfyNkVREL7wKBgQDlI2lHjJC8kh0dY8Y4/5w7gtENG45KUyHRMCjKXfbP+5AGrFp3B/AOw2XnGE8lChQn6Ex0ZlFsYeiYWxwWDOROt4bAbQ6JaMReoFms4TyYFQ6w3i1qAeXIMsl5BaNKHCopC75FUy2a9sR4GEwRC8OjCh+M4W0TI/oYB0K4sb9PJwKBgQCCynuexZZzuSdDn/UaCNsO5PDSPENRUNJnM92HUGXYRsLBp1uGmo+GYiAZRqQAi98lUhDKkcvq8f5d4+wPJeA7nbKUD3jXbF1i6JvB6RlrIHvChNONBfdDN2ILMVMRbbAFrfqDSdEp21CUB1OnCmIwYEkpZS5DpV/Ghc1nPrR62wKBgCLGH6ZSIW4LByGcdJuGfVNxr0LwE5w1JCB6l6bAZcS/Zw8WZrRUq6i9VU/JyeCVPs+6Cz7Xsi0475g/ECPU/CbeD+e4vuIvsW43a+hmt17sw6uTTQlUtPOd6XDam7hfklIh2ysyC8ZFR1Rd4rzd9xRZ0fG6TnnJmwYbgPbc4RSL";

        PrivateKey recipientPrivateKey = getPrivateKeyFromString(privateKeyString);// Retrieve from secure storage

        byte[] encryptedSessionKeyBytes2 = Base64.getDecoder().decode(encryptedSessionKey);
        Cipher rsaCipher2 = Cipher.getInstance("RSA");
        rsaCipher2.init(Cipher.UNWRAP_MODE, recipientPrivateKey);
        Key sessionKey2 = rsaCipher2.unwrap(encryptedSessionKeyBytes2, "AES", Cipher.SECRET_KEY);

        // Decrypt message with session key
        byte[] encryptedMessageBytes2 = Base64.getDecoder().decode(encryptedMessage);
        Cipher aesCipher2 = Cipher.getInstance("AES");
        aesCipher2.init(Cipher.DECRYPT_MODE, sessionKey2);
        byte[] decryptedMessageBytes = aesCipher.doFinal(encryptedMessageBytes2);
        String decryptedMessage = new String(decryptedMessageBytes);
        return new ResponseEntity<>("Hello !!! " + decryptedMessage, HttpStatus.OK);
    }

    @GetMapping("e2ee-gpt")
    public ResponseEntity<?> checkLogicE2EEGPT() throws Exception {
        // Convert public and private keys to Base64 strings
        String publicKeyString = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsnxr1LxMuwm5XP8tOGtZ8I38jvji9jf3OEGSnuLdQyUxCCPR1Ds84I1Vhz0csNpUV3pn/AukNa1ahS6InMh9MX/J8eIoaa/y2pRFYauF1o+sW9xFdFQCInC/wRmMCpMQbhoS/yVYueYelcp8y+vdQu0cUg54kMdR6fUb6DEBa2T1YuA8FQWrkkEv3hlzX/A1z0gt/UZ+lGebrWZSOsJOh2G2zm2unYTPJoeklQ/2WVWmi7UAX5BBhCbvb6xCFFaEBCeoXgW3YeL1o53c6m1Zz3YF0ST6ZpBUaesltDHi3wnKilGm6q4zP2lfj99UB4a1SCq1ViKh7eL04wOYyX2HbQIDAQAB";
        String privateKeyString = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCyfGvUvEy7Cblc/y04a1nwjfyO+OL2N/c4QZKe4t1DJTEII9HUOzzgjVWHPRyw2lRXemf8C6Q1rVqFLoicyH0xf8nx4ihpr/LalEVhq4XWj6xb3EV0VAIicL/BGYwKkxBuGhL/JVi55h6VynzL691C7RxSDniQx1Hp9RvoMQFrZPVi4DwVBauSQS/eGXNf8DXPSC39Rn6UZ5utZlI6wk6HYbbOba6dhM8mh6SVD/ZZVaaLtQBfkEGEJu9vrEIUVoQEJ6heBbdh4vWjndzqbVnPdgXRJPpmkFRp6yW0MeLfCcqKUabqrjM/aV+P31QHhrVIKrVWIqHt4vTjA5jJfYdtAgMBAAECggEASbVhMJbclycyYgnNam3G1DVGteJplCXTletaefwVROvgfkyQlDUsdE1Zo0JlDVH0n7WgqLFEDJi896AacajILr9nrdjoOJEdWQ//QRD88fkeREdIdXxV71QhlESRFTLbh6SD8NNC+25hdhmLhQkwNDnIRsjMGHn/xX7gGfjW7bqvkjyNXQ4AsskUhTjUSATyQ5rl4oLw0ZbJC7G9NRutmaSY/PI8F4+U5snDTUK5Cbv7Yz+KvnZmhiqLgmftO0cyE5tZyqY7bawhEIWE8ch43lC5qPiNC/6ZDO2IBa51G/3EibEHfKDnm7YIOwLGXzUBQlLMfol/XDhdbbsrFGoKrwKBgQDucfa4RFsKeoNhyJQLfJBpjVU8R7OSf7GVD9lHyMFhfR6+Tz6oOKTdawrVttmYxBfT/Nje3MkXQJUK8gfqN2WuOQT+2U+FwlPGJusGvTmAq9tTnxpzYcdvGUDpJ/A6NNYCz/7VayIAxhoh4SorjNI+yvO7eHxnIzgCccUdzza2YwKBgQC/oGRpyu5ULz5+t2gY8OcqgWXNsz09kG6peVbyRRWeuOKLqs0dnhcGEfkpxvKwa2jNcX7VUJQMpcV5k9VHB25Gh/WGfMe62VVpwdP7Gx4wzliB/IVQI0dhKi5E0ogNyRnTIYKFxgzrv6nUSVKRu7Uifu+LVLG369QXfyNkVREL7wKBgQDlI2lHjJC8kh0dY8Y4/5w7gtENG45KUyHRMCjKXfbP+5AGrFp3B/AOw2XnGE8lChQn6Ex0ZlFsYeiYWxwWDOROt4bAbQ6JaMReoFms4TyYFQ6w3i1qAeXIMsl5BaNKHCopC75FUy2a9sR4GEwRC8OjCh+M4W0TI/oYB0K4sb9PJwKBgQCCynuexZZzuSdDn/UaCNsO5PDSPENRUNJnM92HUGXYRsLBp1uGmo+GYiAZRqQAi98lUhDKkcvq8f5d4+wPJeA7nbKUD3jXbF1i6JvB6RlrIHvChNONBfdDN2ILMVMRbbAFrfqDSdEp21CUB1OnCmIwYEkpZS5DpV/Ghc1nPrR62wKBgCLGH6ZSIW4LByGcdJuGfVNxr0LwE5w1JCB6l6bAZcS/Zw8WZrRUq6i9VU/JyeCVPs+6Cz7Xsi0475g/ECPU/CbeD+e4vuIvsW43a+hmt17sw6uTTQlUtPOd6XDam7hfklIh2ysyC8ZFR1Rd4rzd9xRZ0fG6TnnJmwYbgPbc4RSL";

        // Convert Base64 strings back to PublicKey and PrivateKey objects
        PublicKey publicKey = getPublicKeyFromString(publicKeyString);
        PrivateKey privateKey = getPrivateKeyFromString(privateKeyString);

        // Original message
        String message = "Hello, this is a secret message!";

        // Encrypt the message with the public key
        String encryptedMessage = encryptMessageWithPublicKey(message, publicKey);
        System.out.println("Encrypted Message: " + encryptedMessage);

        // Decrypt the message with the private key
        String decryptedMessage = decryptMessageWithPrivateKey(encryptedMessage, privateKey);
        return new ResponseEntity<>("Process RSA !!! " + decryptedMessage, HttpStatus.OK);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> getCurrentUser() {
        String username = userService.getCurrentUserName();
        return new ResponseEntity<>(userService.getCurrentUser(username), HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<User> update(@RequestBody UserRequest request) throws Exception {
        String username = userService.getCurrentUserName();
        User user = userService.update(request, username);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    public static PublicKey getPublicKeyFromString(String publicKeyString) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    public static String encryptMessageWithPublicKey(String message, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = cipher.doFinal(message.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static PrivateKey getPrivateKeyFromString(String privateKeyString) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    public static String decryptMessageWithPrivateKey(String encryptedMessage, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));
        return new String(decryptedBytes);
    }

    @GetMapping("/get-doctors")
    public ResponseEntity<?> getDoctors() {
//        userService.getCurrentUser(username);
//
//        return new ResponseEntity<>(userService.getPublicKeyByUserId(user_id), HttpStatus.OK);
        return null;
    }
}

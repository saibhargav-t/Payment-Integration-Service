package com.cpt.payments.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Component;

import com.cpt.payments.pojo.request.Attributes;
import com.cpt.payments.pojo.request.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SignatureCreator {

	private static final String PRIVATE_KEY_CLASSPATH = "private.pem";

	public String serializeData(JsonNode jsonNode) {
        StringBuilder serialized = new StringBuilder();
        if (jsonNode.isObject()) {
            ObjectNode objectNode = (ObjectNode) jsonNode;
            Stream<String> sortedFieldNames = StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(objectNode.fieldNames(), 0), false)
                .sorted();
            sortedFieldNames.forEach(fieldName -> {
                JsonNode valueNode = objectNode.get(fieldName);
                serialized.append(fieldName);
                if (valueNode.isObject() || valueNode.isArray()) {
                    serialized.append(serializeData(valueNode));
                } else {
                    serialized.append(valueNode.asText());
                }
            });
        } else if (jsonNode.isArray()) {
            jsonNode.elements().forEachRemaining(element -> serialized.append(serializeData(element)));
        } else {
            serialized.append(jsonNode.asText());
        }
        return serialized.toString();
    }

	public PrivateKey getPrivate() throws Exception {
		log.debug("Loading private key from classpath: {}", PRIVATE_KEY_CLASSPATH);

		InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PRIVATE_KEY_CLASSPATH);
		if (inputStream == null) {
			throw new IllegalStateException("private.pem not found in classpath.");
		}

		Security.addProvider(new BouncyCastleProvider());
		try (PemReader pemReader = new PemReader(new InputStreamReader(inputStream))) {
			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();

			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePrivate(keySpec);
		}
	}

	public String generateSignature(String requestBody) throws Exception {
		PrivateKey privateKey = getPrivate();
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(requestBody.getBytes(StandardCharsets.UTF_8));
		byte[] digitalSignature = signature.sign();
		return Base64.getEncoder().encodeToString(digitalSignature);
	}

	public static void main(String[] args) throws Exception {
		SignatureCreator sg = new SignatureCreator();

		Attributes attributes = Attributes.builder().country("LT").locale("en").currency("EUR").amount(18.10)
				.firstname("john").lastname("peter").email("johnpeter@gmail.com")
				.failURL("https://somedomain.com/failure/trustly/ref1")
				.successURL("https://somedomain.com/success/trustly/ref1").build();

		Data data = Data.builder().username("CTPuser").password("CTPpassword")
				.notificationURL("https://somedomain.com/trustly/notify/ref1").endUserID("user1-id")
				.messageID("msg1-id").attributes(attributes).build();

		String method = "Deposit";
		String UUID = "67d6c2f3-51b3-4eed-ad1a-16b4c4063c33";

		String jsonString = JsonUtils.toJsonString(data);
		JsonNode jsonNode = JsonUtils.toJsonNode(data);

		String serializedData = sg.serializeData(jsonNode);
		String plainText = method + UUID + serializedData;

		String signature = sg.generateSignature(plainText);
		System.out.println("Signature: " + signature);

		// Optional: Signature verification
		SHA256RSASignatureVerifier sigVerify = new SHA256RSASignatureVerifier();
		boolean isSigVerified = sigVerify.verifySignature(signature, plainText);
		System.out.println("Signature verified: " + isSigVerified);
	}
}

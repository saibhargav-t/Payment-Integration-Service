package com.cpt.payments.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Component;

@Component
public class SHA256RSASignatureVerifier {

	private static final String PUBLIC_KEY_CLASSPATH = "merchant-public.pem";

	public boolean verifySignature(String xSignature, String requestBody) throws Exception {
		PublicKey localPublicKey = getPublicKey();
		Signature publicSignature = Signature.getInstance("SHA256withRSA");
		publicSignature.initVerify(localPublicKey);
		publicSignature.update(requestBody.getBytes(UTF_8));
		byte[] signatureBytes = Base64.getDecoder().decode(xSignature);
		return publicSignature.verify(signatureBytes);
	}

	private PublicKey getPublicKey() throws Exception {
		try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(PUBLIC_KEY_CLASSPATH);
				PemReader pemReader = new PemReader(new InputStreamReader(inputStream))) {

			if (inputStream == null) {
				throw new IllegalStateException("Public key file not found in classpath: " + PUBLIC_KEY_CLASSPATH);
			}

			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();

			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(content);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			return keyFactory.generatePublic(keySpec);
		}
	}

	// Optional: main method for testing
	public static void main(String[] args) throws Exception {
		SHA256RSASignatureVerifier verifier = new SHA256RSASignatureVerifier();

		String xSignature = "ZOsnzNke+ZvkoPU/6+A1zj7YRjntfy7CD7LgRrtWoEmd/f/7VKvSjzEOanSq7CQUnH5Cy1tkBN+Ok9Z5BzbzGF+XGaeQikbHoOAhljm3E8PaCldDndR6neQYZJuU0XZ/hIhjMm4lRN9/U1T8d4Yts0gL8LjdlmcWUCYsNeex3oBV5RJc5UUuLOszVTyl6AEHfZHx9xo/g4vJW8Rr0ToaD0rN8YUCG2+/LMHzStIdw/9fm1Xt1Xb4BXiWuytZjmzxkKZyCkAnSL6QOx/fULVMPUlRHOOEOQ9sKj1W5a7wLOP6DPnRvpn7EFtgBzCwebkGGG3fWjli/REVLshpqpaFiA==";
		String requestBody = "get|api2.ct.com/merchant/providers?countrycode=lt&pageno=1&pagesize=1000|";

		// Place `merchant-public.pem` inside `src/main/resources/`
		boolean result = verifier.verifySignature(xSignature, requestBody);
		System.out.println("Signature verified: " + result);
	}
}

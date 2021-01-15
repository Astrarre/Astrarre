import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

public class Main {
	private static final String TEMPLATE;

	static {
		try {
			TEMPLATE = IOUtils.toString(Main.class.getResourceAsStream("/TemplateKeyImpl.java"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) throws IOException {
		generate("Boolean");
		generate("Byte");
		generate("Short");
		generate("Char");
		generate("Int");
		generate("Float");
		generate("Double");
		generate("Long");
	}

	private static void generate(String upperCase) throws IOException {
		generate(upperCase, upperCase.toLowerCase());
	}

	private static void generate(String upperCase, String lower) throws IOException {
		FileWriter writer = new FileWriter("astrarre-transfer-v0/src/main/java/io/github/astrarre/transfer/v0/api/keys/generated/"+upperCase+"KeyImpl.java");
		writer.write(TEMPLATE.replace("%Upper%", upperCase).replace("%lower%", lower));
		writer.close();
	}
}

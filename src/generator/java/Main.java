import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class Main {
	private static final String TEMPLATE;

	static {
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("/TemplateKeyImpl.java")))) {
			TEMPLATE = reader.lines().collect(Collectors.joining("\n"));
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
		FileWriter writer = new FileWriter("src/main/java/io/github/astrarre/transfer/v0/api/transaction/keys/generated/"+upperCase+"KeyImpl.java");
		writer.write(TEMPLATE.replace("%Upper%", upperCase).replace("%lower%", lower).replace("//%antiformatter%", ""));
		writer.close();
	}
}

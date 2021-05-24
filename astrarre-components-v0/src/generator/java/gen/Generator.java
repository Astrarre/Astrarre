package gen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import sun.misc.IOUtils;

public class Generator {
	public static void main(String[] args) throws IOException {
		InputStream stream = Generator.class.getResourceAsStream("/template.java");
		String str = new String(IOUtils.readNBytes(stream, Integer.MAX_VALUE));
		writeTo(str, "BoolComponent.java", "Bool", "Boolean", "boolean");
		writeTo(str, "ByteComponent.java", "Byte", "Byte", "byte");
		writeTo(str, "CharComponent.java", "Char", "Character", "char");
		writeTo(str, "ShortComponent.java", "Short", "Short", "short");
		writeTo(str, "FloatComponent.java", "Float", "Float", "float");
		writeTo(str, "IntComponent.java", "Int", "Integer", "int");
		writeTo(str, "DoubleComponent.java", "Double", "Double", "double");
		writeTo(str, "LongComponent.java", "Long", "Long", "long");
	}

	public static void writeTo(String format, String fileName, String titleName, String wrapperName, String primitive) {
		try(FileWriter writer = new FileWriter("astrarre-components-v0/src/main/java/io/github/astrarre/components/v0/api/components/" + fileName)) {
			writer.write(String.format(format, titleName, wrapperName, primitive));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

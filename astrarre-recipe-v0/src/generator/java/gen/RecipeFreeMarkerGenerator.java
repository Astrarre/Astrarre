package gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class RecipeFreeMarkerGenerator {
	public static void main(String[] args) throws IOException, TemplateException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
		cfg.setDirectoryForTemplateLoading(new File("astrarre-recipe-v0/src/generator/resources"));
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
		cfg.setFallbackOnNullLoopVariable(false);
		Map<String, Object> properties = new HashMap<>();
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		properties.put("upperAlpha", alphabet.toCharArray());
		properties.put("lowerAlpha", alphabet.toLowerCase(Locale.ROOT).toCharArray());
		// mix of greek and latin prefixes (whichever sounds best to me)
		properties.put("prefixes", new String[] {"Mono", "Bi", "Tri", "Quad", "Hex", "Hept", "Oct", "Novem", "Dec", "Undec", "Duodec", "TriDec"});
		Template template = cfg.getTemplate("recipe.ftlh");
		FileWriter writer = new FileWriter("astrarre-recipe-v0/src/main/java/io/github/astrarre/recipe/v0/api/Recipe.java");
		template.process(properties, writer);
	}
}

package cls;

import java.lang.reflect.Method;

import io.github.astrarre.abstracter.AbstracterConfig;
import io.github.astrarre.abstracter.func.filter.MemberFilter;

public class CompoundTagMethodFilter implements MemberFilter<Method> {
	@Override
	public boolean test(AbstracterConfig config, Class<?> abstracting, Method method) {
		String name = method.getName();
		return !(name.startsWith("get") || name.endsWith("Array") || name.equals("getKeys"));
	}
}

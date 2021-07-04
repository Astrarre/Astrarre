package io.github.astrarre.util.v0.api.func;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Iterables;
import com.google.common.reflect.TypeToken;
import org.objectweb.asm.Type;

// todo asm generated things
@SuppressWarnings ({
		"unchecked",
		"UnstableApiUsage"
})
public interface ArrayFunc<A> extends Serializable {
	static <A> IterFunc<A> iter(ArrayFunc<A> func) {
		return func.asIter();
	}

	/**
	 * finds the first non-null value in the list, otherwise returns null
	 */
	ArrayFunc NON_NULL = arr -> {
		for (Object a : arr) {
			if(a != null) {
				return a;
			}
		}
		return null;
	};

	A combine(A[] array);

	default IterFunc<A> asIter(Class<A> type) {
		return arr -> this.combine(Iterables.toArray(arr, type));
	}

	default IterFunc<A> asIter() {
		// egregious type hacks
		TypeToken<?> token = new TypeToken<A>(this.getClass()) {};
		Class<?> type = token.getRawType();
		if(type == Object.class) { // doesn't work on lambda
			try {
				Method writeReplace = this.getClass().getDeclaredMethod("writeReplace");
				writeReplace.setAccessible(true);
				SerializedLambda sl = (SerializedLambda) writeReplace.invoke(this);
				type = Class.forName(Type.getMethodType(sl.getInstantiatedMethodType()).getReturnType().getClassName());
			} catch (ReflectiveOperationException e) {}
		}
		return this.asIter((Class<A>) type);
	}
}

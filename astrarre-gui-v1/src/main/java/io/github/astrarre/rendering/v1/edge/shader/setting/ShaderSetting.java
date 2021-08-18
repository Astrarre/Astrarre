package io.github.astrarre.rendering.v1.edge.shader.setting;

import io.github.astrarre.rendering.internal.ogl.PrimitiveSupplier;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;
import org.jetbrains.annotations.ApiStatus;

import net.minecraft.util.Identifier;

public abstract class ShaderSetting<Next extends Global> implements Global {
	final Next next;

	public ShaderSetting(Next next) {
		this.next = next;
	}

	@SuppressWarnings("unchecked")
	public static <Next extends Global> Factory<Img<Next>> image() {
		return (Factory) Img.FACTORY;
	}

	/**
	 * This is where u upload your shader's parameters.
	 * @see Img#texture(Identifier)
	 */
	@Override
	public DataStack getActive() {
		return this.next.getActive();
	}

	@ApiStatus.OverrideOnly
	public abstract void disable(BuiltDataStack stack);

	@ApiStatus.OverrideOnly
	public abstract void enable(BuiltDataStack stack);

	public Next getNext() {
		Next next = this.next;
		if(next instanceof PrimitiveSupplier s) {
			next = (Next) s.create();
		}
		return next;
	}

	public interface Factory<A extends ShaderSetting<?>> {
		@SuppressWarnings("unchecked")
		default <T extends Global> ShaderSetting<T> create(T val) {
			return (ShaderSetting<T>) this.create0(val);
		}

		A create0(Global val);
	}
}

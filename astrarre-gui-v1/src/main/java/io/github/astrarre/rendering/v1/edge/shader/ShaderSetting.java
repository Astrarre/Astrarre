package io.github.astrarre.rendering.v1.edge.shader;

import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.mem.DataStack;

import net.minecraft.util.Identifier;

public abstract class ShaderSetting<Next extends Global> implements Global {
	protected final Next next;

	public ShaderSetting(Next next) {
		this.next = next;
	}

	@SuppressWarnings("unchecked")
	public static <Next extends Global> Factory<Image<Next>> image() {
		return (Factory) Image.FACTORY;
	}

	/**
	 * This is where u upload your shader's parameters.
	 * @see Image#texture(Identifier)
	 */
	@Override
	public DataStack getActive() {
		return this.next.getActive();
	}

	public abstract void takedown(BuiltDataStack stack);

	public abstract void setup(BuiltDataStack stack);

	public interface Factory<A extends ShaderSetting<?>> {
		@SuppressWarnings("unchecked")
		default <T extends Global> ShaderSetting<T> create(T val) {
			return (ShaderSetting<T>) this.create0(val);
		}

		A create0(Global val);
	}
}

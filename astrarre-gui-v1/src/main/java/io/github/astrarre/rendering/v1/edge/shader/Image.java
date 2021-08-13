package io.github.astrarre.rendering.v1.edge.shader;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;

import net.minecraft.util.Identifier;

public class Image<T extends Global> extends ShaderSetting<T> {
	static final ShaderSetting.Factory<Image<?>> FACTORY = Image::new;

	public Image(T val) {
		super(val);
	}

	@Override
	public void takedown(BuiltDataStack stack) {
	}

	@Override
	public void setup(BuiltDataStack stack) {
		RenderSystem.setShaderTexture(0, stack.pop());
	}

	public T texture(Identifier texture) {
		this.getActive().push(texture);
		return this.next;
	}

}
package io.github.astrarre.rendering.v1.edge.shader.settings;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.astrarre.rendering.v1.edge.mem.BuiltDataStack;
import io.github.astrarre.rendering.v1.edge.shader.Global;

import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;

public class Img<T extends Global> extends ShaderSetting<T> {
	static final ShaderSetting.Factory<Img<?>> FACTORY = val -> new Img<>(val, 0);

	protected final int index;
	public Img(T val, int index) {
		super(val);
		this.index = index;
	}

	@Override
	public void disable(BuiltDataStack stack) {
		Object obj = stack.pop();
		if(obj instanceof LightmapTextureManager m) {
			m.disable();
		} else if(obj instanceof OverlayTexture t) {
			t.teardownOverlayColor();
		}
	}

	@Override
	public void enable(BuiltDataStack stack) {
		Object obj = stack.pop();
		if(obj instanceof Identifier i) {
			RenderSystem.setShaderTexture(this.index, i);
		} else if(obj instanceof AbstractTexture t) {
			RenderSystem.setShaderTexture(this.index, t.getGlId());
		} else if(obj instanceof LightmapTextureManager m) {
			m.enable();
		} else if(obj instanceof OverlayTexture t) {
			t.setupOverlayColor();
		}
	}

	public T texture(Identifier texture) {
		this.getActive().push(texture).setSetting(this);
		return this.getNext();
	}

	public T texture(AbstractTexture texture) {
		this.getActive().push(texture).setSetting(this);
		return this.getNext();
	}
}
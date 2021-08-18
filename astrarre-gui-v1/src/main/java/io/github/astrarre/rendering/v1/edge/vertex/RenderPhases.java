package io.github.astrarre.rendering.v1.edge.vertex;

import com.mojang.blaze3d.systems.RenderSystem;

public enum RenderPhases implements RenderPhase {
	BLEND(RenderSystem::enableBlend, RenderSystem::disableBlend),
	TEXTURE(RenderSystem::enableTexture, RenderSystem::disableTexture),
	DEPTH(RenderSystem::enableDepthTest, RenderSystem::disableDepthTest),
	DEPTH_MASK(() -> RenderSystem.depthMask(true), () -> RenderSystem.depthMask(false)),
	CULL(RenderSystem::enableCull, RenderSystem::disableCull),
	NO_TEXTURE(TEXTURE),
	NO_BLEND(BLEND);

	final Runnable init, takedown;

	RenderPhases(Runnable init, Runnable takedown) {
		this.init = init;
		this.takedown = takedown;
	}

	RenderPhases(RenderPhases settings) {
		this.init = settings.takedown;
		this.takedown = settings.init;
	}

	@Override
	public void init() {
		this.init.run();
	}

	@Override
	public void takedown() {
		this.takedown.run();
	}
}

package io.github.astrarre.rendering.v1.edge.vertex;

import io.github.astrarre.util.v0.api.SafeCloseable;
import io.github.astrarre.util.v0.api.func.ArrayFunc;

/**
 * non-parameterized ShaderSettings basically
 */
public interface RenderPhase extends SafeCloseable {
	RenderPhase EMPTY = new RenderPhase() {
		@Override
		public void init() {}

		@Override
		public void takedown() {}
	};
	ArrayFunc<RenderPhase> COMBINE = array -> new RenderPhase() {
		@Override
		public void init() {
			for(RenderPhase extension : array) {
				extension.init();
			}
		}

		@Override
		public void takedown() {
			for(RenderPhase extension : array) {
				extension.takedown();
			}
		}
	};

	static RenderPhase setup(Runnable init) {
		return new RenderPhase() {
			@Override
			public void init() {
				init.run();
			}

			@Override
			public void takedown() {

			}
		};
	}

	static RenderPhase ext(Runnable init, Runnable deinit) {
		return new RenderPhase() {
			@Override
			public void init() {
				init.run();
			}

			@Override
			public void takedown() {
				deinit.run();
			}
		};
	}

	void init();

	void takedown();

	@Override
	default void close() {
		this.takedown();
	}
}

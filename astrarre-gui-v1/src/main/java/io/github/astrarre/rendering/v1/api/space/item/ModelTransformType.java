package io.github.astrarre.rendering.v1.api.space.item;

import org.jetbrains.annotations.ApiStatus;

import net.minecraft.client.render.model.json.ModelTransformation;

public interface ModelTransformType {
	enum Standard implements ModelTransformType {
		/**
		 * render the item with gui transformations (makes the angle at which you view the item consistent with minecraft guis)
		 */
		GUI(ModelTransformation.Mode.GUI),

		/**
		 * render the item with fixed transformations (makes the angle at which you view the item consistent with item frames & campfires)
		 */
		FIXED(ModelTransformation.Mode.FIXED),

		/**
		 * render the item with entity transformations (makes the angle at which you view the item consistent with item entity & dolphin mouths)
		 */
		ENTITY(ModelTransformation.Mode.GROUND),

		/**
		 * render the item with head transformations (makes the angle at which you view the item consistent with snow golems (pumpkin) or players
		 * when a
		 * block/item is on their head)
		 */
		HEAD(ModelTransformation.Mode.HEAD);

		final ModelTransformation.Mode mode;

		Standard(ModelTransformation.Mode mode) {this.mode = mode;}

		@Override
		public ModelTransformation.Mode getMode() {
			return this.mode;
		}
	}

	enum Holding implements ModelTransformType {
		THIRD_PERSON_RIGHT(Perspective.THIRD, Hand.RIGHT, ModelTransformation.Mode.THIRD_PERSON_RIGHT_HAND),
		THIRD_PERSON_LEFT(Perspective.THIRD, Hand.LEFT, ModelTransformation.Mode.THIRD_PERSON_LEFT_HAND),
		FIRST_PERSON_RIGHT(Perspective.FIRST, Hand.RIGHT, ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND),
		FIRST_PERSON_LEFT(Perspective.FIRST, Hand.LEFT, ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND),
		;

		public final Perspective perspective;
		public final Hand hand;
		private final ModelTransformation.Mode mode;

		Holding(Perspective perspective, Hand hand, ModelTransformation.Mode mode) {
			this.perspective = perspective;
			this.hand = hand;
			this.mode = mode;
		}

		@Override
		public ModelTransformation.Mode getMode() {
			return this.mode;
		}
	}

	enum Perspective {
		FIRST,
		THIRD
	}

	enum Hand {
		LEFT,
		RIGHT
	}

	@ApiStatus.Internal
	ModelTransformation.Mode getMode();
}
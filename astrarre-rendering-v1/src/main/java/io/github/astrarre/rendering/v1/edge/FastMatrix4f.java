package io.github.astrarre.rendering.v1.edge;

import io.github.astrarre.util.v0.api.SafeCloseable;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

/**
 * uses the JIT's ability to optimize allocations that don't escape the scope
 * <code>
 *     try(var ignore = FastMatrix4f.translate(matrix, 10, 10, 3)) {
 *         // matrix is translated
 *     } // close reverts it
 * </code>
 */
public final class FastMatrix4f {
	// todo add utils for other operations

	/**
	 * @param matrix the matrix to translate
	 * @return don't use
	 */
	public static Translate translate(Matrix4f matrix, float offX, float offY, float offZ) {
		Translate translate = new Translate(matrix, matrix.a03, matrix.a13, matrix.a23, matrix.a33);
		matrix.multiplyByTranslation(offX, offY, offZ);
		return translate;
	}

	public static Translate translate(MatrixStack stack, float offX, float offY, float offZ) {
		return translate(stack.peek().getModel(), offX, offY, offZ);
	}

	public static abstract class Reverter implements SafeCloseable {
		public final Matrix4f matrix;
		protected Reverter(Matrix4f matrix) {this.matrix = matrix;}
	}

	public static final class Translate extends Reverter {
		final float a03, a13, a23, a33;
		protected Translate(Matrix4f matrix, float a03, float a13, float a23, float a33) {
			super(matrix);
			this.a03 = a03;
			this.a13 = a13;
			this.a23 = a23;
			this.a33 = a33;
		}

		@Override
		public void close() {
			matrix.a03 = a03;
			matrix.a13 = a13;
			matrix.a23 = a23;
			matrix.a33 = a33;
		}
	}
}

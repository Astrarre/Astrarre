package io.github.astrarre.access.v0.api.entry;

import io.github.astrarre.access.v0.api.Access;

/**
 * This entrypoint is fired when a specific access is initialized.
 *
 * fabric.mod.json example:
 * <pre>
 * {
 *   "entrypoints": {
 *      "astrarre:access{othermod:access}": ["net.mymod.OnOtherModAccess"]
 *   }
 * }
 * </pre>
 *
 * @see Generic
 */
public interface AccessInitEntrypoint<T extends Access<?>> {
	void onInit(String modid, String path, T access);

	/**
	 * This entrypoint is fired when any access is initialized.
	 *
	 * fabric.mod.json example:
	 * <pre>
	 * {
	 *   "entrypoints": {
	 *      "astrarre:access": ["net.mymod.OnAnyAccessInit"]
	 *   }
	 * }
	 * </pre>
	 *
	 */
	interface Generic extends AccessInitEntrypoint<Access<?>> {
	}
}

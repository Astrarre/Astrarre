package io.github.astrarre.gui.v0.api.base.widgets;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.itemview.v0.api.nbt.NBTagView;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a text widget but it hashes the password before sending it over the internet. This isn't that secure, but whatever
 */
public class APasswordTextField extends ATextFieldWidget {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "text_field"), APasswordTextField::new);

	public APasswordTextField(int width, int height) {
		super(ENTRY, width, height);
	}

	@Environment(EnvType.CLIENT)
	private APasswordTextField(NBTagView input) {
		super(ENTRY, input);
	}

	protected APasswordTextField(DrawableRegistry.Entry id, int width, int height) {
		super(id, width, height);
	}

	protected APasswordTextField(DrawableRegistry.Entry id, NBTagView input) {
		super(id, input);
	}

	@Override
	protected void sendTextToServer(String text) {
		Hasher hasher = Hashing.sha512().newHasher();
		hasher.putUnencodedChars(text);
		super.sendTextToServer(hasher.hash().toString());
	}
}
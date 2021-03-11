package io.github.astrarre.gui.v0.api.widgets;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import io.github.astrarre.gui.v0.api.DrawableRegistry;
import io.github.astrarre.networking.v0.api.io.Input;
import io.github.astrarre.util.v0.api.Id;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * a text widget but it hashes the password before sending it over the internet. This isn't that secure, but whatever
 */
public class PasswordWidget extends TextFieldWidget {
	private static final DrawableRegistry.Entry ENTRY = DrawableRegistry.register(Id.create("astrarre-gui-v0", "text_field"), PasswordWidget::new);

	public PasswordWidget(int width, int height) {
		super(ENTRY, width, height);
	}

	@Environment(EnvType.CLIENT)
	private PasswordWidget(Input input) {
		super(ENTRY, input);
	}

	protected PasswordWidget(DrawableRegistry.Entry id, int width, int height) {
		super(id, width, height);
	}

	protected PasswordWidget(DrawableRegistry.Entry id, Input input) {
		super(id, input);
	}

	@Override
	protected void sendTextToServer(String text) {
		Hasher hasher = Hashing.sha512().newHasher();
		hasher.putUnencodedChars(text);
		super.sendTextToServer(hasher.hash().toString());
	}
}
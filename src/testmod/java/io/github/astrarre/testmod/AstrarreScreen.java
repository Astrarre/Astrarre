package io.github.astrarre.testmod;

import io.github.astrarre.gui.internal.RootContainerInternal;
import io.github.astrarre.gui.internal.access.ContainerAccess;
import io.github.astrarre.gui.v0.api.drawable.Button;
import io.github.astrarre.rendering.v0.api.Transformation;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class AstrarreScreen extends Screen implements Element {
	public AstrarreScreen(Text title) {
		super(title);
	}


	// todo ticking components
	@Override
	public void tick() {
		super.tick();
	}

}

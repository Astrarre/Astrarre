package testmod;

import java.util.List;

import io.github.astrarre.gui.v0.api.RootContainer;
import io.github.astrarre.gui.v0.api.base.AAggregateDrawable;
import io.github.astrarre.gui.v0.api.base.widgets.AButton;
import io.github.astrarre.gui.v0.api.container.ContainerGUI;
import io.github.astrarre.gui.v0.fabric.adapter.slot.ASlot;
import io.github.astrarre.networking.v0.api.network.NetworkMember;

public class TestContainerGui extends ContainerGUI {
	public TestContainerGui(RootContainer container, NetworkMember member) {
		super(container, member, INVENTORY_WIDTH + 10, INVENTORY_HEIGHT + 90);
	}

	@Override
	protected void addGui(AAggregateDrawable panel, int width, int height, List<ASlot> playerSlots) {
		panel.add(new AButton(AButton.MEDIUM));

	}
}

package io.github.astrarre.gui.v1.api.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import io.github.astrarre.gui.v1.api.component.button.AButton;
import io.github.astrarre.gui.v1.api.listener.focus.FocusDirection;
import io.github.astrarre.gui.v1.api.util.Transformed;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.plane.icon.Icons;
import io.github.astrarre.rendering.v1.api.plane.icon.PixelatedTriangleIcon;
import io.github.astrarre.rendering.v1.api.util.AngleFormat;
import io.github.astrarre.rendering.v1.api.util.Axis2d;
import org.jetbrains.annotations.NotNull;

/**
 * A panel that only shows one component at any given time
 */
public class PaginatedPanel extends APanel {
	public static final float FIT_SIZE = -1, SKIP = -2;

	public APanel withMinimalButtonsFit(Position type, float height, boolean label) {
		return this.withMinimalButtons(type, FIT_SIZE, FIT_SIZE, height, label);
	}

	public APanel withButtonsFit(Position type, float height, boolean label) {
		return this.withButtons(type, FIT_SIZE, FIT_SIZE, FIT_SIZE, FIT_SIZE, height, label);
	}

	public APanel withMinimalButtons(Position type, float previousWidth, float nextWidth, float height, boolean label) {
		return this.withButtons(type, SKIP, previousWidth, nextWidth, SKIP, height, label);
	}

	/**
	 * @param type where to put the tab buttons
	 * @param height the height of the tab bar
	 * @param label whether or not to have a label in the middle that shows which page the user is on
	 * @return the panel with forward/back buttons
	 */
	public APanel withButtons(Position type,
			float toStartWidth,
			float previousWidth,
			float nextWidth,
			float toEndWidth,
			float height,
			boolean label) {
		List<AComponent> components = new ArrayList<>();

		float fixedWidths = 0;
		int unfixed = 0;

		if(toStartWidth >= 0) {
			fixedWidths += toStartWidth;
		} else {
			unfixed++;
		}
		if(previousWidth >= 0) {
			fixedWidths += previousWidth;
		} else {
			unfixed++;
		}
		if(nextWidth >= 0) {
			fixedWidths += nextWidth;
		} else {
			unfixed++;
		}
		if(toEndWidth >= 0) {
			fixedWidths += toEndWidth;
		} else {
			unfixed++;
		}
		if(label) {
			unfixed++;
		}

		float currentFit = (this.getWidth() - fixedWidths) / unfixed;

		if(toStartWidth != SKIP) {
			components.add(this.extracted(toStartWidth, height, currentFit, 270, true, this::toStart));
		}
		if(previousWidth != SKIP) {
			components.add(this.extracted(previousWidth, height, currentFit, 270, false, this::backwards));
		}
		if(label) {
			components.add(new AIcon(() -> Icon.scrollingText((this.index() + 1) + "/" + this.cmps.size(), currentFit)));
		}
		if(nextWidth != SKIP) {
			components.add(this.extracted(nextWidth, height, currentFit, 90, false, this::forward));
		}
		if(toEndWidth != SKIP) {
			components.add(this.extracted(toEndWidth, height, currentFit, 90, true, this::toEnd));
		}

		AList list = new AList(Axis2d.X, 0);
		components.forEach(list::add);

		AList full = new AList(Axis2d.Y, 3);
		if(type == Position.BOTTOM) {
			full.add(this, list);
		} else {
			full.add(list, this);
		}

		return full;
	}

	public boolean toEnd() {
		while(this.next(FocusDirection.FORWARD)) {
		}
		return this.next(FocusDirection.BACKWARDS);
	}

	public boolean toStart() {
		while(this.next(FocusDirection.FORWARD)) {
		}
		return this.next(FocusDirection.FORWARD);
	}

	public boolean forward() {
		// we do twice to initialize the focused variable
		return this.next(FocusDirection.FORWARD) || this.next(FocusDirection.FORWARD);
	}

	public boolean backwards() {
		return this.next(FocusDirection.BACKWARDS) || this.next(FocusDirection.BACKWARDS);
	}

	@Override
	public @NotNull Iterator<Transformed<?>> iterator() {
		if(this.focused != null) {
			return Iterators.singletonIterator(this.focused);
		} else {
			return Collections.emptyIterator();
		}
	}

	@Override
	public APanel add(Transformed<?>... component) {
		super.add(component);
		this.next(FocusDirection.FORWARD);
		return this;
	}

	@Override
	public boolean next(FocusDirection direction) {
		if(this.focused == null && !this.cmps.isEmpty()) {
			this.focused = (direction.isForward() ? this.cmps.get(0) : Iterables.getLast(this.cmps)).component();
		}
		boolean value = super.next(direction);
		this.recomputeBounds();
		return value;
	}

	int index() {
		return this.indexOf(this.focused);
	}

	private AComponent extracted(float width_, float height, float currentFit, int degrees, boolean second, Runnable callback) {
		float width = width_ == FIT_SIZE ? currentFit : width_;

		Icon next = new PixelatedTriangleIcon(0xff444444, Math.min(height - 2, width - 2), second ? height * .66f : height);
		if(second) {
			next = next.andThen(next.offset(0, height / 2));
		}
		next = next.rotateAboutMiddle(AngleFormat.DEGREES, degrees);

		Icon finalNext = next;
		var group = Icons.Groups.button(width, height).transform(icon -> icon.overlayCentered(finalNext));
		return AButton.button(group, c -> callback.run());
	}

	public enum Position {
		TOP, BOTTOM
	}
}

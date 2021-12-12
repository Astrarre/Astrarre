package io.github.astrarre.gui.v1.api.component.button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import io.github.astrarre.gui.v1.api.component.AHoverableComponent;
import io.github.astrarre.gui.v1.api.component.ToggleableComponent;
import io.github.astrarre.gui.v1.api.listener.cursor.ClickType;
import io.github.astrarre.gui.v1.api.listener.cursor.Cursor;
import io.github.astrarre.gui.v1.api.listener.cursor.CursorType;
import io.github.astrarre.gui.v1.api.listener.cursor.MouseListener;
import io.github.astrarre.rendering.v1.api.plane.icon.Icon;
import io.github.astrarre.rendering.v1.api.space.Render3d;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;

/**
 * A button that can cycle between multiple states. The code here is generic enough to allow for toggle based or single click (normal) buttons.
 * @see #button(Icon.Group, Consumer)
 * @see #toggle(Icon.Group, Icon.Group, ToggleListener)
 */
public class AButton extends AHoverableComponent implements MouseListener, ToggleableComponent {
	final List<State> states;
	boolean pressed;
	int activeState;

	interface ToggleListener {
		void accept(Cursor cursor, boolean state);
	}

	public static AButton button(Icon.Group group, Consumer<Cursor> callback) {
		return new AButton(group, callback);
	}

	public static AButton toggle(Icon.Group on, Icon.Group off, ToggleListener callback) {
		return new AButton(new State(on, c -> callback.accept(c,true)), new State(off, c -> callback.accept(c,false)));
	}

	void validateState(State state) {
		state.group.requireUniformSize();
	}

	public AButton(List<State> states) {
		if(states.isEmpty()) {
			throw new IllegalStateException("Must have atleast one state!");
		}
		states.forEach(this::validateState);

		var first = states.get(0);
		var normal = first.group.normal();
		this.setBounds(normal.width(), normal.height());
		this.states = new ArrayList<>(states);
	}

	public AButton(State... states) {
		this(Arrays.asList(states));
	}

	/**
	 * Create a standard, single state button
	 */
	public AButton(Icon.Group group, Consumer<Cursor> callback) {
		this(new State(group, callback));
	}

	@Override
	public boolean mouseClicked(Cursor cursor, ClickType type) {
		return (this.pressed = this.isEnabled());
	}

	@Override
	public boolean mouseReleased(Cursor cursor, ClickType type) {
		boolean released = this.isEnabled() && this.pressed;
		if(released) {
			this.states.get(this.activeState).callback.accept(cursor);
			this.setActiveState(this.states.get(this.activeState + 1));
		}
		return released;
	}

	public State addState(Icon.Group group, Consumer<Cursor> callback) {
		State state = new State(group, callback);
		this.validateState(state);
		this.states.add(state);
		return state;
	}

	public AButton removeState(State state) {
		if(this.states.size() <= 1) {
			throw new IllegalStateException("Must have atleast one state!");
		}
		this.states.remove(state);
		this.activeState = Math.min(this.activeState, this.states.size() - 1);
		return this;
	}

	public void setActiveState(State state) {
		int index = this.states.indexOf(state);
		if(index == -1) {
			throw new IllegalStateException(state + " is not a valid state in " + this);
		} else {
			this.activeState = index;
			state.group.requireUniformSize();
			var normal = state.group.normal();
			this.setBounds(normal.width(), normal.height());
		}
	}

	@Override
	protected void render0(Cursor cursor, Render3d render) {
		this.pressed &= cursor.isPressed(ClickType.Standard.LEFT);
		var group = this.states.get(this.activeState).group;
		if(!this.isEnabled()) {
			group.disabled().render(render);
		} else if(this.pressed) {
			group.pressed().render(render);
		} else if(this.isIn(cursor)) {
			group.hover().render(render);
		} else {
			group.normal().render(render);
		}
	}

	@Override
	protected void onMouseEnter(Cursor cursor, Render3d render) {
		if(this.isEnabled()) {
			cursor.setType(CursorType.Standard.HAND);
		}
	}

	@Override
	protected void onMouseExit(Cursor cursor, Render3d render) {
		if(this.isEnabled()) {
			cursor.setType(CursorType.Standard.ARROW);
		}
	}

	public static final class State {
		private Icon.Group group;
		private Consumer<Cursor> callback;

		public State(Icon.Group group, Consumer<Cursor> callback) {
			Objects.requireNonNull(group, "group cannot be null!");
			Objects.requireNonNull(callback, "callback cannot be null!");
			this.group = group;
			this.callback = callback;
		}

		public State setGroup(Icon.Group group) {
			Objects.requireNonNull(group, "group cannot be null!");
			this.group = group;
			return this;
		}

		public State setCallback(Consumer<Cursor> callback) {
			Objects.requireNonNull(callback, "callback cannot be null!");
			this.callback = callback;
			return this;
		}
	}
}

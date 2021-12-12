package io.github.astrarre.util.test;

import static org.junit.Assert.*;

import io.github.astrarre.util.v0.api.Id;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.minecraft.util.Identifier;

public class IdTest {
	@Test
	public void parseTest() {
		Id create = Id.create("mymod:test");
		assertEquals(create.path(), "test");
		assertEquals(create.mod(), "mymod");
	}

	@Test
	public void idTest() {
		Id create = Id.create("mymod", "identifier");
		assertEquals(new Identifier("mymod", "identifier"), create);
	}
}

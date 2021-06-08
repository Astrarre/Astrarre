package io.github.astrarre.util.test;

import java.util.stream.StreamSupport;

import io.github.astrarre.util.v0.api.event.Event;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EventTest {
	public Event<Integer> sumIter;
	public Event<Integer> sumArr;
	@Before
	public void init() {
		this.sumIter = new Event<>(arr -> StreamSupport.stream(arr.spliterator(), false).mapToInt(Integer::intValue).sum());
		this.sumIter.addListener(4);
		this.sumIter.addListener(7);
		this.sumIter.addListener(9);

		this.sumArr = new Event<>(arr -> {
			int sum = 0;
			for (Integer integer : arr) {
				sum += integer;
			}
			return sum;
		}, Integer.class);
		this.sumArr.addListener(4);
		this.sumArr.addListener(13);
		this.sumArr.addListener(5);
	}

	@Test
	public void sumIter() {
		Assert.assertEquals(this.sumIter.get().intValue(), 22);
	}

	@Test
	public void sumArray() {
		Assert.assertEquals(this.sumArr.get().intValue(), 23);
	}
}

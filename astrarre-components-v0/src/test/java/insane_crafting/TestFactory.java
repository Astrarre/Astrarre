package insane_crafting;

import io.github.astrarre.components.internal.lazyAsm.standard.CopyAccess;
import io.github.astrarre.components.v0.api.factory.ComponentManager;
import io.github.astrarre.components.v0.api.factory.DataObjectHolder;
import io.github.astrarre.components.v0.api.components.BoolComponent;

public class TestFactory {
	public static void main(String[] args) {
		ComponentManager<Test> test = ComponentManager.newManager("insane_crafting", "hello");
		BoolComponent<Test> boolComponent = test.create(BoolComponent.class, "insane_crafting", "test1");
		Test h = new Test();
		System.out.println(boolComponent.getBool(h));
		boolComponent.setBool(h,true);
		System.out.println(boolComponent.getBool(h));
		BoolComponent<Test> boolComponent2 = test.create(BoolComponent.class, "insane_crafting", "test2");
		System.out.println(boolComponent2.getBool(h));
		boolComponent2.setBool(h,true);
		System.out.println(boolComponent2.getBool(h));
		System.out.println(boolComponent.getBool(h));
	}

	public static class Test implements DataObjectHolder {
		public CopyAccess access;
		public int version;

		@Override
		public CopyAccess astrarre_getObject() {
			return this.access;
		}

		@Override
		public int astrarre_getVersion() {
			return this.version;
		}

		@Override
		public void astrarre_setObject(CopyAccess object, int version) {
			this.access = object;
			this.version = version;
		}
	}
}

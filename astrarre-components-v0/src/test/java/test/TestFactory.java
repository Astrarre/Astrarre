package test;

import io.github.astrarre.components.internal.access.CopyAccess;
import io.github.astrarre.components.internal.access.DataObjectHolder;
import io.github.astrarre.components.internal.lazyAsm.DataObjectHolderComponentFactory;
import io.github.astrarre.components.v0.api.components.BoolComponent;

public class TestFactory {
	public static void main(String[] args) {
		DataObjectHolderComponentFactory<Test> test = new DataObjectHolderComponentFactory<>("hello","there");
		BoolComponent<Test> boolComponent = test.createInfer("test", "test1");
		Test h = new Test();
		System.out.println(boolComponent.getBool(h));
		boolComponent.setBool(h,true);
		System.out.println(boolComponent.getBool(h));
		BoolComponent<Test> boolComponent2 = test.createInfer("test", "test2");
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

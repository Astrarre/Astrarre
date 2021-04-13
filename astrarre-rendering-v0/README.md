# astrarre-rendering-v0
Astrarre's rendering abstraction
```groovy
repositories {
    maven {
        url 'https://storage.googleapis.com/devan-maven/'
    }
}

dependencies {
    modImplementation 'io.github.astrarre:astrarre-rendering-v0:1.0.0'
}
```

```java
public class QuickStart {
	public void foo() {
		Graphics2d twoDimensionalGraphics = new FabricGraphics2d(matrixStack);
		Graphics3d threeDimensionalGraphics = new FabricGraphics3d(matrixStack);
		try(Close c = twoDimensionalGraphics.translate(10, 10)) {
			// everything drawn in here is translated 10 units right, and 10 units down
        }
    }
}
```
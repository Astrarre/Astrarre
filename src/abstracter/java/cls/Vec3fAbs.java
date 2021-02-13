package cls;

import io.github.astrarre.abstracter.abs.InterfaceAbstracter;
import util.ExtensionMethod;

import net.minecraft.client.util.math.Vector3f;

public class Vec3fAbs extends InterfaceAbstracter {
	public Vec3fAbs() {
		super(Vector3f.class);
		this.name(this.name.replace("Vector3f", "Vec3f"));
		this.post(new ExtensionMethod(ACC_PUBLIC,
				"io/github/astrarre/common/internal/util/math/Vec3fExtensions",
				"transform",
				String.format("(L%s;Lio/github/astrarre/common/v0/api/util/math/Transformation;)V", this.name)));
	}
}

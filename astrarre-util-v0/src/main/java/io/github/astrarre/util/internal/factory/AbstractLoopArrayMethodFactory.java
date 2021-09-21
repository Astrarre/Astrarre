package io.github.astrarre.util.internal.factory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

import io.github.astrarre.util.v0.api.func.predicate.BoolPredicate;
import io.github.astrarre.util.v0.api.func.predicate.BytePredicate;
import io.github.astrarre.util.v0.api.func.predicate.CharPredicate;
import io.github.astrarre.util.v0.api.func.predicate.FloatPredicate;
import io.github.astrarre.util.v0.api.func.predicate.ShortPredicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;

public abstract class AbstractLoopArrayMethodFactory implements Opcodes, ProxyMethodBuilder {
	public static final Object2ObjectOpenHashMap<Class<?>, String> SORT_TO_PREDICATE_TYPE = new Object2ObjectOpenHashMap<>();
	public static final String ARRAY_FIELD_NAME = "array";

	static {
		SORT_TO_PREDICATE_TYPE.defaultReturnValue(Type.getDescriptor(Predicate.class));
		SORT_TO_PREDICATE_TYPE.put(int.class, Type.getDescriptor(IntPredicate.class));
		SORT_TO_PREDICATE_TYPE.put(long.class, Type.getDescriptor(LongPredicate.class));
		SORT_TO_PREDICATE_TYPE.put(float.class, Type.getDescriptor(FloatPredicate.class));
		SORT_TO_PREDICATE_TYPE.put(double.class, Type.getDescriptor(DoublePredicate.class));
		SORT_TO_PREDICATE_TYPE.put(boolean.class, Type.getDescriptor(BoolPredicate.class));
		SORT_TO_PREDICATE_TYPE.put(byte.class, Type.getDescriptor(BytePredicate.class));
		SORT_TO_PREDICATE_TYPE.put(short.class, Type.getDescriptor(ShortPredicate.class));
		SORT_TO_PREDICATE_TYPE.put(char.class, Type.getDescriptor(CharPredicate.class));
	}

	protected final Class<?> type;
	private final Method method;
	protected final Type methodType;
	protected final String internalName;
	protected final String arrayFieldDesc;

	public AbstractLoopArrayMethodFactory(Method method) {
		this.type = method.getDeclaringClass();
		this.method = method;
		this.methodType = Type.getMethodType(Type.getMethodDescriptor(method));
		this.internalName = Type.getInternalName(type);
		this.arrayFieldDesc = "[L" + this.internalName + ";";
	}

	@Override
	public void requestFields(Set<FieldPrototype> fields) {
		var prototype = new FieldPrototype(ARRAY_FIELD_NAME, this.arrayFieldDesc);
		fields.add(prototype);
	}

	@Override
	public final void emit(ClassNode node) {
		MethodVisitor visitor = node.visitMethod(ACC_PUBLIC, this.getMethod().getName(), this.methodType.toString(), null, null);
		this.emit(visitor, node.name, ARRAY_FIELD_NAME, this.arrayFieldDesc);
	}

	public void emit(MethodVisitor visitor, String className, String arrayFieldName, String arrayFieldDesc) {
		this.emitStart(visitor, className, arrayFieldName, arrayFieldDesc);
		visitor.visitVarInsn(ALOAD, 0);
		visitor.visitFieldInsn(GETFIELD, className, arrayFieldName, arrayFieldDesc);
		int length = Arrays.stream(this.methodType.getArgumentTypes()).mapToInt(Type::getSize).sum();
		int arrayLocalVarIndex = length + 1;
		visitor.visitVarInsn(ASTORE, arrayLocalVarIndex); // load array
		visitor.visitVarInsn(ALOAD, arrayLocalVarIndex);
		visitor.visitInsn(ARRAYLENGTH);
		int arrayLengthLocalVarIndex = arrayLocalVarIndex + 1;
		visitor.visitVarInsn(ISTORE, arrayLengthLocalVarIndex); // load length
		int indexLocalVarIndex = arrayLengthLocalVarIndex + 1;
		visitor.visitInsn(ICONST_0);
		visitor.visitVarInsn(ISTORE, indexLocalVarIndex); // initialize index
		Label exitLabel = new Label(), conditionLabel = new Label(), endOfLoopLabel = new Label();
		this.emitPostInit(visitor, className, arrayFieldName, arrayFieldDesc, arrayLocalVarIndex, arrayLengthLocalVarIndex);
		visitor.visitLabel(conditionLabel); // condition
		visitor.visitVarInsn(ILOAD, indexLocalVarIndex);
		visitor.visitVarInsn(ILOAD, arrayLengthLocalVarIndex);
		visitor.visitJumpInsn(IF_ICMPGE, exitLabel);
		visitor.visitVarInsn(ALOAD, arrayLocalVarIndex); // get value at index
		visitor.visitVarInsn(ILOAD, indexLocalVarIndex);
		visitor.visitInsn(AALOAD);
		int functionLocalVarIndex = indexLocalVarIndex + 1;
		visitor.visitVarInsn(ASTORE, functionLocalVarIndex);
		this.emitLoop(visitor,
		              className,
		              arrayFieldName,
		              arrayFieldDesc,
		              arrayLocalVarIndex,
		              arrayLengthLocalVarIndex,
		              indexLocalVarIndex,
		              exitLabel,
		              endOfLoopLabel,
		              functionLocalVarIndex);
		visitor.visitLabel(endOfLoopLabel);
		visitor.visitIincInsn(indexLocalVarIndex, 1);
		visitor.visitJumpInsn(GOTO, conditionLabel);
		visitor.visitLabel(exitLabel);
		this.emitEnd(visitor, className, arrayFieldName, arrayFieldDesc, arrayLocalVarIndex, arrayLengthLocalVarIndex);
	}

	public abstract void emitLoop(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex,
			int indexLocalVarIndex,
			Label exitLabel,
			Label exitConditionLabel,
			int valueLocalVarIndex);

	public abstract void emitEnd(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex);

	public void emitStart(MethodVisitor visitor, String className, String arrayFieldName, String arrayFieldDesc) {
	}

	public void emitPostInit(MethodVisitor visitor,
			String className,
			String arrayFieldName,
			String arrayFieldDesc,
			int arrayLocalVarIndex,
			int arrayLengthLocalVarIndex) {
	}

	public Method getMethod() {
		return this.method;
	}
}

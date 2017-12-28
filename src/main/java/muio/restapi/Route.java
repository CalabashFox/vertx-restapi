package muio.restapi;

import java.lang.invoke.MethodHandle;

public class Route<T> {

	private Class<T> type;

	private T object;

	private MethodHandle method;

	public Route(Class<T> type, T object, MethodHandle method) {
		this.type = type;
		this.object = object;
		this.method = method;
	}

	public Class<T> getType() {
		return type;
	}

	public T getObject() {
		return object;
	}

	public MethodHandle getMethod() {
		return method;
	}
}

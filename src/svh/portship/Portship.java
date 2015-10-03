package svh.portship;

public class Portship {
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println(
					"usage: portship --prefix=/path/to/3ddata --out=/path/to/unity/Assets/3ddata"
					);
		}
	}
}

package svh.portship.format;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Height map information
 */
public class HIMFile {
	private final File file;
	private final int length;
	private final int width;
	private final int gridCount;
	private final float gridSize;
	private final float[][] heights;
	private final float minHeight;
	private final float maxHeight;

	public HIMFile(File file) throws IOException {
		this.file = file;

		try (FileInputStream fis = new FileInputStream(this.file)) {
			try (DataInputStream dis = new DataInputStream(fis)) {
				this.length = dis.readInt();
				this.width = dis.readInt();
				this.gridCount = dis.readInt();
				this.gridSize = dis.readFloat();

				float min = Float.POSITIVE_INFINITY;
				float max = Float.NEGATIVE_INFINITY;
				float current = 0f;
				this.heights = new float[this.length][];
				for (int y = 0; y < this.length; y++) {
					this.heights[y] = new float[this.width];
					for (int x = 0; x < this.width; x++) {
						this.heights[y][x] = current = dis.readFloat();

						if (current < min) {
							min = current;
						}

						if (current > max) {
							max = current;
						}
					}
				}

				this.minHeight = min;
				this.maxHeight = max;
			}
		}
	}

	private File getFile() {
		return this.file;
	}

	private int getLength() {
		return this.length;
	}

	private int getWidth() {
		return this.width;
	}

	private int getGridCount() {
		return this.gridCount;
	}

	private float getGridSize() {
		return this.gridSize;
	}

	private float[][] getHeights() {
		return this.heights;
	}

	private float getMinHeight() {
		return this.minHeight;
	}

	private float getMaxHeight() {
		return this.maxHeight;
	}
}

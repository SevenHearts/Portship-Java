package svh.portship.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import svh.portship.util.LittleEndianDataInputStream;

/**
 * Height map information
 */
public class HIMFile {
	private final int length;
	private final int width;
	private final int gridCount;
	private final float gridSize;
	private final float[][] heights;
	private final float minHeight;
	private final float maxHeight;
	
	@SuppressWarnings("resource")
	public HIMFile(File file) throws IOException {
		this(new FileInputStream(file), true);
	}
	
	public HIMFile(InputStream stream) throws IOException {
		this(stream, false);
	}
	
	public HIMFile(InputStream stream, boolean autoClose) throws IOException {
		try {
			try (LittleEndianDataInputStream dis = new LittleEndianDataInputStream(stream)) {
				this.length = dis.readInt();
				this.width = dis.readInt();
				this.gridCount = dis.readInt();
				this.gridSize = dis.readFloat();

				System.out.format("length=%d width=%d gridCount=%d gridSize=%f%n", this.length, this.width, this.gridCount, this.gridSize);
		
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
		} finally {
			if (autoClose) {
				stream.close();
			}
		}
	}

	public int getLength() {
		return this.length;
	}

	public int getWidth() {
		return this.width;
	}

	public int getGridCount() {
		return this.gridCount;
	}

	public float getGridSize() {
		return this.gridSize;
	}

	public float[][] getHeights() {
		return this.heights;
	}

	public float getMinHeight() {
		return this.minHeight;
	}

	public float getMaxHeight() {
		return this.maxHeight;
	}
}

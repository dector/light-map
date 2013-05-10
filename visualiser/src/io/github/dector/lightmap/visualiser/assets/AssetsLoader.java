package io.github.dector.lightmap.visualiser.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

/**
 * @author dector
 */
public final class AssetsLoader {

	private static final String ASSETS_DIR = "assets/";
	private static final String IMAGES_DIR = ASSETS_DIR + "images/";
	private static final String FONTS_DIR = ASSETS_DIR + "fonts/";

	public static TextureRegion loadImageFileAsRegion(String name, int w, int h) {
		return new TextureRegion(
				new Texture(Gdx.files.internal(IMAGES_DIR + name)), w, h);
	}

	public static BitmapFont loadFont(String name, int size) {
		FileHandle file = Gdx.files.internal(FONTS_DIR + name);
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(file);

		BitmapFont font = generator.generateFont(size);
		generator.dispose();

		return font;
	}
}

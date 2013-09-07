/*
 * Copyright (c) 2013 Monkey By Monkey.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package pm.monkey.maven.plugins.texturepacker;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.sonatype.plexus.build.incremental.BuildContext;

import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.tools.imagepacker.TexturePacker2.Settings;
import com.badlogic.gdx.tools.imagepacker.TexturePackerFileProcessor;

/**
 * Pack textures with LibGDX's TexturePacker.
 */
@Mojo(name="pack", requiresProject=false,
      defaultPhase=LifecyclePhase.PROCESS_RESOURCES)
public class PackMojo extends AbstractMojo {

    @Component
    private BuildContext buildContext;

    /**
     * Input directory.
     */
    @Parameter(required=true)
    private File inputDir;

    /**
     * Output directory.
     */
    @Parameter(required=true)
    private File outputDir;

    /**
     * Pack file name.
     */
    @Parameter(required=true)
    private String packFileName;

    /**
     * If true, output pages will have power of two dimensions (required for
     * GL1).
     */
    @Parameter(defaultValue="true")
    private boolean pot;

    /**
     * The number of pixels between packed images on the x-axis.
     */
    @Parameter(defaultValue="2")
    private int paddingX;

    /**
     * The number of pixels between packed images on the y-axis.
     */
    @Parameter(defaultValue="2")
    private int paddingY;

    /**
     * If true, half of the paddingX and paddingY will be used around the edges
     * of the packed texture.
     */
    @Parameter(defaultValue="true")
    private boolean edgePadding;

    @Parameter(defaultValue="false")
    private boolean duplicatePadding;

    /**
     * If true, TexturePacker will attempt more efficient packing by rotating
     * images 90 degrees. Applications must take special care to draw these
     * regions properly.
     */
    @Parameter(defaultValue="false")
    private boolean rotation;

    /**
     * The minimum width of output pages.
     */
    @Parameter(defaultValue="16")
    private int minWidth;

    /**
     * The minimum height of output pages.
     */
    @Parameter(defaultValue="16")
    private int minHeight;

    /**
     * The maximum width of output pages. 1024 is safe for all devices.
     * Extremely old devices may have degraded performance over 512.
     */
    @Parameter(defaultValue="1024")
    private int maxWidth;

    /**
     * The maximum height of output pages. 1024 is safe for all devices.
     * Extremely old devices may have degraded performance over 512.
     */
    @Parameter(defaultValue="1024")
    private int maxHeight;

    /**
     * Force the output texturepack to be square.
     */
    @Parameter(defaultValue="false")
    private boolean forceSquareOutput;

    /**
     * If true, blank pixels on the left and right edges of input images will
     * be removed. Applications must take special care to draw these regions
     * properly.
     */
    @Parameter(defaultValue="false")
    private boolean stripWhitespaceX;

    /**
     * If true, blank pixels on the top and bottom edges of input images will
     * be removed. Applications must take special care to draw these regions
     * properly.
     */
    @Parameter(defaultValue="false")
    private boolean stripWhitespaceY;

    /**
     * From 0 to 255. Alpha values below this are treated as zero when
     * whitespace is stripped.
     */
    @Parameter(defaultValue="0")
    private int alphaThreshold;

    /**
     * The minification filter for the texture.
     */
    @Parameter(defaultValue="Nearest")
    private TextureFilter filterMin;

    /**
     * The magnification filter for the texture.
     */
    @Parameter(defaultValue="Nearest")
    private TextureFilter filterMag;

    /**
     * The wrap setting in the x direction for the texture.
     */
    @Parameter(defaultValue="ClampToEdge")
    private TextureWrap wrapX;

    /**
     * The wrap setting in the y direction for the texture.
     */
    @Parameter(defaultValue="ClampToEdge")
    private TextureWrap wrapY;

    /**
     * The format the texture will use in-memory.
     */
    @Parameter(defaultValue="RGBA8888")
    private Format format;

    /**
     * If true, two images that are pixel for pixel the same will only be
     * packed once.
     */
    @Parameter(defaultValue="true")
    private boolean alias;

    /**
     * The image type for output pages, "png" or "jpg".
     */
    @Parameter(defaultValue="png")
    private String outputFormat;

    /**
     * From 0 to 1. The quality setting if outputFormat is "jpg".
     */
    @Parameter(defaultValue="0.9")
    private float jpegQuality;

    /**
     * If true, texture packer won't add regions for completely blank images.
     */
    @Parameter(defaultValue="true")
    private boolean ignoreBlankImages;

    /**
     * If true, the texture packer will not pack as efficiently but will
     * execute much faster.
     */
    @Parameter(defaultValue="false")
    private boolean fast;

    /**
     * If true, lines are drawn on the output pages to show the packed image
     * bounds.
     */
    @Parameter(defaultValue="false")
    private boolean debug;

    /**
     * If true, the directory containing the settings file and all
     * subdirectories are packed as if they were in the same directory. Any
     * settings files in the subdirectories are ignored.
     */
    @Parameter(defaultValue="false")
    private boolean combineSubdirectories;

    /**
     * If true, only image file names are used in the atlas, any subdirectories
     * are not used.
     */
    @Parameter(defaultValue="false")
    private boolean flattenPaths;

    /**
     * If true, the RGB will be multiplied by the alpha.
     */
    @Parameter(defaultValue="false")
    private boolean premultiplyAlpha;

    /**
     * If false, image names are used without stripping any image index suffix.
     */
    @Parameter(defaultValue="true")
    private boolean useIndexes;

    /**
     * If true, RGB values for transparent pixels are set based on the RGB
     * values of the nearest non-transparent pixels. This prevents filtering
     * artifacts when RGB values are sampled for transparent pixels.
     */
    @Parameter(defaultValue="true")
    private boolean bleed;

    public void execute() throws MojoExecutionException, MojoFailureException {

        Settings settings = new Settings();
        settings.pot = this.pot;
        settings.paddingX = this.paddingX;
        settings.paddingY = this.paddingY;
        settings.edgePadding = this.edgePadding;
        settings.duplicatePadding = this.duplicatePadding;
        settings.rotation = this.rotation;
        settings.minWidth = this.minWidth;
        settings.minHeight = this.minHeight;
        settings.maxWidth = this.maxWidth;
        settings.maxHeight = this.maxHeight;
        settings.forceSquareOutput = this.forceSquareOutput;
        settings.stripWhitespaceX = this.stripWhitespaceX;
        settings.alphaThreshold = this.alphaThreshold;
        settings.filterMin = this.filterMin;
        settings.filterMag = this.filterMag;
        settings.wrapX = this.wrapX;
        settings.wrapY = this.wrapY;
        settings.format = this.format;
        settings.alias = this.alias;
        settings.outputFormat = this.outputFormat;
        settings.jpegQuality = this.jpegQuality;
        settings.ignoreBlankImages = this.ignoreBlankImages;
        settings.fast = this.fast;
        settings.debug = this.debug;
        settings.combineSubdirectories = this.combineSubdirectories;
        settings.flattenPaths = this.flattenPaths;
        settings.premultiplyAlpha = this.premultiplyAlpha;
        settings.useIndexes = this.useIndexes;
        settings.bleed = this.bleed;

        try {
            TexturePackerFileProcessor processor =
                new TexturePackerFileProcessor(settings, this.packFileName);
            processor.process(this.inputDir, this.outputDir);
        } catch (Exception e) {
            buildContext.addMessage(this.inputDir, 0, 0,
                    "Texture packing failed", BuildContext.SEVERITY_ERROR, e);
            throw new MojoFailureException("Texture packing failed", e);
        } finally {
            buildContext.refresh(this.outputDir);
        }
    }

}

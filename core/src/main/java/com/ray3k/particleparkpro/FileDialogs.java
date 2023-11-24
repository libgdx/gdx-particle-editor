package com.ray3k.particleparkpro;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils;
import com.badlogic.gdx.utils.Array;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

/**
 * A convenience class that provides static methods to open OS native file dialogs.
 */
public class FileDialogs {
    public static Array<FileHandle> openMultipleDialog(String title, String defaultPath, String[] filterPatterns, String filterDescription) {
        //fix file path characters
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\");
            if (!defaultPath.endsWith("\\")) defaultPath += "\\";
        } else {
            defaultPath = defaultPath.replace("\\", "/");
            if (!defaultPath.endsWith("/")) defaultPath += "/";
        }

        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer filtersPointerBuffer = null;
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.length);

            for (int i = 0; i < filterPatterns.length; i++) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]));
            }
            filtersPointerBuffer.flip();
        }

        var response = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription, true);

        if (response == null) return null;

        var strings = response.split("\\|");
        var returnValue = new Array<FileHandle>();
        for (var string : strings) {
            returnValue.add(Gdx.files.absolute(string));
        }

        return returnValue;
    }

    public static FileHandle openDialog(String title, String defaultPath, String[] filterPatterns, String filterDescription) {
        //fix file path characters
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\");
            if (!defaultPath.endsWith("\\")) defaultPath += "\\";
        } else {
            defaultPath = defaultPath.replace("\\", "/");
            if (!defaultPath.endsWith("/")) defaultPath += "/";
        }

        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer filtersPointerBuffer = null;
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.length);

            for (int i = 0; i < filterPatterns.length; i++) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]));
            }
            filtersPointerBuffer.flip();
        }

        var response = TinyFileDialogs.tinyfd_openFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription, true);

        if (response == null) return null;

        return Gdx.files.absolute(response);
    }

    public static FileHandle saveDialog(String title, String defaultPath, String defaultName, String[] filterPatterns, String filterDescription) {
        //fix file path characters
        if (UIUtils.isWindows) {
            defaultPath = defaultPath.replace("/", "\\");
            if (!defaultPath.endsWith("\\")) defaultPath += "\\";
        } else {
            defaultPath = defaultPath.replace("\\", "/");
            if (!defaultPath.endsWith("/")) defaultPath += "/";
        }

        MemoryStack stack = MemoryStack.stackPush();

        PointerBuffer filtersPointerBuffer = null;
        if (filterPatterns != null) {
            filtersPointerBuffer = stack.mallocPointer(filterPatterns.length);

            for (int i = 0; i < filterPatterns.length; i++) {
                filtersPointerBuffer.put(stack.UTF8("*." + filterPatterns[i]));
            }
            filtersPointerBuffer.flip();
        }

        var response = TinyFileDialogs.tinyfd_saveFileDialog(title, defaultPath, filtersPointerBuffer, filterDescription);

        if (response == null) return null;

        return Gdx.files.absolute(response);
    }
}

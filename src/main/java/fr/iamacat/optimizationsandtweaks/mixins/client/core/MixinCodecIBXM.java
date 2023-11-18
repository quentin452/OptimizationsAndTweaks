package fr.iamacat.optimizationsandtweaks.mixins.client.core;

import ibxm.IBXM;
import ibxm.Module;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.codecs.CodecIBXM;

import javax.sound.sampled.AudioFormat;
import java.util.Arrays;

@Mixin(CodecIBXM.class)
public abstract class MixinCodecIBXM  implements ICodec {
    /**
     * Used when a parameter for one of the synchronized boolean-interface methods
     * is not aplicable.
     */
    @Shadow
    private static final boolean XXX = false;
    /**
     * Format the converted audio will be in.
     */
    @Shadow
    private AudioFormat myAudioFormat = null;
    /**
     * Used to return a current value from one of the synchronized
     * boolean-interface methods.
     */
    @Shadow
    private static final boolean GET = false;

    /**
     * Used to set the value in one of the synchronized boolean-interface methods.
     */
    @Shadow
    private static final boolean SET = true;

/**
 * Processes status messages, warnings, and error messages.
 */
@Shadow
    private SoundSystemLogger logger;

    @Shadow
    /**
     * Prints an error message.
     * @param message Message to print.
     */
    private void errorMessage( String message )
    {
        logger.errorMessage( "CodecWav", message, 0 );
    }

    /**
     * Module instance to be played.
     */
    @Shadow
    private Module module;

    /**
     * True if there is no more data to read in.
     */
    @Shadow
    private boolean endOfStream = false;
    /**
     * True if the using library requires data read by this codec to be
     * reverse-ordered before returning it from methods read() and readAll().
     */
    @Shadow
    private boolean reverseBytes = false;

    /**
     * Duration of the audio (in frames).
     */
    @Shadow
    private int songDuration;

    /**
     * Audio read position (in frames).
     */
    @Shadow
    private int playPosition;

    /**
     * IBXM decoder.
     */
    @Shadow
    private IBXM ibxm;

    /**
     * @author
     * @reason
     */
    @Overwrite
    private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int length) {
        if (arrayOne == null && arrayTwo == null) {
            return new byte[0];
        } else if (arrayOne == null) {
            return Arrays.copyOf(arrayTwo, length);
        } else if (arrayTwo == null) {
            return Arrays.copyOf(arrayOne, arrayOne.length);
        } else {
            byte[] newArray = Arrays.copyOf(arrayOne, arrayOne.length + length);
            System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, length);
            return newArray;
        }
    }

    /**
     * @author
     * @reason
     */
    @Override
    public SoundBuffer readAll() {
        if (module == null) {
            errorMessage("Module null in method 'readAll'");
            return null;
        }
        if (myAudioFormat == null) {
            errorMessage("Audio Format null in method 'readAll'");
            return null;
        }

        int bufferFrameSize = SoundSystemConfig.getFileChunkSize() / 4;
        byte[] outputBuffer = new byte[bufferFrameSize * 4];
        byte[] fullBuffer = null;
        int frames;
        int totalBytes = 0;

        while (!endOfStream(GET, XXX) && (totalBytes < SoundSystemConfig.getMaxFileSize())) {
            frames = Math.min(songDuration - playPosition, bufferFrameSize);
            ibxm.get_audio(outputBuffer, frames);
            totalBytes += frames * 4;
            fullBuffer = appendByteArrays(fullBuffer, outputBuffer, frames * 4);
            playPosition += frames;
            if (playPosition >= songDuration) {
                endOfStream(SET, true);
            }
        }

        if (reverseBytes) {
            reverseBytes(fullBuffer, 0, totalBytes);
        }

        return new SoundBuffer(fullBuffer, myAudioFormat);
    }

    @Shadow
    private synchronized boolean endOfStream(boolean action, boolean value) {
        if (action == SET) {
            endOfStream = value;
        }
        return endOfStream;
    }

    @Shadow
    public static void reverseBytes(byte[] buffer, int offset, int size) {
        byte b;
        for (int i = offset; i < offset + size; i += 2) {
            b = buffer[i];
            buffer[i] = buffer[i + 1];
            buffer[i + 1] = b;
        }
    }
}

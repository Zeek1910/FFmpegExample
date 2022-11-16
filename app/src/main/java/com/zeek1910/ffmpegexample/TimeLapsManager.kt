package com.zeek1910.ffmpegexample

import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import timber.log.Timber
import java.io.File

class TimeLapsManager() {

    suspend fun createTimeLaps(
        inputFiles: List<File>,
        outputFile: File,
        params: Params = Params(),
        progressListener: ((Int) -> Unit)? = null
    ): TimeLapsResult {
        val stringBuilder = StringBuilder()
        for (file in inputFiles) {
            stringBuilder.append(
                " -framerate ${params.frameRate} -t ${params.imageDuration}" +
                        " -loop 1 -i ${file.absolutePath}"
            )
        }
        stringBuilder.append(" -filter_complex ")
        for (i in inputFiles.indices) {
            stringBuilder.append(
                "[$i:v]scale=${params.width}:${params.height}:force_original_aspect_ratio=increase," +
                        "crop=${params.width}:${params.height}," +
//                        "pad=${params.width}:${params.height}:(ow-iw)/2:(oh-ih)/2," +
                        "setsar=1[v$i];"
            )
        }
        for (i in 0 until inputFiles.size - 1) {
            stringBuilder.append(
                "[v${i + 1}]fade=d=1:t=in:alpha=1,setpts=PTS-STARTPTS+${(i + 1) * (params.imageDuration - 1)}/TB[f$i];"
            )
        }
        stringBuilder.append("[v0][f0]overlay[bg1];")
        for (i in 1 until inputFiles.size - 2) {
            stringBuilder.append("[bg$i][f$i]overlay[bg${i + 1}];")
        }
        stringBuilder.append(
            "[bg${inputFiles.size - 2}][f${inputFiles.size - 2}]overlay," +
                    "format=yuv420p[v] -map [v] ${outputFile.absolutePath}"
        )
        Timber.i("FFmpeg command: $stringBuilder")
        progressListener?.let {
            Config.enableStatisticsCallback { statistics ->
                it.invoke(((statistics.time / (inputFiles.size * params.imageDuration * 1000f)) * 100).toInt())
            }
        }
        return when (FFmpeg.execute(stringBuilder.toString())) {
            Config.RETURN_CODE_SUCCESS -> TimeLapsResult.Success
            Config.RETURN_CODE_CANCEL -> TimeLapsResult.Cancel
            else -> TimeLapsResult.Error
        }
    }

    sealed class TimeLapsResult {
        object Success : TimeLapsResult()
        object Cancel : TimeLapsResult()
        object Error : TimeLapsResult()
    }

    data class Params(
        val imageDuration: Int = DEFAULT_IMAGE_DURATION,
        val frameRate: Int = DEFAULT_FRAME_RATE,
        val width: Int = DEFAULT_VIDEO_WIDTH,
        val height: Int = DEFAULT_VIDEO_HEIGHT
    ) {
        companion object {
            private const val DEFAULT_IMAGE_DURATION = 1
            private const val DEFAULT_FRAME_RATE = 25
            private const val DEFAULT_VIDEO_WIDTH = 1920
            private const val DEFAULT_VIDEO_HEIGHT = 1440
        }
    }
}
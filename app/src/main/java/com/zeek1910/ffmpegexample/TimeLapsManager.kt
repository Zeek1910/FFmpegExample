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
                "[$i:v]scale=${params.width}:${params.height}:force_original_aspect_ratio=decrease," +
                        "pad=${params.width}:${params.height}:(ow-iw)/2:(oh-ih)/2,setsar=1[v$i];"
            )
        }
        for (i in inputFiles.indices) {
            stringBuilder.append("[v$i]")
        }
        stringBuilder.append(
            "concat=n=${inputFiles.size}:v=1:a=0,setsar=1[v] -map [v] -pix_fmt yuv420p ${outputFile.absolutePath}"
        )
        Timber.d("FFmpeg command: $stringBuilder")
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
    )

    companion object {
        private const val DEFAULT_IMAGE_DURATION = 1
        private const val DEFAULT_FRAME_RATE = 25
        private const val DEFAULT_VIDEO_WIDTH = 1920
        private const val DEFAULT_VIDEO_HEIGHT = 1440
    }
}
package com.opkix.base.ffmpeg.di.modules

import com.opkix.base.ffmpeg.FFmpegWrapper
import org.koin.dsl.module

val ffmpegModule = module {

	single { FFmpegWrapper() }
}

import com.soywiz.korim.bitmap.*
import com.soywiz.korim.format.*
import com.soywiz.korio.*
import com.soywiz.korio.file.*
import com.soywiz.korio.file.std.*
import kotlinx.coroutines.flow.*
import kotlin.jvm.*
import kotlin.system.*

object CheckReferences {
    @JvmStatic
    fun main(args: Array<String>) = Korio {
        val references = localCurrentDirVfs["references"]
        var notSimilarCount = 0
        for (kind in listOf("jvm", "mingwx64", "linuxx64", "macosx64")) {
            val generatedVfs = localCurrentDirVfs["build/screenshots/$kind"]
            val exists = generatedVfs.exists()
            println("generatedVfs=$generatedVfs . exists=${exists}")

            data class Result(val similarPixelPerfect: Boolean, val equals: Boolean, val psnr: Double) {
                val similar get() = psnr >= 40.0
            }

            if (exists) {
                val pngfiles = references.list().filter { it.extensionLC == "png" }.toList()
                pngfiles.filter { !it.baseName.contains(".alt") }.forEach { file ->
                    val files = pngfiles.filter { it.baseName.substringBefore('.') == file.baseName.substringBefore('.') }
                    val otherFile = generatedVfs[file.baseName]
                    //println(otherFilesUnfiltered)
                    println("Comparing ${otherFile.absolutePath} <-> ${files.map { it.absolutePath }}")
                    val bitmap1List = files.map { it.readBitmap().toBMP32() }
                    val bitmap2 = otherFile.readBitmap().toBMP32()

                    val results = bitmap1List.map { bitmap1 ->
                        val similarPixelPerfect = Bitmap32.matches(bitmap1, bitmap2, threshold = 32)
                        val equals = bitmap1.contentEquals(bitmap2)
                        val psnr = Bitmap32.computePsnr(bitmap1, bitmap2)
                        Result(similarPixelPerfect, equals, psnr)
                    }
                    println(" --> equals=${results.map { it.equals }}, similarPixelPerfect=${results.map { it.similarPixelPerfect }}, similar=${results.map { it.similar }}, psnr=${results.map { it.psnr }}")
                    if (!results.any { it.similar }) notSimilarCount++
                }
            }
        }

        println("Exiting with... exitCode=$notSimilarCount")
        exitProcess(notSimilarCount)
    }
}


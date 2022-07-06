package com.shadowings.apodcompose.deps

import android.util.Log
import java.io.*

/**
 * Handles read and write from the cache
 */
class CacheManager {

    /**
     * Returns the content of the file named [key] in the given [cacheDir]
     * Returns null if the file doesn't exists
     */
    fun readFromCache(cacheDir: File, key: String): String? {
        return try {
            val file = File(cacheDir, key)
            val fileInputStream = FileInputStream(file)
            getFileContent(fileInputStream)
        } catch (e: FileNotFoundException) {
            Log.d("apod", e.toString())
            null
        }
    }

    /**
     * Writes the [content] to the [key] file in che [cacheDir] directory
     */
    fun writeToCache(cacheDir: File, key: String, content: String) {
        val file = File(cacheDir, key)
        file.createNewFile()
        val fw = FileWriter(file.absoluteFile)
        val bw = BufferedWriter(fw)
        bw.write(content)
        bw.close()
    }

    private fun getFileContent(
        fis: FileInputStream
    ): String {
        BufferedReader(InputStreamReader(fis, "UTF-8")).use { br ->
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
                sb.append('\n')
            }
            return sb.toString()
        }
    }
}
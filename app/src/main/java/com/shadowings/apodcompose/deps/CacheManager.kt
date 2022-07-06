package com.shadowings.apodcompose.deps

import java.io.*

class CacheManager {

    fun readFromCache(cacheDir: File, key: String): String? {
        return try {
            val file = File(cacheDir, key)
            val fileInputStream = FileInputStream(file)
            getFileContent(fileInputStream)
        } catch (e: FileNotFoundException) {
            null
        }
    }

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
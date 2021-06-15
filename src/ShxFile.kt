import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShxFile(private val path : String, private val header : ByteBuffer) {
    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel

    fun generateShxFile(){
        val shxBuffer  = ByteBuffer.allocateDirect(100 + 8)
        val mappedByteBuffer : MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 108L)
        try {
            shxBuffer.position(0)
            shxBuffer.put(header)
            shxBuffer.putInt(50)
            shxBuffer.putInt(48)
            shxBuffer.position(0)
            mappedByteBuffer.put(shxBuffer)
            fileChannel.write(mappedByteBuffer)
        }finally {
            mappedByteBuffer.clear()
            fileChannel.close()
            randomAccessFile.close()
            shxBuffer.clear()
            header?.clear()
        }
    }
}
package generateFile

import shapeType.BoundingBox
import shapeType.PolyLine
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShxFile(path : String) {
    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel
    private val header by lazy {
        CommonFunction.generateHeader(54, ByteBuffer.allocateDirect(100), polyLine!!)
    }
    private var polyLine : PolyLine?=null

    fun generateShxFile(polyLine : PolyLine){
        this.polyLine = polyLine
        val size = 44 +(4 * polyLine.Parts.size) + (16 * polyLine.Point.size)

        val shxBuffer  = ByteBuffer.allocateDirect(100 + 8)
        val mappedByteBuffer : MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 108L)
        try {
            shxBuffer.position(0)
            shxBuffer.put(header)
            shxBuffer.putInt(50)
            shxBuffer.putInt(size/2)
            shxBuffer.position(0)
            mappedByteBuffer.put(shxBuffer)
            fileChannel.write(mappedByteBuffer)
        }finally {
            mappedByteBuffer.clear()
            fileChannel.close()
            randomAccessFile.close()
            shxBuffer.clear()
            header.clear()
        }
    }

}
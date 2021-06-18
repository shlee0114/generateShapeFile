package generateFile

import shapeType.BoundingBox
import shapeType.Point
import shapeType.PolyLine
import shapeType.ShapeType
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShxFile(path : String) {

    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel
    private var header : ByteBuffer? = null

    private var shapeType = ShapeType.None
    private var polyLine : PolyLine?=null
    private var point : Point?=null

    fun generateShxFile(polyLine : PolyLine){
        header = CommonFunction.generateHeader(54, ByteBuffer.allocateDirect(100), polyLine)
        this.polyLine = polyLine
        val size = 44 +(4 * polyLine.Parts.size) + (16 * polyLine.Point.size)
        generateShxFile(size)
    }

    fun generateShxFile(point : Point){
        header = CommonFunction.generateHeader(54, ByteBuffer.allocateDirect(100), point)
        this.point = point
        val size = 20
        generateShxFile(size)
    }

    private fun generateShxFile(size : Int){

        val shxBuffer  = ByteBuffer.allocateDirect(100 + 8)
        val mappedByteBuffer : MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 108L)
        try {
            shxBuffer.position(0)
            shxBuffer.put(header!!)
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
            header!!.clear()
        }
    }
}
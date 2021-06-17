package generateFile

import shapeType.BoundingBox
import shapeType.PolyLine
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShpFile(path : String, private val boundingBox: BoundingBox) {
    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel

    private var size = 0

    private var polyLine : PolyLine?=null

    private val header by lazy {
        CommonFunction.generateHeader((100 + size) / 2, ByteBuffer.allocateDirect(100), polyLine!!)
    }

    private val coordinate by lazy {
        ByteBuffer.allocateDirect(100 + size)
    }

    fun generateShpFile(polyLine : PolyLine){
        this.polyLine = polyLine
        size = 44 + 8 +(4 * polyLine.Parts.size) + (16 * polyLine.Point.size)

        generateCoordinate()
        writeShpFile()
    }

    private fun generateCoordinate(){
        coordinate.put(header)
        coordinate.order(ByteOrder.BIG_ENDIAN)
        coordinate.putInt(1)
        coordinate.putInt(size)
        coordinate.order(ByteOrder.LITTLE_ENDIAN)
        coordinate.putInt(polyLine!!.ShapeType)
        coordinate.putDouble(polyLine!!.Box.minX)
        coordinate.putDouble(polyLine!!.Box.minY)
        coordinate.putDouble(polyLine!!.Box.maxX)
        coordinate.putDouble(polyLine!!.Box.maxY)
        coordinate.putInt(polyLine!!.NumParts)
        coordinate.putInt(polyLine!!.NumPoints)
        coordinate.putInt(0)
        for(i in polyLine!!.Point){
            coordinate.putDouble(i.x)
            coordinate.putDouble(i.y)
        }
    }

    private fun writeShpFile(){
        val mappedByteBuffer : MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,100L + size)
        try {
            coordinate.position(0)

            mappedByteBuffer.put(coordinate)
            fileChannel.write(mappedByteBuffer)
        }finally {
            mappedByteBuffer.clear()
            fileChannel.close()
            randomAccessFile.close()
            coordinate.clear()
            header.clear()
        }
    }
}
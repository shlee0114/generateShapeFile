package generateFile

import shapeType.Point
import shapeType.PolyLine
import shapeType.ShapeType
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShpFile(path : String) {
    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel

    private var size = 0

    private var polyLine : PolyLine?=null
    private var point : Point?=null

    private var header : ByteBuffer? = null

    private val coordinate by lazy {
        ByteBuffer.allocateDirect(100 + size + 8)
    }

    fun generateShpFile(polyLine : PolyLine){
        header = CommonFunction.generateHeader((100 + size) / 2, ByteBuffer.allocateDirect(100), polyLine)
        this.polyLine = polyLine
        size = 44 +(4 * polyLine.Parts.size) + (16 * polyLine.Point.size)

        generateCoordinate(ShapeType.PolyLine)
        writeShpFile()
    }

    fun generateShpFile(point : Point){
        header = CommonFunction.generateHeader((100 + size) / 2, ByteBuffer.allocateDirect(100), point)
        this.point = point
        size = 20

        generateCoordinate(ShapeType.Point)
        writeShpFile()
    }

    private fun generateCoordinate(shapeType : ShapeType){
        coordinate.put(header!!)
        coordinate.order(ByteOrder.BIG_ENDIAN)
        coordinate.putInt(1)
        coordinate.putInt(size/2)
        coordinate.order(ByteOrder.LITTLE_ENDIAN)
        coordinate.putInt(shapeType.type)
        when(shapeType){
            ShapeType.Point ->  generatePointCoordinate()
            ShapeType.PolyLine -> generatePolyLineCoordinate()
        }
    }

    private fun generatePolyLineCoordinate(){
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

    private fun generatePointCoordinate(){
        coordinate.putDouble(point!!.x)
        coordinate.putDouble(point!!.y)
    }

    private fun writeShpFile(){
        val mappedByteBuffer : MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,100L + size + 8)
        try {
            coordinate.position(0)

            mappedByteBuffer.put(coordinate)
            fileChannel.write(mappedByteBuffer)
        }finally {
            mappedByteBuffer.clear()
            fileChannel.close()
            randomAccessFile.close()
            coordinate.clear()
            header!!.clear()
        }
    }
}
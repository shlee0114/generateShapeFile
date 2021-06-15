import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class ShpFile(path : String, private val shapeType : Int, private val boundingBox: BoundingBox) {
    private val randomAccessFile : RandomAccessFile = RandomAccessFile(File(path), "rw")
    private var fileChannel : FileChannel = randomAccessFile.channel

    private val SHP_FILE_CODE = 0x0000270a
    private val SHP_FILE_LENGTH by lazy {
        (100 + size) / 2
    }
    private val SHP_FILE_VERSION = 1000

    private var size = 0

    private var polyLine : PolyLine?=null

    private var header : ByteBuffer? = null
    private val coordinate by lazy {
        ByteBuffer.allocateDirect(100 + size)
    }

    fun generateShpFile(polyLine : PolyLine){
        this.polyLine = polyLine
        size = 44 + 8 +(4 * polyLine.Parts.size) + (16 * polyLine.Point.size)
        generateHeader()
        generateCoordinate()
        writeShpFile()
    }

    fun generateHeader() : ByteBuffer{
        if(header != null)
            return header!!
        header = ByteBuffer.allocateDirect(100)
        header!!.position(0)
        header!!.order(ByteOrder.BIG_ENDIAN)
        header!!.putInt(SHP_FILE_CODE)
        header!!.position(header!!.position() + 20)
        header!!.putInt(SHP_FILE_LENGTH)
        header!!.order(ByteOrder.LITTLE_ENDIAN)
        header!!.putInt(SHP_FILE_VERSION)
        header!!.putInt(shapeType)
        header!!.putDouble(boundingBox.minX)
        header!!.putDouble(boundingBox.minY)
        header!!.putDouble(boundingBox.maxX)
        header!!.putDouble(boundingBox.maxY)
        header!!.position(0)
        return header!!
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
            header?.clear()
        }
    }
}
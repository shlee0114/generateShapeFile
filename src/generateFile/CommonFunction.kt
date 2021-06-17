package generateFile

import shapeType.BoundingBox
import shapeType.Point
import shapeType.PolyLine
import java.nio.ByteBuffer
import java.nio.ByteOrder

class CommonFunction {
    companion object{
        private val SHAPE_FILE_CODE = 0x0000270a
        private val SHAPE_FILE_VERSION = 1000

        fun generateHeader(fileSize : Int, header : ByteBuffer, polyLine: PolyLine) : ByteBuffer{
            header.position(0)
            header.order(ByteOrder.BIG_ENDIAN)
            header.putInt(SHAPE_FILE_CODE)
            header.position(header.position() + 20)
            header.putInt(fileSize)
            header.order(ByteOrder.LITTLE_ENDIAN)
            header.putInt(SHAPE_FILE_VERSION)
            header.putInt(polyLine.ShapeType)
            header.putDouble(polyLine.Box.minX)
            header.putDouble(polyLine.Box.minY)
            header.putDouble(polyLine.Box.maxX)
            header.putDouble(polyLine.Box.maxY)
            header.position(0)
            return header
        }

        fun generateBoundingBox(point: Array<Point>) : BoundingBox {
            var minX = 99999999999.0
            var maxX = -99999999999.0
            var minY = 99999999999.0
            var maxY = -99999999999.0
            for(i in point){
                if(i.x > maxX)
                    maxX = i.x
                if(i.x < minX)
                    minX = i.x
                if(i.y > maxY)
                    maxY = i.y
                if(i.y < minY)
                    minY = i.y
            }
            return BoundingBox(minX, maxX, minY, maxY)
        }
    }
}
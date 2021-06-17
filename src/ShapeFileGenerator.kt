import generateFile.ShpFile
import generateFile.ShxFile
import shapeType.BoundingBox
import shapeType.Point
import shapeType.PolyLine
import shapeType.ShapeType
import java.io.File

class ShapeFileGenerator(private val shapeType : ShapeType, private val polyLine : PolyLine) {

    private val boundingBox : BoundingBox by lazy {
        generateBoundingBox()
    }

    fun generate(filePath : String, fileName : String){
        val shxPath = "$filePath\\$fileName.shx"
        val shpPath = "$filePath\\$fileName.shp"
        deleteFileWhenExists(File(shpPath))
        deleteFileWhenExists(File(shxPath))

        val shp = ShpFile(shpPath,boundingBox)
        shp.generateShpFile(polyLine)
        val shx = ShxFile(shxPath)
        shx.generateShxFile(polyLine)
    }

    private fun generateBoundingBox() : BoundingBox {
        var minX = 0.0
        var maxX = 0.0
        var minY = 0.0
        var maxY = 0.0
        for(i in polyLine.Point){
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

    private fun deleteFileWhenExists(file : File){
        if(file.exists()){
            file.delete()
        }
    }
}

fun main(){
    val shapeFileGenerator = ShapeFileGenerator(
        ShapeType.PolyLine, PolyLine(
            ShapeType.PolyLine.type, BoundingBox(-12.0, 12.0, -12.0, 12.0), 1, 3, intArrayOf(0), arrayListOf(
        Point(-12.0, 12.0), Point(12.0, -12.0), Point(10.0, -5.0)
    ))
    )
    shapeFileGenerator.generate("D:\\test\\te","test")

}
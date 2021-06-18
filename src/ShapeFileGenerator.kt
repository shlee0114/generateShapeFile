import generateFile.ShpFile
import generateFile.ShxFile
import shapeType.BoundingBox
import shapeType.Point
import shapeType.ShapeType
import java.io.File

class ShapeFileGenerator(private val shapeType : ShapeType, private val polyLine : Point) {

    fun generate(filePath : String, fileName : String){
        val shxPath = "$filePath\\$fileName.shx"
        val shpPath = "$filePath\\$fileName.shp"
        deleteFileWhenExists(File(shpPath))
        deleteFileWhenExists(File(shxPath))

        val shp = ShpFile(shpPath)
        shp.generateShpFile(polyLine)
        val shx = ShxFile(shxPath)
        shx.generateShxFile(polyLine)
    }

    private fun deleteFileWhenExists(file : File){
        if(file.exists()){
            file.delete()
        }
    }
}

fun main(){
   // val shapeFileGenerator = ShapeFileGenerator(
   //     ShapeType.PolyLine, PolyLine(
   //         ShapeType.PolyLine.type, BoundingBox(-12.0, 12.0, -12.0, 12.0), 1, 5, intArrayOf(0), arrayListOf(
   //     Point(-12.0, 12.0), Point(12.0, -12.0), Point(10.0, -5.0), Point(-12.0, -12.0), Point(10.0, -5.0)
   // ))
   // )
    val shapeFileGenerator = ShapeFileGenerator(
        ShapeType.Point, Point(12.3, 13.2)
    )
    shapeFileGenerator.generate("D:\\test\\te","test")

}
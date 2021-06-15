import java.io.File

class ShapeFileGenerator {
    fun generate(shpPath : String){
        val fileName = getFileNameNoExt(shpPath)
        val shxPath = getFilePath(shpPath) + "/" + fileName + ".shx"
        deleteFileWhenExists(File(shpPath))
        deleteFileWhenExists(File(shxPath))

        val shp = ShpFile(shpPath, 3, BoundingBox(-12.0, 12.0, -12.0, 12.0))
        shp.generateShpFile(PolyLine(3, BoundingBox(-12.0, 12.0, -12.0, 12.0), 1, 3, intArrayOf(0), arrayListOf(Point(-12.0, 12.0), Point(12.0, -12.0), Point(5.0, -5.0))))
        val shx = ShxFile(shxPath, shp.generateHeader())
        shx.generateShxFile()
    }

    private fun deleteFileWhenExists(file : File){
        if(file.exists()){
            file.delete()
        }
    }

    private fun getFileNameNoExt(pmFilename: String): String {
        var pmFilename = pmFilename
        var lmName = pmFilename
        pmFilename = File(pmFilename).name
        val lmIndex = pmFilename.lastIndexOf(46.toChar())
        if (lmIndex >= 0) {
            lmName = pmFilename.substring(0, lmIndex)
        }
        return lmName
    }

    private fun getFilePath(pmPath: String): String {
        val lmFile = File(pmPath)
        return if (lmFile.isDirectory) lmFile.path else lmFile.parent
    }
}

fun main(){
    val shapeFileGenerator = ShapeFileGenerator()
    shapeFileGenerator.generate("D:\\test\\te\\test.shp")

}
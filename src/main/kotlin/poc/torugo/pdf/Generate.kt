package poc.torugo.pdf

// import com.openhtmltopdf.svgsupport.BatikSVGDrawer

import com.openhtmltopdf.extend.FSSupplier
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder
import org.jboss.resteasy.annotations.jaxrs.FormParam
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm
import org.jboss.resteasy.annotations.providers.multipart.PartType
import java.io.*
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


class Body {
    @FormParam("file")
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    var file: InputStream? = null
}

@Path("/convert/html")
class Generate {
    val producer : String = "afyadigital.maagizo"
    val fontSupplier = Supplier()

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.MULTIPART_FORM_DATA)
    fun hello(@MultipartForm body: Body ): Response {
//        println(inputStreamAsString(fontIS))
        val file = body.file ?: return Response.status(400, "empty file").build()
        val reader = file.bufferedReader()
        val content = StringBuilder()
        reader.use { reader ->
            var line = reader.readLine()
            while (line != null) {
                content.append(line)
                line = reader.readLine()
            }
        }
        try {
            val stream = ByteArrayOutputStream()
            val builder = PdfRendererBuilder()
            builder.useFastMode()
            builder.usePdfUaAccessbility(true)
            builder.usePdfAConformance(PdfRendererBuilder.PdfAConformance.PDFA_3_A)
            // builder.useSVGDrawer(BatikSVGDrawer())
            builder.useFont(fontSupplier, "Arial")
            builder.withHtmlContent(content.toString(), null)
            builder.toStream(stream);
            builder.withProducer(this.producer)
            builder.run()
            val response = Response.ok(stream.toByteArray())
            response.header("Content-Disposition", "attachment;filename=prescription.pdf")
            return response.build()
        } catch (e : Exception) {
            e.printStackTrace()
            return Response.serverError().build()
        }
    }

}

class Supplier : FSSupplier<InputStream?> {
    override fun supply(): InputStream? {
        return try {
            this.javaClass.getResourceAsStream("/arial.ttf")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        }
    }
}
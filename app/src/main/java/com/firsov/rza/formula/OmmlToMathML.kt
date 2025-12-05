package com.firsov.rza.formula

import android.content.Context
import android.util.Log
import java.io.StringReader
import java.io.StringWriter
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

object OmmlToMathML {
    fun convert(context: Context, omml: String): String? {
        return try {
            Log.d("OMML", "START TRANSFORM")
            val factory = TransformerFactory.newInstance()

            val xsltStream = context.assets.open("xslt/OMML2MML.XSL")
            Log.d("OMML", "XSLT LOADED OK")

            val transformer = factory.newTransformer(StreamSource(xsltStream))

            val input = StringReader(omml)
            val output = StringWriter()

            transformer.transform(StreamSource(input), StreamResult(output))

            Log.d("OMML", "TRANSFORM COMPLETE")

            output.toString()
        } catch (e: Exception) {
            Log.e("OMML", "ERROR: ${e.message}", e)
            null
        }
    }
}



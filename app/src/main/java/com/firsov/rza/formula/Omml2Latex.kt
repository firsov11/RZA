package com.firsov.rza.formula

import org.w3c.dom.Element
import org.w3c.dom.Node
import javax.xml.parsers.DocumentBuilderFactory

object Omml2Latex {

    fun convert(ommlXml: String): String? {
        return try {
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val inputStream = ommlXml.byteInputStream()
            val doc = builder.parse(inputStream)
            val root = doc.documentElement
            parseNode(root)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun parseNode(node: Node): String {
        return when (node.nodeType) {
            Node.ELEMENT_NODE -> parseElement(node as Element)
            Node.TEXT_NODE -> node.nodeValue ?: ""
            else -> ""
        }
    }

    private fun parseElement(elem: Element): String {
        return when (elem.tagName) {
            "m:oMath", "m:oMathPara" -> elem.childNodes.toLaTeX()
            "m:f" -> { // дробь
                val num = elem.getElementsByTagName("m:num").item(0)
                val den = elem.getElementsByTagName("m:den").item(0)
                "\\frac{${parseNode(num)}}{${parseNode(den)}}"
            }
            "m:sup" -> { // надстрочный индекс
                val base = elem.getElementsByTagName("m:e").item(0)
                val sup = elem.getElementsByTagName("m:sup").item(0)
                "${parseNode(base)}^{${parseNode(sup)}}"
            }
            "m:sub" -> { // подстрочный индекс
                val base = elem.getElementsByTagName("m:e").item(0)
                val sub = elem.getElementsByTagName("m:sub").item(0)
                "${parseNode(base)}_{${parseNode(sub)}}"
            }
            "m:r" -> elem.getElementsByTagName("m:t").item(0)?.textContent ?: ""
            "m:t" -> elem.textContent ?: ""
            "m:rad" -> { // корень
                val e = elem.getElementsByTagName("m:deg").item(0) // индекс корня, если есть
                val radicand = elem.getElementsByTagName("m:e").item(0)
                if (e != null) {
                    "\\sqrt[${parseNode(e)}]{${parseNode(radicand)}}"
                } else {
                    "\\sqrt{${parseNode(radicand)}}"
                }
            }
            else -> elem.childNodes.toLaTeX()
        }
    }

    // расширение для NodeList
    private fun org.w3c.dom.NodeList.toLaTeX(): String {
        val sb = StringBuilder()
        for (i in 0 until this.length) {
            sb.append(parseNode(this.item(i)))
        }
        return sb.toString()
    }
}

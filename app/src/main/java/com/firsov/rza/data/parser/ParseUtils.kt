package com.firsov.rza.data.parser

import com.firsov.rza.data.models.DocxBlock
import com.firsov.rza.data.models.DocxImage
import com.firsov.rza.data.models.DocxTableModel
import com.firsov.rza.data.models.DocxText
import com.firsov.rza.data.models.TableCell
import com.firsov.rza.data.models.TableCellContent
import com.firsov.rza.data.models.TableRow
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import org.apache.poi.xwpf.usermodel.XWPFTableRow

fun parseParagraphInline(p: XWPFParagraph): List<DocxBlock> {
    val result = mutableListOf<DocxBlock>()
    val textBuffer = StringBuilder()

    fun flushText() {
        if (textBuffer.isNotEmpty()) {
            result += DocxText(textBuffer.toString())
            textBuffer.clear()
        }
    }

    p.runs.forEach { run ->
        val pics = run.embeddedPictures

        if (pics.isNotEmpty()) {
            flushText()
            pics.forEach { pic ->
                result += DocxImage(bytes = pic.pictureData.data)
            }
        } else {
            run.text()?.let { textBuffer.append(it) }
        }
    }

    flushText()
    return result
}

fun parseTable(table: XWPFTable): DocxTableModel {
    val columnCount = getColumnCount(table)
    val rowCount = table.rows.size

    val grid = Array(rowCount) { arrayOfNulls<TableCell>(columnCount) }
    val verticalMerges = mutableMapOf<Int, Pair<TableCell, Int>>()

    table.rows.forEachIndexed { rowIndex, row ->
        var colIndex = 0
        var cellIndex = 0

        while (colIndex < columnCount) {

            // продолжение вертикального объединения
            val merge = verticalMerges[colIndex]
            if (merge != null && merge.second > 0) {
                grid[rowIndex][colIndex] = merge.first
                verticalMerges[colIndex] = merge.first to (merge.second - 1)
                colIndex++
                continue
            }

            val cell = row.tableCells.getOrNull(cellIndex)
            if (cell == null) {
                colIndex++
                continue
            }

            val colSpan =
                cell.ctTc.tcPr?.gridSpan?.`val`?.toInt() ?: 1

            val rowSpan = getRowSpan(
                table = table,
                startRowIndex = rowIndex,
                startColIndex = colIndex
            )

            val tableCell = TableCell(
                content = parseSingleTableCell(cell),
                colSpan = colSpan,
                rowSpan = rowSpan
            )

            if (rowSpan > 1) {
                repeat(colSpan) {
                    verticalMerges[colIndex + it] =
                        tableCell to (rowSpan - 1)
                }
            }

            for (r in rowIndex until (rowIndex + rowSpan)) {
                for (c in colIndex until (colIndex + colSpan)) {
                    grid[r][c] =
                        if (r == rowIndex && c == colIndex) tableCell else null
                }
            }

            colIndex += colSpan
            cellIndex++
        }
    }

    val rows = grid.map { rowArray ->
        TableRow(cells = rowArray.toList())
    }

    return DocxTableModel(rows, columnCount)
}

fun getColumnCount(table: XWPFTable): Int {
    val grid = table.ctTbl.tblGrid
    return grid?.gridColList?.size
        ?: table.rows.maxOfOrNull { it.tableCells.size }
        ?: 0
}

fun getRowSpan(
    table: XWPFTable,
    startRowIndex: Int,
    startColIndex: Int
): Int {

    val startRow = table.rows[startRowIndex]
    val startCell = startRow.getCellByVisualIndex(startColIndex)
        ?: return 1

    val tcPr = startCell.ctTc.tcPr ?: return 1
    val vMerge = tcPr.vMerge ?: return 1

    // если это не начало объединения — rowSpan = 1
    if (vMerge.`val`?.toString() != "restart") return 1

    var span = 1

    for (rowIndex in startRowIndex + 1 until table.rows.size) {
        val row = table.rows[rowIndex]
        val cell = row.getCellByVisualIndex(startColIndex) ?: break

        val merge = cell.ctTc.tcPr?.vMerge ?: break
        if (merge.`val`?.toString() == "continue") {
            span++
        } else {
            break
        }
    }

    return span
}

fun XWPFTableRow.getCellByVisualIndex(colIndex: Int): XWPFTableCell? {
    var currentCol = 0

    for (cell in tableCells) {
        val span = cell.ctTc.tcPr?.gridSpan?.`val`?.toInt() ?: 1

        if (colIndex in currentCol until (currentCol + span)) {
            return cell
        }

        currentCol += span
    }

    return null
}


fun parseSingleTableCell(cell: XWPFTableCell): TableCellContent {
    val textBuffer = StringBuilder()

    cell.paragraphs.forEach { p ->
        p.runs.forEach { r ->

            val pics = r.embeddedPictures
            if (pics.isNotEmpty()) {
                return TableCellContent.Image(
                    bytes = pics.first().pictureData.data
                )
            }

            r.text()?.let { textBuffer.append(it) }
        }
    }

    return TableCellContent.Text(textBuffer.toString())
}





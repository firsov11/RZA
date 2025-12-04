package com.firsov.rza.formula

import android.content.Context
import android.util.Log
import android.webkit.WebView
import android.widget.FrameLayout
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class FormulaViewWeb @JvmOverloads constructor(
    context: Context
) : FrameLayout(context), CoroutineScope {

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val webView: WebView = WebView(context).apply {
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        settings.javaScriptEnabled = true
    }

    private var currentRenderJob: Job? = null
    private var omml: String? = null

    init {
        addView(webView)
        Log.d("FormulaViewWeb", "WebView initialized")
    }

    fun setOmmlFormula(xml: String?) {
        Log.d("FormulaViewWeb", "setOmmlFormula called")
        omml = xml
        renderFormula()
    }

    private fun renderFormula() {
        Log.d("FormulaViewWeb", "renderFormula called")
        currentRenderJob?.cancel()

        val xml = omml ?: run {
            Log.d("FormulaViewWeb", "OMML is null, clearing WebView")
            webView.loadData("", "text/html", "UTF-8")
            return
        }

        currentRenderJob = launch {
            val latex: String? = withContext(Dispatchers.Default) {
                try {
                    Log.d("FormulaViewWeb", "Converting OMML to LaTeX")
                    Omml2Latex.convert(xml)
                } catch (e: Exception) {
                    Log.e("FormulaViewWeb", "Error converting OMML to LaTeX", e)
                    null
                }
            }

            Log.d("FormulaViewWeb", "Conversion result: $latex")

            val html = if (!latex.isNullOrEmpty()) {
                """
                <html>
                <head>
                  <script type="text/javascript"
                    src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/3.2.2/es5/tex-mml-chtml.js">
                  </script>
                  <style>
                    body { font-size: 18px; text-align: center; margin: 0; padding: 0; }
                  </style>
                </head>
                <body>
                  $$${latex}$$
                </body>
                </html>
                """.trimIndent()
            } else {
                ""
            }

            Log.d("FormulaViewWeb", "Loading HTML into WebView")
            webView.loadDataWithBaseURL("about:blank", html, "text/html", "UTF-8", null)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
        Log.d("FormulaViewWeb", "onDetachedFromWindow called, job cancelled")
    }
}

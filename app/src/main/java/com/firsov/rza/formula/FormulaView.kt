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

    private val webView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    }

    private var currentJob: Job? = null
    private var omml: String? = null

    init {
        addView(webView)
    }

    fun setOmmlFormula(xml: String?) {
        omml = xml
        renderFormula()
    }

    private fun renderFormula() {
        currentJob?.cancel()

        val xml = omml ?: return webView.loadData("", "text/html", "UTF-8")

        currentJob = launch {
            val mathML = withContext(Dispatchers.Default) {
                OmmlToMathML.convert(context, xml)
            }

            Log.d("OMML", "MathML output: $mathML")

            if (mathML.isNullOrBlank()) {
                webView.loadData("", "text/html", "UTF-8")
                return@launch
            }

            val html = """
                <html>
                <head>
                  <meta charset="utf-8"/>
                  <script src="https://cdnjs.cloudflare.com/ajax/libs/mathjax/3.2.2/es5/tex-mml-chtml.js"></script>
                  <style>
                    body {
                      margin: 0;
                      padding: 0;
                      font-size: 18px;
                      display: flex;
                      justify-content: center;
                    }
                  </style>
                </head>
                <body>
                  $mathML
                  <script>
                    if (window.MathJax) {
                        MathJax.typeset();
                    }
                  </script>
                </body>
                </html>
            """.trimIndent()

            webView.loadDataWithBaseURL("about:blank", html, "text/html", "UTF-8", null)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job.cancel()
    }
}

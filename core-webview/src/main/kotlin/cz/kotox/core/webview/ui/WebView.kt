package cz.kotox.core.webview.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lightbrite.android.soulvibe.webview.databinding.WebViewActivityBinding
import cz.kotox.core.webview.R
import timber.log.Timber

fun Fragment.showWeb(url: String) = startActivity(WebViewActivity.newIntent(requireContext(), url))

private const val ARG_URL = "url"

class WebViewActivity : AppCompatActivity() {

	companion object {

		fun newIntent(context: Context, url: String) = Intent(context, WebViewActivity::class.java).apply {
			putExtra(ARG_URL, url)
		}
	}

	private lateinit var binding: WebViewActivityBinding

	private val webClient = object : WebViewClient() {

		override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
			super.onPageStarted(view, url, favicon)
			binding.progress.visibility = View.VISIBLE
		}

		override fun onPageFinished(view: WebView?, url: String?) {
			super.onPageFinished(view, url)
			binding.progress.visibility = View.GONE
		}

		override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
			view.loadUrl(url)
			return true
		}

		@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
		override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
			view.loadUrl(request.url.toString())
			return true
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		binding = DataBindingUtil.inflate(layoutInflater, R.layout.web_view_activity, null, false)
		setContentView(binding.root)

		setupToolbar()

		setupWebView()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean =
		when (item.itemId) {
			android.R.id.home -> {
				onBackPressed()
				true
			}
			else -> super.onOptionsItemSelected(item)
		}

	private fun setupWebView() {
		binding.progress.visibility = View.VISIBLE
		binding.webview.apply {
			webViewClient = webClient
//			settings.javaScriptEnabled = true
			val url: String? = intent.getStringExtra(ARG_URL)
			if (url != null) {
				loadUrl(url)
			} else {
				Timber.d(IllegalStateException("Unable to load null url for webView!"))
				finish()
			}
		}
	}

	private fun setupToolbar() {
		setSupportActionBar(binding.toolbar)
		binding.toolbar.navigationIcon =
			AppCompatResources.getDrawable(this@WebViewActivity, R.drawable.abc_ic_ab_back_material)?.apply {
				DrawableCompat.setTint(this, ContextCompat.getColor(this@WebViewActivity, /*R.color.color_on_background - temporarily commented out until styles/colors will be refactored*/R.color.global_primary ))
			}
	}

}
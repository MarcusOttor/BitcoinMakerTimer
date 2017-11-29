package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.advertisements

import android.content.Context
import android.content.Intent
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.analytics.Analytics
import com.fyber.requesters.RequestError
import com.fyber.ads.AdFormat
import com.fyber.requesters.InterstitialRequester
import com.fyber.requesters.RequestCallback
import kotlin.concurrent.thread

class FyberInterstitial(private var context: Context) {

    private var interstitialIntent: Intent? = null
    private var requestCallback: RequestCallback? = null

    init {
        requestCallback = object : RequestCallback {
            override fun onAdAvailable(intent: Intent) {
                interstitialIntent = intent
                println("INTERSTITIAL_SUCCESS")
            }

            override fun onAdNotAvailable(adFormat: AdFormat) {
                interstitialIntent = null
                println("INTERSTITIAL_NOT_AVAILABLE")
                request()
            }

            override fun onRequestError(requestError: RequestError) {
                interstitialIntent = null
                println("INTERSTITIAL_ERROR")
            }
        }
        request()
    }

    private fun request() {
        thread {
            Thread.sleep(3000)
            try {
                InterstitialRequester.create(requestCallback !!)
                        .request(context)
            } catch (ex: Exception) { }
        }
    }

    fun show(): Boolean {
        if (interstitialIntent != null) {
            Analytics.report(Analytics.INTERSTITIAL, Analytics.FYBER, Analytics.OPEN)
            context.startActivity(interstitialIntent)
            return true
        }
        return false
    }
}
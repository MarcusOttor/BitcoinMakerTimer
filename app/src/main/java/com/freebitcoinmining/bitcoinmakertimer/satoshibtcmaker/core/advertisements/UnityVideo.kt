package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.advertisements

import android.app.Activity
import android.widget.TextView
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.analytics.Analytics
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.CoinsManager
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.PreferencesManager
import com.unity3d.ads.IUnityAdsListener
import com.unity3d.ads.UnityAds

class UnityVideo(private var preferencesManager: PreferencesManager,
                 private var coinsManager: CoinsManager) : IUnityAdsListener {

    private lateinit var coinsText: TextView

    fun init(activity: Activity) {
        UnityAds.initialize(activity, "1597116", this)
    }

    override fun onUnityAdsStart(p0: String?) {

    }

    override fun onUnityAdsFinish(p0: String?, p1: UnityAds.FinishState?) {
        if (!preferencesManager.get(PreferencesManager.ADDITIONAL_LIFE, false)) {
            coinsManager.addCoins(200)
            Analytics.report(Analytics.VIDEO, Analytics.UNITY, Analytics.REWARD)
        } else {
            preferencesManager.put(PreferencesManager.ADDITIONAL_LIFE, false)
            preferencesManager.put(PreferencesManager.TICKETS_LIFES, 1)
            Analytics.report(Analytics.VIDEO, Analytics.UNITY, Analytics.GAME_BOOST)
        }
        try {
            coinsText.text = coinsManager.getCoins().toString()
        } catch (ex: Exception) {}
    }

    override fun onUnityAdsError(p0: UnityAds.UnityAdsError?, p1: String?) {

    }

    override fun onUnityAdsReady(p0: String?) {

    }

    fun showVideo(activity: Activity, coinsText: TextView) : Boolean {
        this.coinsText = coinsText
        if (UnityAds.isReady("rewardedVideo")) {
            UnityAds.show(activity, "rewardedVideo")
            Analytics.report(Analytics.VIDEO, Analytics.UNITY, Analytics.OPEN)
            return true
        }
        return false
    }

}

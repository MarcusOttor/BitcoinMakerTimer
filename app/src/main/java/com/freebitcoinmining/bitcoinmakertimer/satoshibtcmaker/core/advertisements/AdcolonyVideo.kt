package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.advertisements

import android.app.Activity
import android.widget.TextView
import com.adcolony.sdk.AdColony
import com.adcolony.sdk.AdColonyInterstitial
import com.adcolony.sdk.AdColonyInterstitialListener
import com.adcolony.sdk.AdColonyZone
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.analytics.Analytics
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.CoinsManager
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.PreferencesManager

class AdcolonyVideo {

    private lateinit var coins: TextView
    private var prefernceManager: PreferencesManager
    private var adcolonyVideo: AdColonyInterstitial? = null
    private var coinManager: CoinsManager

    constructor(preferencesManager: PreferencesManager, coinManager: CoinsManager) {
        this.coinManager = coinManager
        this.prefernceManager = preferencesManager
    }

    fun init(activity: Activity) {
        AdColony.configure(activity, "app1506543c2b9e4d2a9b", "vzf1b9cbf2aea749a3bd")
        AdColony.setRewardListener { adColonyReward ->
            if (!prefernceManager.get(PreferencesManager.ADDITIONAL_LIFE, false)) {
                coinManager.addCoins(adColonyReward.rewardAmount)
                coins.text = coinManager.getCoins().toString()
                Analytics.report(Analytics.VIDEO, Analytics.ADCOLONY, Analytics.REWARD)
            } else {
                prefernceManager.put(PreferencesManager.ADDITIONAL_LIFE, false)
                prefernceManager.put(PreferencesManager.TICKETS_LIFES, 1)
                Analytics.report(Analytics.VIDEO, Analytics.ADCOLONY, Analytics.GAME_BOOST)
            }
        }
        onResume(activity)
    }

    fun showVideo(coins: TextView): Boolean {
        this.coins = coins
        if (adcolonyVideo != null) {
            if (adcolonyVideo!!.show()) {
                Analytics.report(Analytics.VIDEO, Analytics.ADCOLONY, Analytics.OPEN)
                return true
            }
        }
        return false
    }

    fun onResume(activity: Activity) {
        if (adcolonyVideo == null || adcolonyVideo!!.isExpired) {
            AdColony.requestInterstitial("vzf1b9cbf2aea749a3bd",
                    object : AdColonyInterstitialListener() {
                override fun onRequestFilled(ad: AdColonyInterstitial) {
                    adcolonyVideo = ad
                }

                override fun onRequestNotFilled(zone: AdColonyZone?) {
                    super.onRequestNotFilled(zone)
                }

                override fun onExpiring(ad: AdColonyInterstitial?) {
                    super.onExpiring(ad)
                    AdColony.requestInterstitial("vzf1b9cbf2aea749a3bd", this)
                }
            })
        }
    }
}

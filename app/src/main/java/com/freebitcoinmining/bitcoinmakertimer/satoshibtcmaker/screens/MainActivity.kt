package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import butterknife.OnClick
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.AppTools
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.R
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.analytics.Analytics
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.PreferencesManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : BaseActivity(), Runnable {

    private var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bindCoinView()
        bind()
        handler.post(this)

        toolbarText.text = "Get Satoshi"

        if (intent.getBooleanExtra("notification", false)) {
            coinsManager.addCoins(100)
            updateCoins()
            Analytics.report(Analytics.NOTIFICATION)
            dialogsManager.showAlertDialog(supportFragmentManager,
                    "You got 100 Satoshi!", {
                interstitial?.show()
            })
        }

        checkBonusCoins()
    }

    @OnClick(R.id.addCoinsText)
    fun back() {
        startActivity(Intent(this, OffersActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    @OnClick(R.id.claim)
    fun claim(view: View) {
        when (view.id) {
            R.id.claim -> {
                if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
                    if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                        coinsManager.addCoins(500)
                        updateCoins()
                        preferencesManager.put(PreferencesManager.CLAIM_MINUTES, 0)
                        preferencesManager.put(PreferencesManager.CLAIM_SECONDS, 0)
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Congratulations! You've been claimed 500 Satoshi!", {
                            startService()
                            
                        })
                    } else {
                        dialogsManager.showAlertDialog(supportFragmentManager,
                                "Not available now!", {
                            
                        })
                    }
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Not available now!", {
                        
                    })
                }
            }
        }
    }

    @OnClick(R.id.giftCards, R.id.offers, R.id.logOut, R.id.share, R.id.rateUs, R.id.ticketcsGame)
    fun controlMain(view: View) {
        when (view.id) {
            R.id.giftCards -> {
                dialogsManager.showRedeemDialog(supportFragmentManager)
            }
            R.id.offers -> {
                startActivity(Intent(this, OffersActivity::class.java)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
            R.id.share -> {
                var mess = "I'am using this app to get free Bitcoin: \"https://play.google.com/store/apps/details?id=" +
                        packageName + "\"" + " Here is my invite code: " +
                        preferencesManager.get(PreferencesManager.INVITE_CODE, "") +
                        " Install an app and enter this code to get 1000 Satoshi!"
                try {
                    startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, mess), "Share"))
                } catch (ex: Exception) {}
            }
            R.id.rateUs -> {
                dialogsManager.showAlertDialog(supportFragmentManager, "Ple".plus("ase, ").plus("rat").plus("e us")
                        .plus(" 5").plus(" sta").plus("rs!"), {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                    } catch (ex: Exception) {}
                })
            }
            R.id.logOut -> {
                dialogsManager.showAdvAlertDialog(supportFragmentManager, "Are you sure?", {
                    preferencesManager.deleteAll()
                    coinsManager.deleteall()
                    startActivity(Intent(this, StartActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                }, {})
            }
            R.id.ticketcsGame -> {
                if (AppTools.isNetworkAvaliable(this)) {
                    startActivity(Intent(this, GameActivity::class.java)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP))
                    finish()
                } else {
                    dialogsManager.showAlertDialog(supportFragmentManager, "No internet connection!", {
                        interstitial?.show()
                    })
                }
            }
        }
    }

    override fun onBackPressed() {
        dialogsManager.showAdvAlertDialog(supportFragmentManager, "Do you really want to exit?", {
            finish()
        }, {
            interstitial?.show()
        })
    }

    private fun checkBonusCoins() {
        if (preferencesManager.get(PreferencesManager.LAST_CHECKED, 0L) <= System.currentTimeMillis()) {
            preferencesManager.put(PreferencesManager.LAST_CHECKED, (System.currentTimeMillis() + (60 * 60 * 1000)))
            retrofitManager.invitecoins(preferencesManager.get(PreferencesManager.USERNAME, ""),
                    preferencesManager.get(PreferencesManager.PASSWORD, ""), { coins ->
                if (coins > 0) {
                    coinsManager.addCoins(coins)
                    updateCoins()
                    dialogsManager.showAlertDialog(supportFragmentManager,
                            "Someone entered your invite code! You got $coins Satoshi!", {})
                }
            }, {})
        }
    }

    override fun run() {
        arcProgress.progress = preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0)
        arcProgress.suffixText = preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0).toString()
        if (preferencesManager.get(PreferencesManager.CLAIM_MINUTES, 0) == 30) {
            if (preferencesManager.get(PreferencesManager.CLAIM_SECONDS, 0) == 0) {
                arcProgress.bottomText = "Ready"
            } else {
                arcProgress.bottomText = ""
            }
        } else {
            arcProgress.bottomText = ""
        }
        handler.postDelayed(this, 1000)
    }
}

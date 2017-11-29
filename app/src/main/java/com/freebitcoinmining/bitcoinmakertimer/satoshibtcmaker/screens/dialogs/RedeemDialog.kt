package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.dialogs

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import butterknife.ButterKnife
import butterknife.OnClick
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.AppTools
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.R
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.MyApplication
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.analytics.Analytics
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.CoinsManager
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.DialogsManager
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.managers.PreferencesManager
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.inject.AppModule
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.inject.DaggerAppComponent
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.MainActivity
import kotlinx.android.synthetic.main.dialog_redeem.view.*
import javax.inject.Inject
import kotlin.concurrent.thread

class RedeemDialog : DialogFragment() {

    @Inject lateinit var preferencesManager: PreferencesManager
    @Inject lateinit var coinsManager: CoinsManager
    @Inject lateinit var dialogsManager: DialogsManager

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        DaggerAppComponent.builder()
                .appModule(AppModule(context))
                .mainModule((activity.application as MyApplication).mainModule)
                .build().inject(this)

        var view = inflater?.inflate(R.layout.dialog_redeem, container, false)
        view?.rootView?.btcToRedeemText?.text = "Withdraw\n${format(coinsManager.getCoins() / 100000000f)} BTC"

        ButterKnife.bind(this, view!!)

        return view
    }

    @OnClick(R.id.redeemBtn)
    fun redeem() {
        if (AppTools.isNetworkAvaliable(activity)) {
            if (coinsManager.getCoins() >= 500000) {
                if (AppTools.isEmailAdressCorrect(view?.rootView?.emailText?.text.toString())) {
                    var dismisser = dialogsManager.showProgressDialog(activity.supportFragmentManager)
                    thread {
                        Thread.sleep(3000)
                        Analytics.report(Analytics.WITHDRAW, Analytics.AMOUNT, coinsManager.getCoins().toString())
                        activity.runOnUiThread {
                            dismisser.dismiss()
                            coinsManager.subtractCoins(coinsManager.getCoins())
                            view?.rootView?.btcToRedeemText?.text = "Withdraw\n${format(coinsManager.getCoins() / 100000000f)} BTC"
                            (activity as MainActivity).updateCoins()
                            dialogsManager.showAlertDialog(activity.supportFragmentManager,
                                    "You will receive your Satoshi in 3 - 7 days!", {
                                dismiss()
                            })
                        }
                    }
                } else {
                    dialogsManager.showAlertDialog(activity.supportFragmentManager,
                            "Wallet is not valid!", {
                        (activity as MainActivity).interstitial?.show()
                    })
                }
            } else {
                dialogsManager.showAlertDialog(activity.supportFragmentManager,
                        "Not enough Satoshi! You need 500000 Satoshi", {
                    (activity as MainActivity).interstitial?.show()
                })
            }
        } else {
            dialogsManager.showAlertDialog(activity.supportFragmentManager,
                    "No internet connection!", {
                (activity as MainActivity).interstitial?.show()
            })
        }
    }

    private fun format(v: Float): String {
        return String.format("%.8f", v)
    }
}

package com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.inject

import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.MyApplication
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.core.services.ClaimService
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.dialogs.LoginDialog
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.dialogs.PromocodeDialog
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.dialogs.RedeemDialog
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.dialogs.SignupDialog
import com.freebitcoinmining.bitcoinmakertimer.satoshibtcmaker.screens.BaseActivity
import dagger.Component

@Component(modules = arrayOf(AppModule::class, MainModule::class))
interface AppComponent {

    fun inject(screen: BaseActivity)
    fun inject(app: MyApplication)
    fun inject(dialog: LoginDialog)
    fun inject(dialog: SignupDialog)
    fun inject(dialog: PromocodeDialog)
    fun inject(dialog: RedeemDialog)
    fun inject(service: ClaimService)
}

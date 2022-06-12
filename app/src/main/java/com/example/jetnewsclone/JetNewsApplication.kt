package com.example.jetnewsclone

import android.app.Application
import com.example.jetnewsclone.data.AppContainer
import com.example.jetnewsclone.data.AppContainerImpl

/**
 * @author: My Project
 * @Skype: chukimmuoi
 * @Mobile : +84 167 367 2505
 * @Email: chukimmuoi@gmail.com
 * @Website: https://github.com/chukimmuoi
 * @Project: JetNewsClone
 * Created by chukimmuoi on 24/05/2022.
 */
class JetNewsApplication: Application() {

    // AppContainer instance used by the rest of classes to obtain dependencies
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainerImpl(this)
    }
}
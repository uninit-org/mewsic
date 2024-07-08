package dev.uninit.mewsic.app

import com.google.auto.service.AutoService
import dev.uninit.mewsic.api.MewsicPluginBase
import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.theme.AppTheme
import dev.uninit.mewsic.app.style.theme.CuteAppTheme
import dev.uninit.mewsic.app.style.theme.MaterialAppTheme

@AutoService(MewsicPluginBase::class)
class DefaultPlugin : MewsicPluginBase {
    private val defaultSchemes = mapOf<String, AppColors>()
    private val defaultThemes = mapOf<AppTheme.Key, AppTheme>(
        CuteAppTheme.KEY to CuteAppTheme,
        MaterialAppTheme.KEY to MaterialAppTheme
    )

    override fun colorSchemes(): Map<String, AppColors> = defaultSchemes
    override fun themes(): Map<AppTheme.Key, AppTheme> = defaultThemes
}

package dev.uninit.mewsic.api

import dev.uninit.mewsic.api.style.color.AppColors
import dev.uninit.mewsic.api.style.theme.AppTheme


interface MewsicPluginBase {
    fun colorSchemes(): Map<String, AppColors>
    fun themes(): Map<AppTheme.Key, AppTheme>
}

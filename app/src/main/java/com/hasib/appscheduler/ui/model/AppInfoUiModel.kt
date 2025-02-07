package com.hasib.appscheduler.ui.model

import com.hasib.appscheduler.domian.model.AppInfo

data class AppInfoUiModel(val appInfo: AppInfo, val formattedScheduledTime: String?)

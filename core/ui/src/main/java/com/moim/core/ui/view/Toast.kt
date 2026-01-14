package com.moim.core.ui.view

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.moim.core.designsystem.R

enum class ToastMessage(
    @StringRes val messageRes: Int,
) {
    ServerErrorMessage(R.string.common_error_disconnection),
    NetworkErrorMessage(R.string.common_error_network),

    CommentErrorMessage(R.string.plan_detail_comment_error),
    EmptyPlanErrorMessage(R.string.home_new_plan_created_not),
    SocialLoginErrorMessage(R.string.sign_in_kakao_fail),

    ReportCompletedMessage(R.string.plan_detail_report_completed),
    PlanWriteTimeErrorMessage(R.string.plan_write_time_error),
}

fun showToast(
    context: Context,
    message: ToastMessage,
) {
    Toast.makeText(context, message.messageRes, Toast.LENGTH_SHORT).show()
}

fun showToast(
    context: Context,
    message: String,
) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun showToast(
    context: Context,
    @StringRes messageRes: Int,
) {
    Toast.makeText(context, messageRes, Toast.LENGTH_SHORT).show()
}

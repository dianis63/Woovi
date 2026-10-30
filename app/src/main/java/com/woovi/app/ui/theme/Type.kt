package com.woovi.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.woovi.app.R

val Nunito = FontFamily(
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semibold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold)
)

val WooviTypography = Typography(
    headlineLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    titleMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = Nunito, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
)
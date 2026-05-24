package com.paymember.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.paymember.R
import com.paymember.ui.components.Eyebrow
import com.paymember.ui.components.SectionCard
import com.paymember.ui.components.ServiceBrand
import com.paymember.ui.components.ServiceLogo
import com.paymember.ui.components.Services
import com.paymember.viewmodel.AuthUiState
import kotlin.math.roundToInt

private const val GoogleWebClientIdPlaceholder = "replace-with-your-web-client-id.apps.googleusercontent.com"

@Composable
fun LoginScreen(
    uiState: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitEmailPassword: () -> Unit,
    onGoogleToken: (String) -> Unit,
    onGoogleError: (String) -> Unit,
    onToggleMode: () -> Unit
) {
    val context = LocalContext.current
    val googleWebClientId = context.getString(R.string.google_web_client_id)
    val isGoogleConfigured = googleWebClientId.isNotBlank() && googleWebClientId != GoogleWebClientIdPlaceholder
    val googleOptions = remember(googleWebClientId) {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(googleWebClientId)
            .build()
    }
    val googleClient = remember(context, googleOptions) {
        GoogleSignIn.getClient(context, googleOptions)
    }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) {
            onGoogleError("Inicio de sesion con Google cancelado.")
            return@rememberLauncherForActivityResult
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken
            if (idToken.isNullOrBlank()) {
                onGoogleError("Google no devolvio un token. Revisa el Web Client ID.")
            } else {
                onGoogleToken(idToken)
            }
        } catch (ex: ApiException) {
            onGoogleError(googleSignInErrorMessage(ex.statusCode))
        }
    }
    val lowerRing = MaterialTheme.colorScheme.primary.copy(alpha = 0.07f)
    val lowerRingSoft = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
            .drawBehind {
                val thin = 1.dp.toPx()
                drawCircle(
                    color = lowerRing,
                    radius = size.width * 0.52f,
                    center = Offset(size.width * 0.78f, size.height * 0.88f),
                    style = Stroke(width = thin)
                )
                drawCircle(
                    color = lowerRingSoft,
                    radius = size.width * 0.34f,
                    center = Offset(size.width * 0.16f, size.height * 0.96f),
                    style = Stroke(width = thin)
                )
            }
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 68.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            HeaderBlock()
            SectionCard(
                modifier = Modifier.fillMaxWidth(),
                padding = PaddingValues(18.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Eyebrow(if (uiState.isRegisterMode) "CREAR CUENTA" else "INICIAR SESI\u00d3N")
                    Text(
                        if (uiState.isRegisterMode) "Empieza a controlar tus suscripciones" else "Hola de nuevo",
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        lineHeight = 28.sp,
                        textAlign = TextAlign.Center
                    )
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = onEmailChange,
                        label = { Text("Correo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small
                    )
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = onPasswordChange,
                        label = { Text("Contrase\u00f1a") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = MaterialTheme.shapes.small
                    )
                    if (!uiState.errorMessage.isNullOrBlank()) {
                        Text(
                            uiState.errorMessage,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Button(
                        onClick = onSubmitEmailPassword,
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        )
                    ) {
                        Text(if (uiState.isRegisterMode) "Crear cuenta" else "Entrar")
                    }
                    OutlinedButton(
                        onClick = {
                            if (!isGoogleConfigured) {
                                onGoogleError("Configura GOOGLE_WEB_CLIENT_ID antes de usar Google.")
                                return@OutlinedButton
                            }

                            googleClient.signOut().addOnCompleteListener {
                                googleLauncher.launch(googleClient.signInIntent)
                            }
                        },
                        enabled = !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text("Continuar con Google")
                    }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = onToggleMode,
                            enabled = !uiState.isLoading,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), CircleShape)
                                .padding(horizontal = 2.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (uiState.isRegisterMode) {
                                    "\u00bfYa tienes cuenta? Inicia sesi\u00f3n"
                                } else {
                                    "\u00bfNo tienes cuenta? Reg\u00edstrate"
                                },
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun googleSignInErrorMessage(statusCode: Int): String {
    return when (statusCode) {
        GoogleSignInStatusCodes.SIGN_IN_CANCELLED -> "Inicio de sesion con Google cancelado."
        GoogleSignInStatusCodes.SIGN_IN_CURRENTLY_IN_PROGRESS -> "Ya hay un inicio de sesion con Google en curso."
        GoogleSignInStatusCodes.SIGN_IN_FAILED -> "Google no pudo iniciar sesion. Intentalo de nuevo."
        CommonStatusCodes.DEVELOPER_ERROR -> "Configuracion de Google no valida. Revisa package name, SHA-1 y Web Client ID."
        CommonStatusCodes.NETWORK_ERROR -> "No hay conexion para iniciar sesion con Google."
        else -> "No se pudo iniciar sesion con Google (codigo $statusCode)."
    }
}

@Composable
private fun HeaderBlock() {
    val ringColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.14f)
    val ringColorSoft = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.08f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.82f)
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.primary, MaterialTheme.shapes.large)
                .drawBehind {
                    val thin = 1.dp.toPx()
                    drawCircle(
                        color = ringColor,
                        radius = size.width * 0.42f,
                        center = Offset(size.width * 0.92f, size.height * 0.05f),
                        style = Stroke(width = thin)
                    )
                    drawCircle(
                        color = ringColorSoft,
                        radius = size.width * 0.28f,
                        center = Offset(size.width * 0.08f, size.height * 0.92f),
                        style = Stroke(width = thin)
                    )
                }
                .padding(horizontal = 20.dp, vertical = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "PayMember",
                    style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                Text(
                    "Gestiona tus pagos sin sorpresas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
                    textAlign = TextAlign.Center
                )
            }
        }
        ServiceLogoMarquee()
    }
}

@Composable
private fun ServiceLogoMarquee() {
    val logoSize = 38.dp
    val logoGap = 10.dp
    val itemSlot = logoSize + logoGap
    val services = listOf(
        "netflix",
        "spotify",
        "disney",
        "chatgpt",
        "youtube-premium",
        "prime-video",
        "apple-tv",
        "microsoft-365",
        "icloud",
        "canva"
    ).map(Services::brandFor)
    val cycleWidth = with(LocalDensity.current) {
        (itemSlot * services.size).toPx()
    }
    val transition = rememberInfiniteTransition(label = "service-logo-marquee")
    val offset = transition.animateFloat(
        initialValue = 0f,
        targetValue = cycleWidth,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "service-logo-offset"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(
                    brush = Brush.horizontalGradient(
                        0f to Color.Transparent,
                        0.14f to Color.Black,
                        0.86f to Color.Black,
                        1f to Color.Transparent
                    ),
                    blendMode = BlendMode.DstIn
                )
            }
            .clipToBounds(),
        contentAlignment = Alignment.CenterStart
    ) {
        MarqueeLogoChain(
            services = services,
            logoSize = logoSize,
            itemSlot = itemSlot,
            offset = offset.value
        )
    }
}

@Composable
private fun MarqueeLogoChain(
    services: List<ServiceBrand>,
    logoSize: Dp,
    itemSlot: Dp,
    offset: Float
) {
    val copies = 8

    Row(
        modifier = Modifier
            .requiredWidth(itemSlot * services.size * copies)
            .offset {
                IntOffset((-offset).roundToInt(), 0)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(copies) {
            services.forEach { service ->
                Box(
                    modifier = Modifier.width(itemSlot),
                    contentAlignment = Alignment.CenterStart
                ) {
                    ServiceLogo(service = service, size = logoSize)
                }
            }
        }
    }
}

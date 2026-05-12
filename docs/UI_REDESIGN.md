# PayMember · Handoff visual para Codex

Este documento es el paquete que le pegas a Codex (o Claude Code) dentro
de Android Studio para que aplique el rediseño visual sobre tu proyecto
existente **sin tocar la lógica**. Está pensado para que lo copies entero
o por secciones según el cambio que pidas.

> Archivo de referencia visual: `PayMember Redesign.html` en este proyecto.
> Pantallas, paleta, tipografía y componentes están allí. Si Codex pregunta,
> dile que se inspire en ese HTML.

---

## 0. Cómo se lo paso a Codex

Tres formas, de menos a más fricción:

1. **Prompt directo (lo más rápido).** Copia las secciones 1–6 de este
   archivo en el chat de Codex y dile:
   > *Aplica este rediseño sobre el proyecto actual. No cambies ninguna
   > lógica de viewmodel, navigation, repository ni persistencia. Solo
   > Compose, Color.kt, Type.kt, Theme.kt y los `*Screen.kt`.*
2. **Markdown en el repo.** Guarda este archivo como `docs/UI_REDESIGN.md`
   junto al `CHECKLIST_EJECUCION.md` que ya tienes. Luego en Codex:
   > *Lee `docs/UI_REDESIGN.md` y aplica el rediseño respetando el contrato
   > de no tocar lógica.*
3. **Pantallazos + este doc.** Adjunta capturas del HTML en el chat y
   referencia este markdown. Útil si Codex no entiende algún detalle visual.

Al final hay un **prompt único listo para copiar** (sección 7).

---

## 1. Filosofía visual

**Quiet premium fintech.** No es una app de neón ni una app de Material 3
genérica. Es una app financiera silenciosa:

- Fondo cálido (`#EFEDE6`) en lugar de blanco frío.
- Verde forest profundo como ancla de marca.
- Tipografía grande con jerarquía por tamaño, no por color.
- Números siempre con `fontFeatureSettings = "tnum"`.
- Sombras suaves (≈ 1–2 dp) o ninguna. Bordes hairline en su lugar.
- Color saturado solo en datos (gráficos de distribución) y en logos de
  servicio.
- Sin gradientes hipersaturados, sin glassmorphism.

---

## 2. Tokens

### 2.1 Color.kt

```kotlin
package com.paymember.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// ── Marca
val ForestGreen     = Color(0xFF1F4634)
val ForestSoft      = Color(0xFFE6EDDD)
val ForestInk       = Color(0xFF0E2A1F)
val OnBrand         = Color.White

// ── Dark mode marca
val ForestGreenDark = Color(0xFF9CD8B5)
val ForestSoftDark  = Color(0xFF22332A)
val ForestInkDark   = Color(0xFFDEF1E4)
val OnBrandDark     = Color(0xFF0B1A12)

// ── Neutros cálidos · light
val Bone            = Color(0xFFEFEDE6)
val SurfaceWhite    = Color(0xFFFFFFFF)
val SurfaceTint1    = Color(0xFFFAF7EE)
val SurfaceTint2    = Color(0xFFF5F1E4)
val Ink             = Color(0xFF0F1010)
val Ink2            = Color(0xFF3A3B3D)
val InkMuted        = Color(0xFF8A8A85)
val Hairline        = Color(0xFFE5E1D2)
val Hairline2       = Color(0xFFEDEAE0)

// ── Neutros cálidos · dark
val BoneDark        = Color(0xFF14130F)
val SurfaceDark     = Color(0xFF1E1C17)
val SurfaceTint1Dk  = Color(0xFF25221C)
val SurfaceTint2Dk  = Color(0xFF2C281F)
val InkDark         = Color(0xFFF1EDDE)
val Ink2Dark        = Color(0xFFBFB9A8)
val InkMutedDark    = Color(0xFF7E796C)
val HairlineDark    = Color(0xFF2F2B22)
val Hairline2Dark   = Color(0xFF3A3527)

// ── Categóricos (mismo set en light y dark)
val Coral    = Color(0xFFE8916B)
val Lavender = Color(0xFFB5AEE6)
val Sky      = Color(0xFFA6C6E8)
val Butter   = Color(0xFFE8D08C)
val Moss     = Color(0xFF9CB87A)
val Rose     = Color(0xFFE4B5C5)

// ── Semánticos
val Success     = Color(0xFF2F7E5C)
val Danger      = Color(0xFFC24B4B)
val SuccessDark = Color(0xFF4FB089)
val DangerDark  = Color(0xFFE27A7A)

val PayMemberLightScheme = lightColorScheme(
    primary             = ForestGreen,
    onPrimary           = OnBrand,
    primaryContainer    = ForestSoft,
    onPrimaryContainer  = ForestInk,
    secondary           = Ink,                 // CTA principal = ink, no brand
    onSecondary         = SurfaceWhite,
    background          = Bone,
    onBackground        = Ink,
    surface             = SurfaceWhite,
    onSurface           = Ink,
    surfaceVariant      = SurfaceTint2,
    onSurfaceVariant    = InkMuted,
    outline             = Hairline,
    outlineVariant      = Hairline2,
    error               = Danger,
)

val PayMemberDarkScheme = darkColorScheme(
    primary             = ForestGreenDark,
    onPrimary           = OnBrandDark,
    primaryContainer    = ForestSoftDark,
    onPrimaryContainer  = ForestInkDark,
    secondary           = InkDark,
    onSecondary         = BoneDark,
    background          = BoneDark,
    onBackground        = InkDark,
    surface             = SurfaceDark,
    onSurface           = InkDark,
    surfaceVariant      = SurfaceTint2Dk,
    onSurfaceVariant    = InkMutedDark,
    outline             = HairlineDark,
    outlineVariant      = Hairline2Dark,
    error               = DangerDark,
)
```

### 2.2 Type.kt

Geist no viene en Google Fonts por defecto en Compose. Dos rutas:

- **Recomendada (offline):** descarga `Geist-Regular/Medium/SemiBold/Bold.ttf`
  y `GeistMono-Medium.ttf` desde
  <https://github.com/vercel/geist-font/tree/main/fonts> a `res/font/`.
- **Alternativa:** usa Plus Jakarta Sans via `DownloadableFonts` provider.

```kotlin
package com.paymember.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Geist = FontFamily(
    Font(R.font.geist_regular,  FontWeight.Normal),
    Font(R.font.geist_medium,   FontWeight.Medium),
    Font(R.font.geist_semibold, FontWeight.SemiBold),
    Font(R.font.geist_bold,     FontWeight.Bold),
)
val GeistMono = FontFamily(Font(R.font.geistmono_medium, FontWeight.Medium))

private val TabularNums = "tnum"

val PMTypography = Typography(
    displayLarge   = TextStyle(Geist, 56.sp, FontWeight.SemiBold, letterSpacing = (-2.2).sp, fontFeatureSettings = TabularNums),
    displayMedium  = TextStyle(Geist, 40.sp, FontWeight.SemiBold, letterSpacing = (-1.6).sp, fontFeatureSettings = TabularNums),
    headlineLarge  = TextStyle(Geist, 32.sp, FontWeight.SemiBold, letterSpacing = (-0.8).sp),
    titleLarge     = TextStyle(Geist, 22.sp, FontWeight.SemiBold, letterSpacing = (-0.5).sp),
    titleMedium    = TextStyle(Geist, 15.sp, FontWeight.SemiBold, letterSpacing = (-0.2).sp),
    bodyLarge      = TextStyle(Geist, 14.sp, FontWeight.Normal),
    bodyMedium     = TextStyle(Geist, 13.sp, FontWeight.Normal),
    bodySmall      = TextStyle(Geist, 12.sp, FontWeight.Medium),
    labelLarge     = TextStyle(Geist, 13.sp, FontWeight.SemiBold, letterSpacing = (-0.1).sp),
    labelMedium    = TextStyle(GeistMono, 11.sp, FontWeight.SemiBold, letterSpacing = 0.9.sp),
    labelSmall     = TextStyle(GeistMono, 10.sp, FontWeight.SemiBold, letterSpacing = 0.9.sp),
)
```

> **Convención:** los `eyebrow` / micro-titulares (“GASTO MENSUAL · MAYO”) van
> en `labelMedium` (GeistMono, UPPERCASE, letterSpacing alto).

### 2.3 Theme.kt + Shape

```kotlin
val PMShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small      = RoundedCornerShape(14.dp),
    medium     = RoundedCornerShape(18.dp),
    large      = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun PayMemberTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) PayMemberDarkScheme else PayMemberLightScheme,
        typography  = PMTypography,
        shapes      = PMShapes,
        content     = content,
    )
}
```

---

## 3. Componentes compartidos

Crea un nuevo archivo `ui/components/PMComponents.kt`:

```kotlin
package com.paymember.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Tarjeta blanca con hairline y sin elevation. Es el ladrillo de todo.
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(16.dp),
    color: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = color,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) { Column(Modifier.padding(padding), content = content) }
}

// Eyebrow / micro-titulares
@Composable
fun Eyebrow(text: String, color: Color = MaterialTheme.colorScheme.onSurfaceVariant) {
    Text(text.uppercase(), color = color, style = MaterialTheme.typography.labelMedium)
}

// Precio con la coma en pequeño y el € desaturado
@Composable
fun MoneyText(
    value: Double,
    size: TextUnit = 40.sp,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val txt = "%.2f".format(value)
    val (int, frac) = txt.split(".", limit = 2).let { it[0] to it.getOrElse(1) { "00" } }
    Row(verticalAlignment = Alignment.Bottom) {
        Text("€", color = color.copy(alpha = 0.6f),
             fontSize = size.value.times(0.55f).sp, fontWeight = FontWeight.SemiBold)
        Text(int, color = color, fontSize = size, fontWeight = FontWeight.SemiBold)
        Text(",$frac", color = color.copy(alpha = 0.55f),
             fontSize = size.value.times(0.55f).sp, fontWeight = FontWeight.SemiBold)
    }
}

// Logo de servicio: chip de color con SVG drawable (si existe) o monograma
data class ServiceBrand(
    val key: String,           // "netflix", "spotify", …
    val bg: Color,
    val fg: Color,
    val mark: String,          // fallback si no hay drawable: "N", "tv+", "365"…
    val drawable: Int? = null, // R.drawable.svc_netflix … opcional
)

@Composable
fun ServiceLogo(svc: ServiceBrand, size: Dp = 44.dp) {
    Box(
        Modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.30f))
            .background(svc.bg),
        contentAlignment = Alignment.Center,
    ) {
        if (svc.drawable != null) {
            Icon(painter = painterResource(svc.drawable), contentDescription = null,
                 tint = svc.fg, modifier = Modifier.size(size * 0.55f))
        } else {
            val short = svc.mark.length <= 2
            Text(
                svc.mark, color = svc.fg, fontWeight = FontWeight.Bold,
                fontSize = (if (short) size.value * 0.46f else size.value * 0.32f).sp,
            )
        }
    }
}
```

### Catálogo de servicios

```kotlin
object Services {
    val netflix  = ServiceBrand("netflix",  Color(0xFF0F0F10), Color(0xFFE50914), "N")
    val spotify  = ServiceBrand("spotify",  Color(0xFF0E2818), Color(0xFF1ED760), "S")
    val disney   = ServiceBrand("disney",   Color(0xFF0B1B3E), Color.White,       "D+")
    val chatgpt  = ServiceBrand("chatgpt",  Color(0xFF0E2A22), Color(0xFF10A37F), "✦")
    val icloud   = ServiceBrand("icloud",   Color(0xFFE8F2FB), Color(0xFF1B7BE8), "☁")
    val office   = ServiceBrand("office",   Color(0xFF22152A), Color(0xFFE8916B), "365")
    val max      = ServiceBrand("max",      Color(0xFF0F1A4A), Color(0xFF7AA0FF), "M")
    val prime    = ServiceBrand("prime",    Color(0xFF0E1A28), Color(0xFF7AC4F0), "P")
    val appletv  = ServiceBrand("appletv",  Color(0xFF111111), Color.White,       "tv+")
    val youtube  = ServiceBrand("youtube",  Color(0xFF1A0F10), Color(0xFFFF3D3D), "▶")
    // Para sustituir cualquier glifo por la SVG oficial:
    //   .copy(drawable = R.drawable.svc_netflix)
}
```

> **Para el SVG real:** descarga el press-kit de cada servicio
> (`spotify.design/brand`, `dnp.disney.com/branding`, etc.), pásalo por
> Android Studio → `New → Vector Asset`, guárdalo como `svc_<nombre>.xml`
> en `res/drawable/`, y en el `ServiceBrand` haz
> `drawable = R.drawable.svc_<nombre>`. El `Logo` lo dibujará en lugar del
> monograma. Importante: no incluyas marcas registradas en el APK público
> sin permiso.

---

## 4. Cómo modificar cada Screen

> Regla: el `ViewModel`, el `Repository` y el `NavController` no se tocan.
> Solo cambia la composición visual.

### 4.1 `SubscriptionListScreen.kt`  (Home)

Cambios:

1. **Quitar** `Scaffold` con `TopAppBar` y `FloatingActionButton`.
2. **Header custom** en un `Row`: chip-logo + saludo + iconos circulares
   (search / bell) como `IconButton` redondos con borde hairline.
3. **CreateSubscriptionPanel** → cambiar a tarjeta de fondo
   `MaterialTheme.colorScheme.primary` con CTA blanca (`Catálogo`) y CTA
   `outlined` (`Manual`). Mantén `onAddClick`.
4. **MonthlyOverviewCard** → renombrar a `MonthlyHeroCard`. Layout:
   - Eyebrow `GASTO MENSUAL · <MES>`
   - `MoneyText(totalMonthly, 40.sp)`
   - Subtítulo: `≈ €X al año · N activas`
   - Pill verde con flecha (tendencia vs mes anterior — opcional, puede
     mostrar siempre `0` si no hay histórico)
   - Barra de distribución apilada usando `Coral`, `Lavender`, `Sky`,
     `Butter`, `Moss`, `Rose` (`Row` de `Box` con `weight(item.monthly /
     total)`).
5. **Nuevo** `UpcomingStrip` — `LazyRow` de mini-tarjetas con
   día/mes/logo/precio. Conecta con `subscriptions.sortedBy {
   nextChargeDistance(it.billingDay) }`.
6. **Lista de suscripciones** → fila con `ServiceLogo(40.dp)` + nombre + plan
   + día + precio. Si `splitPeople > 1`, badge `÷N` verde.
7. **Eliminar** el `FloatingActionButton`. El usuario crea desde la
   tarjeta hero.

### 4.2 `SubscriptionCatalogScreen.kt`

1. Header custom con `IconButton` circular para back, eyebrow
   `PASO 1 DE 3`, título grande.
2. Search bar con `OutlinedTextField` `shape = RoundedCornerShape(16.dp)`
   y placeholder “Buscar Netflix, Spotify…”.
3. `ManualSubscriptionCard` → tarjeta con `border` dashed (usa
   `drawBehind` o un `Modifier.border` con un `Brush` punteado).
4. `CategoryRow` → reemplazar `LazyRow` horizontal por chips de filtro
   arriba + `LazyVerticalGrid(columns = 2)` con `ServiceCatalogCard`.
5. `ServiceCatalogCard` → `ServiceLogo(44.dp)` + nombre + pill `N tarifas` +
   precio “desde €X.XX/mes”.

### 4.3 `SubscriptionPlansScreen.kt`

1. Header igual (`PASO 2 DE 3`).
2. `ServiceHeader` → `SectionCard(color = svc.bg)` con `ServiceLogo(48.dp)`
   y texto blanco. Watermark grande de la inicial al fondo (opcional, con
   `Modifier.drawBehind` + alpha 0.16f).
3. `PlanCard` → `SectionCard` con `border = 2.dp brand` cuando
   `selected`. Tags como `AssistChip` pequeños. Precio derecha con
   `MoneyText(22.sp)`.
4. Badge **Recomendado** posicionado con `offset(y = -10.dp)` sobre la
   esquina superior derecha del plan más caro.
5. CTA inferior pinned: `Button` con `Modifier.fillMaxWidth().height(52.dp)`,
   `shape = MaterialTheme.shapes.small`, color `secondary` (ink).

### 4.4 `SubscriptionFormScreen.kt`

1. Header `PASO 3 DE 3`.
2. **LockedPlanSummary** → reducir a fila compacta:
   `ServiceLogo + nombre + precio + chip "Editar"`.
3. **SplitPriceCard** → sustituir layout actual por:
   - Row con icono `Split` + label “Dividir entre personas”.
   - Stepper más grande: `-`, dots de progreso (`Row` de `Box` 16×16
     redondos coloreados), número grande tabular, `+`.
   - Card verde inferior con eyebrow `TU PARTE`, subtítulo `€total ÷ N` y
     `MoneyText` blanco grande a la derecha.
4. **BillingCalendarCard** → mantener la lógica de `YearMonth` pero:
   - Header con icono `Calendar` + “Día de cobro” + chip verde con
     `Día N` a la derecha (`MaterialTheme.colorScheme.primary`).
   - Cell selected: círculo `primary`, blanco dentro.
   - Hoy (`!= selected`): fondo `primaryContainer` + dot debajo del número.
5. **ReminderCard** → switch grande tintado en primary, debajo un Row de
   4 segmented options (`El día · 1d · 3d · 7d`) con el seleccionado
   `secondary` (ink) y resto `surfaceVariant`.
6. **Notas** → opcional, colapsable.
7. **CTA fijo abajo** muestra precio: `Crear suscripción · €X,XX/mes`.

---

## 5. Reglas visuales que Codex debe respetar

- `Modifier.padding` lateral de pantallas: `16.dp`.
- Separación entre cards: `12.dp`.
- Padding interno de tarjetas: `16.dp` (`14.dp` en filas tipo list-item).
- CTA principal: `MaterialTheme.colorScheme.secondary` (ink), 52.dp alto,
  `shape = MaterialTheme.shapes.small`.
- Iconos en botón circular header: 38–40.dp, fondo `surface`, border
  `1.dp outline`.
- Logos de servicios: `ServiceLogo` (rounded square, radio = 30% del lado).
- Tipografía de números: **siempre** `fontFeatureSettings = "tnum"`.
- Color en CTA = ink, no brand. Reserva `primary` para acentos puntuales.
- Sin `Card` por defecto: usa `SectionCard`.
- Sin `Scaffold` con `TopAppBar` Material: header propio en `Row`.
- Eyebrow / micros en `labelMedium` (GeistMono uppercase letterSpacing 0.9).

---

## 6. No tocar

- `data/dao/*`, `data/db/*`, `data/model/*`, `data/repository/*`,
  `data/reminder/*`.
- `viewmodel/SubscriptionViewModel.kt`.
- `MainActivity.kt` (a menos que cambie `setContent { PayMemberTheme {…} }`,
  que sí se permite).
- Cualquier llamada a `onAddClick`, `onEditClick`, `onDeleteClick`,
  `onFormChange`, `onSaveClick`, `onBackClick` — mantener firmas.
- `BillingPeriod`, `SubscriptionEntity`, `SubscriptionFormState`.

---

## 7. Prompt único para Codex (copia y pega)

```
Necesito que rediseñes la UI de mi app Android PayMember (Jetpack Compose
+ Material 3) siguiendo la guía visual de docs/UI_REDESIGN.md.

REGLAS DE INTOCABLES (no toques nada de esto):
- ViewModel: com.paymember.viewmodel.*
- Data: com.paymember.data.* (dao, db, model, reminder, repository)
- Firmas de callbacks de los Screen (onAddClick, onEditClick, etc.)
- Navigation (MainActivity NavHost)
- DataStore / Room schemas

CAMBIOS QUE QUIERO:
1. Reescribe ui/theme/Color.kt, Type.kt y Theme.kt según secciones 2.1–2.3
   del MD. Soporta light + dark.
2. Crea ui/components/PMComponents.kt con SectionCard, Eyebrow, MoneyText
   y ServiceLogo (sección 3 del MD). Crea también ui/components/Services.kt
   con el catálogo ServiceBrand.
3. Rediseña los cuatro screens según sección 4:
   - SubscriptionListScreen.kt
   - SubscriptionCatalogScreen.kt
   - SubscriptionPlansScreen.kt
   - SubscriptionFormScreen.kt
4. Conecta los ServiceBrand con los SubscriptionTemplate existentes
   (SubscriptionCatalog.kt) por el campo id; añade un map id -> ServiceBrand
   en Services.kt sin modificar SubscriptionCatalog.kt (la lógica de plans
   se queda).
5. Respeta las reglas de la sección 5.

Cuando termines, dime qué archivos has tocado y resume cambios. Si dudas
de un detalle visual, mira PayMember Redesign.html.
```

---

## 8. Migración progresiva (si Codex no lo hace todo de una)

Orden recomendado por archivo:

1. `Color.kt`  ← compila y verifica que la app sigue arrancando.
2. `Type.kt` + `Theme.kt` + cargar fuente Geist (o Plus Jakarta como fallback).
3. `ui/components/PMComponents.kt` (helpers nuevos).
4. `SubscriptionListScreen.kt` (la pantalla que más impacto visual da).
5. `SubscriptionCatalogScreen.kt`.
6. `SubscriptionPlansScreen.kt`.
7. `SubscriptionFormScreen.kt`.

Después de cada paso: `./gradlew assembleDebug` + smoke test rápido.

---

## 9. Snippets útiles que querrás durante el handoff

### Distribution bar (apilada)
```kotlin
@Composable
fun DistributionBar(
    items: List<Pair<String, Double>>,    // (name, monthly)
    colors: List<Color> = listOf(
        ForestGreen, Coral, Lavender, Butter, Sky, Moss, Rose,
    ),
) {
    val total = items.sumOf { it.second }.coerceAtLeast(0.01)
    Row(
        Modifier
            .fillMaxWidth()
            .height(10.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.outlineVariant),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        items.forEachIndexed { i, (_, v) ->
            Box(
                Modifier
                    .weight((v / total).toFloat().coerceAtLeast(0.02f))
                    .fillMaxHeight()
                    .background(colors[i % colors.size]),
            )
        }
    }
}
```

### Pill button (CTA secundaria sobre fondo verde)
```kotlin
@Composable
fun PillButton(text: String, onClick: () -> Unit, light: Boolean = true) {
    Surface(
        onClick = onClick,
        color = if (light) Color.White else Color.Transparent,
        contentColor = if (light) MaterialTheme.colorScheme.primary else Color.White,
        shape = RoundedCornerShape(999.dp),
        border = if (light) null
                 else BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
```

---

## 10. Checklist final antes de cerrar el handoff

- [ ] `Color.kt` actualizado y compilando (light + dark).
- [ ] `Type.kt` con Geist (o Plus Jakarta como plan B) y `tnum` en números.
- [ ] `Theme.kt` con `PMShapes` y dos colorSchemes.
- [ ] `SectionCard`, `Eyebrow`, `MoneyText`, `ServiceLogo` creados.
- [ ] `Services.kt` con map id → `ServiceBrand` (sin tocar el catálogo).
- [ ] 4 pantallas migradas, sin cambios en callbacks.
- [ ] Funcionan: crear suscripción manual y desde catálogo, split,
      día de cobro, recordatorio, edit, delete.
- [ ] Modo oscuro respeta colores semánticos del scheme.
- [ ] Reglas de la sección 5 verificadas con el ojo.

package com.paymember.ui.screens

import androidx.compose.ui.graphics.Color
import com.paymember.data.model.BillingPeriod

data class SubscriptionCategoryTemplate(
    val id: String,
    val title: String,
    val description: String
)

data class SubscriptionTemplate(
    val id: String,
    val name: String,
    val logoText: String,
    val categoryId: String,
    val accentColor: Color,
    val logoTextColor: Color = Color.White,
    val summary: String,
    val plans: List<SubscriptionPlanTemplate>
)

data class SubscriptionPlanTemplate(
    val id: String,
    val name: String,
    val price: String,
    val currencyLabel: String = "EUR",
    val period: BillingPeriod = BillingPeriod.MONTHLY,
    val details: List<String>
)

val SubscriptionCategories = listOf(
    SubscriptionCategoryTemplate("streaming", "Streaming", "Video, cine, series, anime y deporte"),
    SubscriptionCategoryTemplate("music", "Musica y audio", "Musica, podcasts, audiolibros y YouTube"),
    SubscriptionCategoryTemplate("shopping", "Compras y entregas", "Envios, comida a domicilio y supermercados"),
    SubscriptionCategoryTemplate("cloud", "Nube y productividad", "Almacenamiento, ofimatica y herramientas creativas"),
    SubscriptionCategoryTemplate("gaming", "Gaming", "Catalogos de juegos, online y juego en la nube"),
    SubscriptionCategoryTemplate("ai", "IA y asistentes", "Asistentes, busqueda y herramientas con IA"),
    SubscriptionCategoryTemplate("learning", "Aprendizaje", "Idiomas, cursos y formacion online"),
    SubscriptionCategoryTemplate("wellness", "Bienestar", "Deporte, meditacion y entrenamiento"),
    SubscriptionCategoryTemplate("security", "Privacidad", "VPN, password managers y correo seguro")
)

val PopularSubscriptionTemplates = listOf(
    service(
        id = "netflix",
        name = "Netflix",
        logoText = "N",
        categoryId = "streaming",
        accentColor = Color(0xFFE50914),
        summary = "Series y peliculas con planes con anuncios, Full HD y 4K.",
        plans = listOf(
            plan("ads", "Estandar con anuncios", "8.99", "Full HD", "2 dispositivos", "Con publicidad"),
            plan("standard", "Estandar", "14.99", "Full HD", "2 dispositivos", "Sin publicidad"),
            plan("premium", "Premium", "21.99", "4K HDR", "4 dispositivos", "Audio espacial")
        )
    ),
    service(
        id = "prime-video",
        name = "Prime Video",
        logoText = "prime",
        categoryId = "streaming",
        accentColor = Color(0xFF00A8E1),
        summary = "Streaming incluido con Prime, con opcion de quitar anuncios con suplemento.",
        plans = listOf(
            plan("monthly", "Prime mensual", "4.99", "Prime Video", "Envios Prime", "Con anuncios"),
            plan("yearly", "Prime anual", "49.90", "EUR", BillingPeriod.YEARLY, "Pago anual", "Incluye Prime Video", "Ahorro frente al mes"),
            plan("no-ads", "Prime sin anuncios", "6.98", "Prime mensual + suplemento", "Menos interrupciones", "Algunos directos pueden mantener anuncios")
        )
    ),
    service(
        id = "max",
        name = "Max",
        logoText = "max",
        categoryId = "streaming",
        accentColor = Color(0xFF1B4DFF),
        summary = "HBO, Warner, Discovery y deportes opcionales.",
        plans = listOf(
            plan("ads", "Basico con anuncios", "6.99", "Full HD", "2 dispositivos", "Con publicidad"),
            plan("standard", "Estandar", "10.99", "Full HD", "2 dispositivos", "30 descargas"),
            plan("premium", "Premium", "15.99", "4K UHD", "4 dispositivos", "100 descargas"),
            plan("dazn", "Max DAZN", "44.99", "Max Premium", "DAZN incluido", "Sin plan anual")
        )
    ),
    service(
        id = "disney",
        name = "Disney+",
        logoText = "D+",
        categoryId = "streaming",
        accentColor = Color(0xFF113CCF),
        summary = "Disney, Pixar, Marvel, Star Wars, National Geographic y Star.",
        plans = listOf(
            plan("ads", "Estandar con anuncios", "6.99", "Full HD", "2 dispositivos", "Con publicidad"),
            plan("standard", "Estandar", "10.99", "Full HD", "2 dispositivos", "Descargas"),
            plan("premium", "Premium", "15.99", "4K UHD y HDR", "4 dispositivos", "Dolby Atmos")
        )
    ),
    service(
        id = "apple-tv",
        name = "Apple TV",
        logoText = "tv+",
        categoryId = "streaming",
        accentColor = Color(0xFF111111),
        summary = "Series, peliculas y deportes seleccionados de Apple.",
        plans = listOf(
            plan("monthly", "Mensual", "9.99", "Sin anuncios", "4K", "Compartible en familia"),
            plan("yearly", "Anual", "99.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Sin anuncios", "Ahorro frente al mes")
        )
    ),
    service(
        id = "filmin",
        name = "Filmin",
        logoText = "F",
        categoryId = "streaming",
        accentColor = Color(0xFFF4C430),
        logoTextColor = Color(0xFF141414),
        summary = "Cine europeo, independiente, clasicos y series seleccionadas.",
        plans = listOf(
            plan("monthly", "Mensual", "9.99", "Catalogo completo", "Cine independiente", "Sin permanencia"),
            plan("yearly", "Anual", "99.00", "EUR", BillingPeriod.YEARLY, "Pago anual", "Catalogo completo", "Ahorro frente al mes")
        )
    ),
    service(
        id = "skyshowtime",
        name = "SkyShowtime",
        logoText = "S",
        categoryId = "streaming",
        accentColor = Color(0xFF6C2BD9),
        summary = "Universal, Paramount, DreamWorks, Sky y Showtime.",
        plans = listOf(
            plan("ads", "Estandar con anuncios", "4.49", "Precio equivalente semestral", "Con anuncios", "Full HD"),
            plan("standard", "Estandar", "6.99", "Sin anuncios", "Full HD", "2 dispositivos"),
            plan("premium", "Premium", "10.00", "Mas calidad", "Mas dispositivos", "Descargas")
        )
    ),
    service(
        id = "movistar-plus",
        name = "Movistar Plus+",
        logoText = "M+",
        categoryId = "streaming",
        accentColor = Color(0xFF00A9E0),
        summary = "Canales, cine, series, documentales y deporte segun plan.",
        plans = listOf(
            plan("cine-series", "Plan Libre Cine y Series", "4.99", "Cine y series", "Mas de 70 canales", "Sin permanencia"),
            plan("libre", "Plan Libre completo", "9.99", "Cine, series y deporte", "Canales en directo", "Sin permanencia")
        )
    ),
    service(
        id = "dazn",
        name = "DAZN",
        logoText = "DAZN",
        categoryId = "streaming",
        accentColor = Color(0xFF111111),
        summary = "Deporte en directo y bajo demanda, segun derechos disponibles.",
        plans = listOf(
            plan("victoria", "Victoria", "9.99", "Futbol femenino", "Documentales", "Contenido DAZN"),
            plan("esencial", "Esencial", "19.99", "Motor y deportes", "F1 y MotoGP segun temporada", "Sin futbol completo"),
            plan("total", "Total", "39.99", "Mas competiciones", "Futbol y motor", "Plan mas completo")
        )
    ),
    service(
        id = "crunchyroll",
        name = "Crunchyroll",
        logoText = "CR",
        categoryId = "streaming",
        accentColor = Color(0xFFF47521),
        summary = "Anime, simulcasts y manga seleccionado.",
        plans = listOf(
            plan("fan", "Fan", "4.99", "Sin anuncios", "1 dispositivo", "Anime en simulcast"),
            plan("mega", "Mega Fan", "6.49", "4 dispositivos", "Descargas", "Sin anuncios"),
            plan("yearly", "Mega Fan anual", "64.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Descargas", "Ahorro frente al mes")
        )
    ),
    service(
        id = "spotify",
        name = "Spotify",
        logoText = "S",
        categoryId = "music",
        accentColor = Color(0xFF1DB954),
        summary = "Musica, podcasts y audiolibros seleccionados.",
        plans = listOf(
            plan("individual", "Individual", "11.99", "1 cuenta", "Sin anuncios", "Descargas"),
            plan("duo", "Duo", "16.99", "2 cuentas", "Mismo domicilio", "Sin anuncios"),
            plan("family", "Familiar", "20.99", "Hasta 6 cuentas", "Mismo domicilio", "Control familiar"),
            plan("student", "Estudiantes", "6.49", "Verificacion estudiante", "1 cuenta", "Precio reducido")
        )
    ),
    service(
        id = "apple-music",
        name = "Apple Music",
        logoText = "AM",
        categoryId = "music",
        accentColor = Color(0xFFFA243C),
        summary = "Mas de 100 millones de canciones, lossless y audio espacial.",
        plans = listOf(
            plan("individual", "Individual", "10.99", "Sin anuncios", "Audio sin perdida", "Apple Music Classical"),
            plan("family", "Familiar", "16.99", "Hasta 6 personas", "En Familia", "Bibliotecas separadas"),
            plan("student", "Estudiante", "5.99", "Verificacion estudiante", "Incluye Apple TV", "Precio reducido")
        )
    ),
    service(
        id = "youtube-premium",
        name = "YouTube Premium",
        logoText = "YT",
        categoryId = "music",
        accentColor = Color(0xFFFF0000),
        summary = "YouTube sin anuncios, reproduccion en segundo plano y YouTube Music.",
        plans = listOf(
            plan("individual", "Individual", "13.99", "YouTube sin anuncios", "Segundo plano", "YouTube Music incluido"),
            plan("family", "Familiar", "25.99", "Hasta 5 miembros", "Grupo familiar", "YouTube Music incluido"),
            plan("student", "Estudiante", "8.99", "Verificacion estudiante", "YouTube Music incluido", "Precio reducido")
        )
    ),
    service(
        id = "youtube-music",
        name = "YouTube Music",
        logoText = "YM",
        categoryId = "music",
        accentColor = Color(0xFFFF0033),
        summary = "Musica oficial, directos, versiones y videoclips.",
        plans = listOf(
            plan("individual", "Music Premium", "10.99", "Sin anuncios", "Descargas", "Segundo plano"),
            plan("family", "Familiar", "17.99", "Hasta 5 miembros", "Grupo familiar", "Sin anuncios")
        )
    ),
    service(
        id = "amazon-music",
        name = "Amazon Music",
        logoText = "AMZ",
        categoryId = "music",
        accentColor = Color(0xFF00A8E1),
        summary = "Musica en HD y Ultra HD con integracion Alexa.",
        plans = listOf(
            plan("individual", "Unlimited Individual", "10.99", "Sin anuncios", "HD y Ultra HD", "Descargas"),
            plan("prime", "Unlimited con Prime", "9.99", "Precio para clientes Prime", "HD y Ultra HD", "Alexa"),
            plan("family", "Familiar", "17.99", "Hasta 6 cuentas", "Sin anuncios", "HD")
        )
    ),
    service(
        id = "deezer",
        name = "Deezer",
        logoText = "DZ",
        categoryId = "music",
        accentColor = Color(0xFFA238FF),
        summary = "Musica, Flow, letras y audio HiFi.",
        plans = listOf(
            plan("premium", "Premium", "11.99", "Sin anuncios", "HiFi", "Descargas"),
            plan("duo", "Duo", "15.99", "2 cuentas", "HiFi", "Sin anuncios"),
            plan("family", "Familiar", "19.99", "Hasta 6 cuentas", "HiFi", "Perfiles infantiles")
        )
    ),
    service(
        id = "tidal",
        name = "Tidal",
        logoText = "T",
        categoryId = "music",
        accentColor = Color(0xFF111111),
        summary = "Musica HiFi, Dolby Atmos y enfoque en calidad de audio.",
        plans = listOf(
            plan("individual", "Individual", "10.99", "Audio lossless", "Dolby Atmos", "Descargas"),
            plan("family", "Familiar", "16.99", "Hasta 6 cuentas", "Audio lossless", "Sin anuncios"),
            plan("student", "Estudiante", "5.49", "Verificacion estudiante", "Audio lossless", "Precio reducido")
        )
    ),
    service(
        id = "audible",
        name = "Audible",
        logoText = "A",
        categoryId = "music",
        accentColor = Color(0xFFF8991D),
        logoTextColor = Color(0xFF111111),
        summary = "Audiolibros, podcasts y contenidos originales.",
        plans = listOf(
            plan("monthly", "Mensual", "9.99", "Audiolibros", "Originales Audible", "Cancela cuando quieras")
        )
    ),
    service(
        id = "amazon-prime",
        name = "Amazon Prime",
        logoText = "prime",
        categoryId = "shopping",
        accentColor = Color(0xFF00A8E1),
        summary = "Envios Prime, Prime Video, Prime Gaming y ventajas de compra.",
        plans = listOf(
            plan("monthly", "Mensual", "4.99", "Envios rapidos", "Prime Video", "Ofertas Prime"),
            plan("yearly", "Anual", "49.90", "EUR", BillingPeriod.YEARLY, "Pago anual", "Envios rapidos", "Ahorro frente al mes"),
            plan("student", "Prime Student", "2.49", "Verificacion estudiante", "Envios Prime", "Precio reducido")
        )
    ),
    service(
        id = "uber-one",
        name = "Uber One",
        logoText = "Uber",
        categoryId = "shopping",
        accentColor = Color(0xFF111111),
        summary = "Ahorros en Uber Eats y viajes Uber elegibles.",
        plans = listOf(
            plan("monthly", "Mensual", "4.99", "Envio a 0 EUR en pedidos elegibles", "Hasta 50% en servicio", "Creditos en viajes"),
            plan("yearly", "Anual", "49.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ventajas Uber Eats", "Ahorro frente al mes")
        )
    ),
    service(
        id = "glovo-prime",
        name = "Glovo Prime",
        logoText = "G",
        categoryId = "shopping",
        accentColor = Color(0xFFFFC244),
        logoTextColor = Color(0xFF111111),
        summary = "Entregas gratis y descuentos en pedidos seleccionados.",
        plans = listOf(
            plan("prime", "Prime", "3.99", "Entregas seleccionadas", "Promociones", "Pedidos elegibles"),
            plan("unlimited", "Prime Ilimitado", "7.99", "Mas entregas incluidas", "Promociones", "Pedidos elegibles")
        )
    ),
    service(
        id = "just-eat-plus",
        name = "Just Eat Plus",
        logoText = "JE",
        categoryId = "shopping",
        accentColor = Color(0xFFFF8000),
        summary = "Programa de ventajas y envios gratis en pedidos participantes.",
        plans = listOf(
            plan("plus", "Plus", "10.99", "Envios gratis en participantes", "Ofertas", "Importe minimo segun condiciones")
        )
    ),
    service(
        id = "carrefour-plus",
        name = "Carrefour+",
        logoText = "C+",
        categoryId = "shopping",
        accentColor = Color(0xFF0050AA),
        summary = "Abono con ahorro en frescos y ventajas en Carrefour.",
        plans = listOf(
            plan("monthly", "Mi abono Carrefour+", "6.99", "15% en frescos", "Acumulado en cuenta", "Desde febrero de 2026")
        )
    ),
    service(
        id = "google-one",
        name = "Google One",
        logoText = "G1",
        categoryId = "cloud",
        accentColor = Color(0xFF4285F4),
        summary = "Almacenamiento para Google Fotos, Drive y Gmail.",
        plans = listOf(
            plan("basic", "Basic 100 GB", "1.99", "100 GB", "Compartible con 5 personas", "Google Fotos, Drive y Gmail"),
            plan("premium", "Premium 2 TB", "9.99", "2 TB", "Ventajas premium", "Compartible en familia"),
            plan("ai-pro", "Google AI Pro 2 TB", "19.99", "Gemini incluido", "2 TB", "Funciones avanzadas de IA")
        )
    ),
    service(
        id = "icloud",
        name = "iCloud+",
        logoText = "iCloud",
        categoryId = "cloud",
        accentColor = Color(0xFF2F7CFF),
        summary = "Almacenamiento Apple, privacidad y copias de seguridad.",
        plans = listOf(
            plan("50gb", "50 GB", "0.99", "Relay Privado", "Ocultar mi correo", "1 camara HomeKit"),
            plan("200gb", "200 GB", "2.99", "Compartible en familia", "5 camaras HomeKit", "Privacidad"),
            plan("2tb", "2 TB", "9.99", "Compartible en familia", "Camaras ilimitadas", "Mucho almacenamiento"),
            plan("6tb", "6 TB", "29.99", "Compartible en familia", "Camaras ilimitadas", "Uso intensivo"),
            plan("12tb", "12 TB", "59.99", "Compartible en familia", "Camaras ilimitadas", "Maxima capacidad")
        )
    ),
    service(
        id = "microsoft-365",
        name = "Microsoft 365",
        logoText = "365",
        categoryId = "cloud",
        accentColor = Color(0xFFF25022),
        summary = "Office, OneDrive, Outlook y funciones de Copilot segun plan.",
        plans = listOf(
            plan("basic", "Basico", "2.00", "100 GB", "Outlook", "Proteccion y almacenamiento"),
            plan("personal", "Personal", "10.00", "1 TB", "Apps Office", "1 persona"),
            plan("family", "Familia", "13.00", "Hasta 6 personas", "Hasta 6 TB", "Apps Office")
        )
    ),
    service(
        id = "dropbox",
        name = "Dropbox",
        logoText = "DB",
        categoryId = "cloud",
        accentColor = Color(0xFF0061FF),
        summary = "Almacenamiento, sincronizacion, transferencias y firma.",
        plans = listOf(
            plan("plus", "Plus", "9.99", "USD", BillingPeriod.MONTHLY, "2 TB", "1 persona", "Transferencias hasta 50 GB"),
            plan("family", "Family", "16.99", "USD", BillingPeriod.MONTHLY, "Hasta 6 usuarios", "2 TB", "Carpeta familiar"),
            plan("professional", "Professional", "16.58", "USD", BillingPeriod.MONTHLY, "3 TB", "Controles avanzados", "Transferencias hasta 100 GB")
        )
    ),
    service(
        id = "canva",
        name = "Canva Pro",
        logoText = "Canva",
        categoryId = "cloud",
        accentColor = Color(0xFF00C4CC),
        summary = "Diseno online, plantillas premium, marca e IA creativa.",
        plans = listOf(
            plan("pro", "Pro mensual", "11.99", "Contenido premium", "Kit de marca", "Herramientas de IA"),
            plan("pro-yearly", "Pro anual", "109.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Contenido premium", "Ahorro frente al mes")
        )
    ),
    service(
        id = "adobe-cc",
        name = "Adobe Creative Cloud",
        logoText = "Adobe",
        categoryId = "cloud",
        accentColor = Color(0xFFFF0000),
        summary = "Photoshop, Premiere, Illustrator, Firefly y herramientas creativas.",
        plans = listOf(
            plan("single", "Aplicacion unica", "26.43", "1 app", "100 GB", "Creditos generativos"),
            plan("photo", "Fotografia", "24.19", "Lightroom y Photoshop", "Almacenamiento", "Fotografia"),
            plan("all", "Creative Cloud Pro", "79.30", "Mas de 20 apps", "IA creativa", "100 GB")
        )
    ),
    service(
        id = "playstation-plus",
        name = "PlayStation Plus",
        logoText = "PS+",
        categoryId = "gaming",
        accentColor = Color(0xFF003791),
        summary = "Multijugador online, juegos mensuales y catalogos segun nivel.",
        plans = listOf(
            plan("essential", "Essential", "8.99", "Juegos mensuales", "Online", "Descuentos"),
            plan("extra", "Extra", "13.99", "Catalogo de juegos", "Ubisoft+ Classics", "Online"),
            plan("premium", "Premium", "16.99", "Clasicos", "Pruebas", "Streaming en la nube")
        )
    ),
    service(
        id = "xbox-game-pass",
        name = "Xbox Game Pass",
        logoText = "XGP",
        categoryId = "gaming",
        accentColor = Color(0xFF107C10),
        summary = "Catalogo de juegos para consola, PC y nube segun plan.",
        plans = listOf(
            plan("pc", "PC Game Pass", "13.99", "EUR", BillingPeriod.MONTHLY, "Catalogo PC", "EA Play", "Juegos seleccionados"),
            plan("premium", "Premium", "14.99", "EUR", BillingPeriod.MONTHLY, "Catalogo consola", "Online", "Sin lanzamientos dia 1 en todos los casos"),
            plan("ultimate", "Ultimate", "19.99", "EUR", BillingPeriod.MONTHLY, "Consola, PC y nube", "EA Play", "Mas ventajas")
        )
    ),
    service(
        id = "nintendo-online",
        name = "Nintendo Switch Online",
        logoText = "NSO",
        categoryId = "gaming",
        accentColor = Color(0xFFE60012),
        summary = "Juego online, clasicos, guardado en la nube y paquete de expansion.",
        plans = listOf(
            plan("individual", "Individual anual", "19.99", "EUR", BillingPeriod.YEARLY, "Online", "Clasicos NES/SNES/Game Boy", "Guardado en la nube"),
            plan("family", "Familiar anual", "34.99", "EUR", BillingPeriod.YEARLY, "Hasta 8 cuentas", "Online", "Guardado en la nube"),
            plan("expansion", "Expansion anual", "39.99", "EUR", BillingPeriod.YEARLY, "Nintendo 64 y Game Boy Advance", "DLCs seleccionados", "Online")
        )
    ),
    service(
        id = "geforce-now",
        name = "GeForce Now",
        logoText = "GFN",
        categoryId = "gaming",
        accentColor = Color(0xFF76B900),
        logoTextColor = Color(0xFF111111),
        summary = "Juego en la nube para tu biblioteca compatible.",
        plans = listOf(
            plan("performance", "Performance", "10.99", "Colas prioritarias", "Sesiones largas", "Hasta 1440p segun dispositivo"),
            plan("ultimate", "Ultimate", "21.99", "RTX 4080/5080 segun region", "4K", "Maximo rendimiento")
        )
    ),
    service(
        id = "ea-play",
        name = "EA Play",
        logoText = "EA",
        categoryId = "gaming",
        accentColor = Color(0xFFFF4747),
        summary = "Catalogo EA, pruebas anticipadas y descuentos.",
        plans = listOf(
            plan("standard", "EA Play", "5.99", "Catalogo EA", "Pruebas de juegos", "10% descuento"),
            plan("pro", "EA Play Pro", "16.99", "Lanzamientos premium", "PC", "Recompensas")
        )
    ),
    service(
        id = "chatgpt",
        name = "ChatGPT",
        logoText = "GPT",
        categoryId = "ai",
        accentColor = Color(0xFF10A37F),
        summary = "Asistente de IA para texto, voz, imagen, analisis y automatizacion.",
        plans = listOf(
            plan("plus", "Plus", "20.00", "USD", BillingPeriod.MONTHLY, "Limites ampliados", "Voz y archivos", "Modelos avanzados"),
            plan("pro", "Pro", "200.00", "USD", BillingPeriod.MONTHLY, "Maximo acceso", "Investigacion profunda", "Funciones anticipadas")
        )
    ),
    service(
        id = "claude",
        name = "Claude",
        logoText = "Claude",
        categoryId = "ai",
        accentColor = Color(0xFFD97757),
        summary = "Asistente de Anthropic para escritura, analisis, codigo y documentos.",
        plans = listOf(
            plan("pro", "Pro mensual", "20.00", "USD", BillingPeriod.MONTHLY, "Mas uso", "Proyectos", "Claude Code"),
            plan("pro-yearly", "Pro anual", "200.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Descuento", "Mas uso"),
            plan("max", "Max", "100.00", "USD", BillingPeriod.MONTHLY, "Mas limites", "Prioridad", "Funciones anticipadas")
        )
    ),
    service(
        id = "gemini",
        name = "Google AI Pro",
        logoText = "Gemini",
        categoryId = "ai",
        accentColor = Color(0xFF4285F4),
        summary = "Gemini avanzado junto a almacenamiento de Google One.",
        plans = listOf(
            plan("ai-pro", "AI Pro", "19.99", "Gemini avanzado", "2 TB", "NotebookLM y Workspace segun disponibilidad"),
            plan("ai-ultra", "AI Ultra", "249.99", "Limites mas altos", "Funciones avanzadas", "Almacenamiento ampliado")
        )
    ),
    service(
        id = "perplexity",
        name = "Perplexity",
        logoText = "P",
        categoryId = "ai",
        accentColor = Color(0xFF20B8CD),
        summary = "Busqueda con IA, respuestas con fuentes y modelos avanzados.",
        plans = listOf(
            plan("pro", "Pro", "20.00", "USD", BillingPeriod.MONTHLY, "Busquedas Pro", "Modelos avanzados", "Carga de archivos"),
            plan("pro-yearly", "Pro anual", "200.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro", "Modelos avanzados")
        )
    ),
    service(
        id = "notion-ai",
        name = "Notion AI",
        logoText = "N",
        categoryId = "ai",
        accentColor = Color(0xFF111111),
        summary = "IA integrada en Notion para escribir, resumir y consultar espacios de trabajo.",
        plans = listOf(
            plan("ai", "Notion AI", "10.00", "USD", BillingPeriod.MONTHLY, "Complemento IA", "Resumenes", "Preguntas sobre workspace")
        )
    ),
    service(
        id = "atresplayer",
        name = "atresplayer",
        logoText = "AT",
        categoryId = "streaming",
        accentColor = Color(0xFFFF6A00),
        summary = "Originales, preestrenos, television a la carta y plan familiar.",
        plans = listOf(
            plan("ads", "Premium con anuncios", "5.99", "1080p", "1 perfil", "Catalogo y preestrenos"),
            plan("no-ads", "Premium sin anuncios", "7.99", "4K", "Descarga offline", "Sin publicidad"),
            plan("family", "Premium familiar", "9.99", "4 perfiles", "4K", "Sin publicidad"),
            plan("yearly", "Premium anual", "79.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Sin anuncios", "Dos meses gratis")
        )
    ),
    service(
        id = "mitele-plus",
        name = "mitele PLUS",
        logoText = "mt+",
        categoryId = "streaming",
        accentColor = Color(0xFF1C7ED6),
        summary = "Contenido de Mediaset, directos, programas y planes mensual/anual.",
        plans = listOf(
            plan("basic", "Plan basico", "5.00", "Contenido mitele PLUS", "Directos y programas", "Cancela cuando quieras"),
            plan("family", "Plan familiar", "8.00", "Mas perfiles", "Contenido PLUS", "Uso compartido"),
            plan("basic-yearly", "Basico anual", "42.00", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Mismo plan basico")
        )
    ),
    service(
        id = "flixole",
        name = "FlixOle",
        logoText = "Fx",
        categoryId = "streaming",
        accentColor = Color(0xFFE8D08C),
        logoTextColor = Color(0xFF111111),
        summary = "Cine espanol, clasicos, series y catalogo europeo.",
        plans = listOf(
            plan("monthly", "Mensual", "4.99", "Catalogo completo", "14 dias gratis", "Sin permanencia"),
            plan("yearly", "Anual", "49.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Catalogo completo", "Ahorro frente al mes")
        )
    ),
    service(
        id = "mubi",
        name = "MUBI",
        logoText = "M",
        categoryId = "streaming",
        accentColor = Color(0xFF111111),
        summary = "Cine de autor, peliculas seleccionadas y estrenos curados.",
        plans = listOf(
            plan("monthly", "Mensual", "12.99", "Cine curado", "Sin anuncios", "Descargas"),
            plan("yearly", "Anual", "95.88", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Catalogo MUBI")
        )
    ),
    service(
        id = "podimo",
        name = "Podimo",
        logoText = "P",
        categoryId = "music",
        accentColor = Color(0xFFE84D7A),
        summary = "Podcasts exclusivos, audiolibros y escucha offline.",
        plans = listOf(
            plan("monthly", "Mensual", "4.99", "Podcasts exclusivos", "20h de audiolibros", "Modo offline"),
            plan("yearly", "Anual", "49.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Paga 10 meses", "30 dias gratis")
        )
    ),
    service(
        id = "storytel",
        name = "Storytel",
        logoText = "ST",
        categoryId = "music",
        accentColor = Color(0xFFFF5B35),
        summary = "Audiolibros, ebooks, originales y modo infantil.",
        plans = listOf(
            plan("unlimited", "Unlimited", "8.99", "1 cuenta", "Escucha y lectura ilimitada", "Offline y Kids Mode"),
            plan("family", "Family", "15.99", "2 cuentas", "Acceso ilimitado", "Compartido")
        )
    ),
    service(
        id = "everand",
        name = "Everand",
        logoText = "E",
        categoryId = "music",
        accentColor = Color(0xFF111111),
        summary = "Ebooks, audiolibros, revistas, podcasts y documentos seleccionados.",
        plans = listOf(
            plan("monthly", "Mensual", "11.99", "USD", BillingPeriod.MONTHLY, "Lectura y audio", "Catalogo Everand", "Cancela cuando quieras"),
            plan("yearly", "Anual", "119.99", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Catalogo Everand")
        )
    ),
    service(
        id = "duolingo",
        name = "Super Duolingo",
        logoText = "D",
        categoryId = "learning",
        accentColor = Color(0xFF58CC02),
        logoTextColor = Color(0xFF111111),
        summary = "Idiomas gamificados sin anuncios, corazones ilimitados y practica personalizada.",
        plans = listOf(
            plan("super", "Super", "13.99", "Sin anuncios", "Corazones ilimitados", "Practica personalizada"),
            plan("super-yearly", "Super anual", "87.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Mismas funciones Super"),
            plan("family", "Super familiar", "122.99", "EUR", BillingPeriod.YEARLY, "Hasta 6 cuentas", "Pago anual", "Plan familiar")
        )
    ),
    service(
        id = "babbel",
        name = "Babbel",
        logoText = "Bb",
        categoryId = "learning",
        accentColor = Color(0xFFFFB000),
        logoTextColor = Color(0xFF111111),
        summary = "Cursos de idiomas por niveles, dialogos y repasos.",
        plans = listOf(
            plan("monthly", "Mensual", "12.99", "1 idioma", "Lecciones guiadas", "Repaso"),
            plan("yearly", "Anual", "83.88", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "1 idioma"),
            plan("lifetime", "Lifetime", "199.99", "EUR", BillingPeriod.YEARLY, "Pago unico orientativo", "Todos los idiomas", "Sin renovacion")
        )
    ),
    service(
        id = "busuu",
        name = "Busuu",
        logoText = "Bu",
        categoryId = "learning",
        accentColor = Color(0xFF2E68FF),
        summary = "Idiomas con plan de estudio, comunidad y certificados.",
        plans = listOf(
            plan("monthly", "Premium mensual", "13.98", "Lecciones completas", "Sin anuncios", "Modo offline"),
            plan("yearly", "Premium anual", "83.76", "EUR", BillingPeriod.YEARLY, "Pago anual", "Certificados", "Ahorro frente al mes")
        )
    ),
    service(
        id = "coursera-plus",
        name = "Coursera Plus",
        logoText = "C",
        categoryId = "learning",
        accentColor = Color(0xFF2A73CC),
        summary = "Cursos y certificados profesionales de universidades y empresas.",
        plans = listOf(
            plan("monthly", "Plus mensual", "59.00", "USD", BillingPeriod.MONTHLY, "Catalogo Plus", "Certificados", "Cancela cuando quieras"),
            plan("yearly", "Plus anual", "399.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Certificados incluidos")
        )
    ),
    service(
        id = "skillshare",
        name = "Skillshare",
        logoText = "S",
        categoryId = "learning",
        accentColor = Color(0xFF00FF84),
        logoTextColor = Color(0xFF111111),
        summary = "Clases creativas online de diseno, foto, negocio y productividad.",
        plans = listOf(
            plan("monthly", "Mensual", "29.00", "USD", BillingPeriod.MONTHLY, "Clases ilimitadas", "Proyectos", "Offline en app"),
            plan("yearly", "Anual", "168.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Clases ilimitadas")
        )
    ),
    service(
        id = "masterclass",
        name = "MasterClass",
        logoText = "MC",
        categoryId = "learning",
        accentColor = Color(0xFFE74C3C),
        summary = "Clases online impartidas por expertos y creadores reconocidos.",
        plans = listOf(
            plan("individual", "Individual anual", "120.00", "USD", BillingPeriod.YEARLY, "1 dispositivo", "Catalogo completo", "Pago anual"),
            plan("duo", "Duo anual", "180.00", "USD", BillingPeriod.YEARLY, "2 dispositivos", "Descargas", "Pago anual"),
            plan("family", "Family anual", "240.00", "USD", BillingPeriod.YEARLY, "6 dispositivos", "Descargas", "Pago anual")
        )
    ),
    service(
        id = "proton-unlimited",
        name = "Proton Unlimited",
        logoText = "P",
        categoryId = "security",
        accentColor = Color(0xFF6D4AFF),
        summary = "Correo, VPN, Drive, Pass y Calendar con enfoque en privacidad.",
        plans = listOf(
            plan("monthly", "Mensual", "12.99", "EUR", BillingPeriod.MONTHLY, "Mail, VPN y Drive", "Pass incluido", "1 usuario"),
            plan("yearly", "Anual", "119.88", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Suite Proton")
        )
    ),
    service(
        id = "nordvpn",
        name = "NordVPN",
        logoText = "N",
        categoryId = "security",
        accentColor = Color(0xFF4687FF),
        summary = "VPN multiplataforma con proteccion contra amenazas segun plan.",
        plans = listOf(
            plan("monthly", "Standard mensual", "12.99", "VPN", "10 dispositivos", "Sin permanencia"),
            plan("yearly", "Standard anual", "59.88", "EUR", BillingPeriod.YEARLY, "Pago anual", "VPN", "Ahorro promocional variable")
        )
    ),
    service(
        id = "1password",
        name = "1Password",
        logoText = "1P",
        categoryId = "security",
        accentColor = Color(0xFF2368D5),
        summary = "Gestor de contrasenas, passkeys, tarjetas y secretos familiares.",
        plans = listOf(
            plan("individual", "Individual", "2.99", "USD", BillingPeriod.MONTHLY, "Facturacion anual", "Apps ilimitadas", "1 GB documentos"),
            plan("families", "Families", "4.99", "USD", BillingPeriod.MONTHLY, "Facturacion anual", "Hasta 5 personas", "Cuentas familiares")
        )
    ),
    service(
        id = "todoist",
        name = "Todoist",
        logoText = "T",
        categoryId = "cloud",
        accentColor = Color(0xFFE44332),
        summary = "Tareas, proyectos, filtros, recordatorios y calendario.",
        plans = listOf(
            plan("pro", "Pro mensual", "5.00", "USD", BillingPeriod.MONTHLY, "Recordatorios", "Calendario", "Filtros"),
            plan("pro-yearly", "Pro anual", "48.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Funciones Pro")
        )
    ),
    service(
        id = "github-copilot",
        name = "GitHub Copilot",
        logoText = "GH",
        categoryId = "ai",
        accentColor = Color(0xFF111111),
        summary = "Asistente de programacion integrado en IDEs y GitHub.",
        plans = listOf(
            plan("pro", "Pro", "10.00", "USD", BillingPeriod.MONTHLY, "Completado de codigo", "Chat", "Modelos avanzados"),
            plan("pro-plus", "Pro+", "39.00", "USD", BillingPeriod.MONTHLY, "Mas limites", "Modelos premium", "Funciones avanzadas")
        )
    ),
    service(
        id = "cursor",
        name = "Cursor",
        logoText = "C",
        categoryId = "ai",
        accentColor = Color(0xFF111111),
        summary = "Editor de codigo con IA, agentes, autocompletado y contexto de repo.",
        plans = listOf(
            plan("pro", "Pro", "20.00", "USD", BillingPeriod.MONTHLY, "Uso ampliado", "Agentes", "Autocompletado"),
            plan("pro-yearly", "Pro anual", "192.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Funciones Pro")
        )
    ),
    service(
        id = "midjourney",
        name = "Midjourney",
        logoText = "MJ",
        categoryId = "ai",
        accentColor = Color(0xFF111111),
        summary = "Generacion de imagenes por IA con planes por tiempo GPU.",
        plans = listOf(
            plan("basic", "Basic", "10.00", "USD", BillingPeriod.MONTHLY, "GPU limitada", "Galeria", "Uso personal"),
            plan("standard", "Standard", "30.00", "USD", BillingPeriod.MONTHLY, "Mas GPU", "Relax mode", "Uso frecuente"),
            plan("basic-yearly", "Basic anual", "96.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro", "GPU limitada")
        )
    ),
    service(
        id = "apple-arcade",
        name = "Apple Arcade",
        logoText = "A",
        categoryId = "gaming",
        accentColor = Color(0xFF111111),
        summary = "Catalogo de juegos sin anuncios ni compras dentro de la app.",
        plans = listOf(
            plan("monthly", "Mensual", "6.99", "Juegos Apple Arcade", "Compartir en familia", "Sin anuncios"),
            plan("apple-one", "Incluido en Apple One", "19.95", "Bundle Apple", "Servicios combinados", "Precio segun plan")
        )
    ),
    service(
        id = "google-play-pass",
        name = "Google Play Pass",
        logoText = "GP",
        categoryId = "gaming",
        accentColor = Color(0xFF4285F4),
        summary = "Apps y juegos Android sin anuncios ni compras en la app.",
        plans = listOf(
            plan("monthly", "Mensual", "4.99", "Apps y juegos", "Sin anuncios", "Compartible en familia"),
            plan("yearly", "Anual", "29.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Catalogo Play Pass")
        )
    ),
    service(
        id = "ubisoft-plus",
        name = "Ubisoft+",
        logoText = "U+",
        categoryId = "gaming",
        accentColor = Color(0xFF111111),
        summary = "Catalogo Ubisoft, ediciones premium y acceso dia uno segun plan.",
        plans = listOf(
            plan("classics", "Classics", "7.99", "Catalogo clasico", "PC", "Juegos seleccionados"),
            plan("premium", "Premium", "17.99", "Novedades", "DLC y ediciones premium", "PC y nube segun disponibilidad")
        )
    ),
    service(
        id = "roblox-premium",
        name = "Roblox Premium",
        logoText = "R",
        categoryId = "gaming",
        accentColor = Color(0xFF111111),
        summary = "Robux mensuales, ventajas de mercado y beneficios en experiencias.",
        plans = listOf(
            plan("450", "Premium 450", "5.99", "450 Robux/mes", "Ventajas premium", "Renovacion mensual"),
            plan("1000", "Premium 1000", "11.99", "1000 Robux/mes", "Ventajas premium", "Renovacion mensual"),
            plan("2200", "Premium 2200", "23.99", "2200 Robux/mes", "Ventajas premium", "Renovacion mensual")
        )
    ),
    service(
        id = "fortnite-crew",
        name = "Fortnite Crew",
        logoText = "F",
        categoryId = "gaming",
        accentColor = Color(0xFF7A5CFF),
        summary = "Suscripcion mensual de Fortnite con pase, pavos y recompensas.",
        plans = listOf(
            plan("monthly", "Crew mensual", "11.99", "Pase de batalla", "1000 paVos", "Pack mensual")
        )
    ),
    service(
        id = "humble-choice",
        name = "Humble Choice",
        logoText = "H",
        categoryId = "gaming",
        accentColor = Color(0xFFCC2929),
        summary = "Juegos PC mensuales, Humble Vault y descuentos en tienda.",
        plans = listOf(
            plan("monthly", "Mensual", "11.99", "USD", BillingPeriod.MONTHLY, "Juegos mensuales", "Descuentos", "Cancela cuando quieras"),
            plan("yearly", "Anual", "129.00", "USD", BillingPeriod.YEARLY, "Pago anual", "Juegos mensuales", "Ahorro frente al mes")
        )
    ),
    service(
        id = "strava",
        name = "Strava",
        logoText = "S",
        categoryId = "wellness",
        accentColor = Color(0xFFFC4C02),
        summary = "Seguimiento deportivo, segmentos, rutas y analisis de entrenamiento.",
        plans = listOf(
            plan("monthly", "Mensual", "11.99", "Analisis", "Rutas", "Segmentos"),
            plan("yearly", "Anual", "79.99", "EUR", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Funciones premium")
        )
    ),
    service(
        id = "calm",
        name = "Calm",
        logoText = "Ca",
        categoryId = "wellness",
        accentColor = Color(0xFF2A73CC),
        summary = "Meditacion, sueno, musica relajante y programas guiados.",
        plans = listOf(
            plan("monthly", "Mensual", "14.99", "USD", BillingPeriod.MONTHLY, "Meditaciones", "Sleep Stories", "Musica"),
            plan("yearly", "Anual", "69.99", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Catalogo completo")
        )
    ),
    service(
        id = "headspace",
        name = "Headspace",
        logoText = "H",
        categoryId = "wellness",
        accentColor = Color(0xFFFF7F32),
        summary = "Meditacion, mindfulness, foco, sueno y bienestar mental.",
        plans = listOf(
            plan("monthly", "Mensual", "12.99", "USD", BillingPeriod.MONTHLY, "Meditaciones", "Sueno", "Cursos"),
            plan("yearly", "Anual", "69.99", "USD", BillingPeriod.YEARLY, "Pago anual", "Ahorro frente al mes", "Catalogo completo")
        )
    ),
    service(
        id = "peloton",
        name = "Peloton",
        logoText = "P",
        categoryId = "wellness",
        accentColor = Color(0xFFE62D2E),
        summary = "Clases de fitness online, fuerza, bici, yoga y entrenamientos.",
        plans = listOf(
            plan("app-one", "App One", "12.99", "USD", BillingPeriod.MONTHLY, "Clases seleccionadas", "Entrenamientos", "App"),
            plan("app-plus", "App+", "24.00", "USD", BillingPeriod.MONTHLY, "Mas clases", "Equipos Peloton", "Metricas")
        )
    )
)

fun findSubscriptionTemplate(serviceId: String): SubscriptionTemplate? {
    return PopularSubscriptionTemplates.firstOrNull { it.id == serviceId }
}

fun findSubscriptionPlan(serviceId: String, planId: String): SubscriptionPlanTemplate? {
    return findSubscriptionTemplate(serviceId)?.plans?.firstOrNull { it.id == planId }
}

fun templatesForCategory(categoryId: String): List<SubscriptionTemplate> {
    return PopularSubscriptionTemplates.filter { it.categoryId == categoryId }
}

private fun service(
    id: String,
    name: String,
    logoText: String,
    categoryId: String,
    accentColor: Color,
    logoTextColor: Color = Color.White,
    summary: String,
    plans: List<SubscriptionPlanTemplate>
): SubscriptionTemplate {
    return SubscriptionTemplate(
        id = id,
        name = name,
        logoText = logoText,
        categoryId = categoryId,
        accentColor = accentColor,
        logoTextColor = logoTextColor,
        summary = summary,
        plans = plans
    )
}

private fun plan(
    id: String,
    name: String,
    price: String,
    vararg details: String
): SubscriptionPlanTemplate {
    return SubscriptionPlanTemplate(
        id = id,
        name = name,
        price = price,
        details = details.toList()
    )
}

private fun plan(
    id: String,
    name: String,
    price: String,
    currencyLabel: String,
    period: BillingPeriod,
    vararg details: String
): SubscriptionPlanTemplate {
    return SubscriptionPlanTemplate(
        id = id,
        name = name,
        price = price,
        currencyLabel = currencyLabel,
        period = period,
        details = details.toList()
    )
}

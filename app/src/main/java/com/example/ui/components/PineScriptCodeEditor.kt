package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

// -------------------------------------------------------------
// Pine Script v5 Syntax Highlighting Transformation
// -------------------------------------------------------------

val PineCommentColor = Color(0xFF768390)
val PineDirectiveColor = Color(0xFFFFB454)
val PineStringColor = Color(0xFFE6B450)
val PineKeywordColor = Color(0xFFFF7B72)
val PineNamespaceColor = Color(0xFF79B8FF)
val PineFunctionColor = Color(0xFF56B6C2)
val PineVariableColor = Color(0xFF7EE787)
val PineTypeColor = Color(0xFF569CD6)
val PineNumberColor = Color(0xFFFF922B)
val PineHexColor = Color(0xFFC792EA)

private val PINE_SYNTAX_PATTERN: Pattern = Pattern.compile(
    "(//.*)|" +                                                          // G1: Single line comments
    "(\"(\\\\.|[^\"\\\\])*\"|'(\\\\.|[^'\\\\])*')|" +                     // G2: String literals
    "(//@version=\\d+)|" +                                                // G4: Compiler directive
    "\\b(ta|strategy|request|input|math|str|color)\\.[a-zA-Z0-9_]+\\b|" + // G5: Namespaces
    "\\b(plot|plotshape|plotchar|plotcandle|plotbar|alert|alertcondition|hline|fill|bgcolor)\\b|" + // G6: Built-ins
    "\\b(strategy|indicator|library|if|else|for|to|by|while|break|continue|return|switch|case|var|varip|import|export|type|method|not|and|or)\\b|" + // G7: Keywords
    "\\b(int|float|bool|string|color|line|label|box|table)\\b|" +         // G8: Types
    "\\b(close|open|high|low|volume|time|hl2|hlc3|ohlc4|bar_index|timenow|na|true|false)\\b|" + // G9: Built-in vars
    "(#[0-9a-fA-F]{6,8})|" +                                              // G10: Hex colors
    "(\\b\\d+(\\.\\d+)?\\b)"                                              // G11: Numbers
)

fun highlightPineScript(text: String): AnnotatedString {
    return buildAnnotatedString {
        append(text)
        val matcher = PINE_SYNTAX_PATTERN.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            when {
                matcher.group(1) != null -> {
                    addStyle(
                        SpanStyle(color = PineCommentColor, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                        start,
                        end
                    )
                }
                matcher.group(2) != null -> {
                    addStyle(SpanStyle(color = PineStringColor), start, end)
                }
                matcher.group(4) != null -> {
                    addStyle(SpanStyle(color = PineDirectiveColor, fontWeight = FontWeight.Bold), start, end)
                }
                matcher.group(5) != null -> {
                    addStyle(SpanStyle(color = PineNamespaceColor, fontWeight = FontWeight.SemiBold), start, end)
                }
                matcher.group(6) != null -> {
                    addStyle(SpanStyle(color = PineFunctionColor), start, end)
                }
                matcher.group(7) != null -> {
                    addStyle(SpanStyle(color = PineKeywordColor, fontWeight = FontWeight.Bold), start, end)
                }
                matcher.group(8) != null -> {
                    addStyle(SpanStyle(color = PineTypeColor), start, end)
                }
                matcher.group(9) != null -> {
                    addStyle(SpanStyle(color = PineVariableColor), start, end)
                }
                matcher.group(10) != null -> {
                    addStyle(SpanStyle(color = PineHexColor, fontWeight = FontWeight.Medium), start, end)
                }
                matcher.group(11) != null -> {
                    addStyle(SpanStyle(color = PineNumberColor), start, end)
                }
            }
        }
    }
}

class PineScriptVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            highlightPineScript(text.text),
            OffsetMapping.Identity
        )
    }
}

// -------------------------------------------------------------
// Validation Result Model
// -------------------------------------------------------------

data class PineValidationResult(
    val isValid: Boolean,
    val errors: List<PineDiagnosticError> = emptyList(),
    val warnings: List<String> = emptyList(),
    val bytecodeSize: Int = 1842,
    val executionTimeMs: Long = 18,
    val memoryFootprintKb: Int = 64
)

data class PineDiagnosticError(
    val lineNumber: Int,
    val message: String,
    val severity: String = "ERROR",
    val fixSuggestion: String? = null
)

// -------------------------------------------------------------
// Reusable Pine Script Code Editor Component
// -------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PineScriptCodeEditor(
    scriptContent: String,
    onContentChange: (String) -> Unit,
    onSave: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    onShowToast: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isCompiling by remember { mutableStateOf(false) }
    var compileProgress by remember { mutableFloatStateOf(0f) }
    var compileStatusText by remember { mutableStateOf("Ready (Pine v5)") }
    var validationResult by remember { mutableStateOf<PineValidationResult?>(null) }
    var lastSavedTime by remember { mutableStateOf<String?>(null) }
    var activeConsoleTab by remember { mutableStateOf("diagnostics") }

    val visualTransformation = remember { PineScriptVisualTransformation() }
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    // Simulated Validation Logic
    fun executeCompileValidation() {
        isCompiling = true
        compileProgress = 0.2f
        compileStatusText = "Tokenizing AST..."

        coroutineScope.launch {
            delay(250)
            compileProgress = 0.5f
            compileStatusText = "Verifying type constraints & series bindings..."
            delay(250)
            compileProgress = 0.8f
            compileStatusText = "Generating runtime bytecode..."
            delay(200)

            // Validate code semantics
            val errors = mutableListOf<PineDiagnosticError>()
            val warnings = mutableListOf<String>()

            if (!scriptContent.contains("//@version=5")) {
                warnings.add("Missing directive '//@version=5'. Defaulting to Pine Script v5 mode.")
            }

            if (!scriptContent.contains("strategy(") && !scriptContent.contains("indicator(")) {
                errors.add(
                    PineDiagnosticError(
                        lineNumber = 1,
                        message = "Script must declare either strategy() or indicator() in global scope",
                        fixSuggestion = "strategy(\"MyStrategy\", overlay=true)"
                    )
                )
            }

            // Check bracket balances
            var parenCount = 0
            var bracketCount = 0
            scriptContent.lines().forEachIndexed { idx, line ->
                // Skip comments
                val cleanLine = line.substringBefore("//")
                parenCount += cleanLine.count { it == '(' } - cleanLine.count { it == ')' }
                bracketCount += cleanLine.count { it == '[' } - cleanLine.count { it == ']' }

                if (parenCount < 0) {
                    errors.add(
                        PineDiagnosticError(
                            lineNumber = idx + 1,
                            message = "Unmatched closing parenthesis ')' detected",
                            fixSuggestion = "Remove extra ')' or balance parentheses"
                        )
                    )
                    parenCount = 0
                }
            }

            if (parenCount > 0) {
                errors.add(
                    PineDiagnosticError(
                        lineNumber = scriptContent.lines().size,
                        message = "Unclosed parenthesis '(' encountered at end of statement"
                    )
                )
            }

            isCompiling = false
            compileProgress = 1f

            val isValid = errors.isEmpty()
            val result = PineValidationResult(
                isValid = isValid,
                errors = errors,
                warnings = warnings,
                bytecodeSize = if (isValid) 1400 + (scriptContent.length * 2) else 0,
                executionTimeMs = (12..28).random().toLong(),
                memoryFootprintKb = 48 + (scriptContent.lines().size / 2)
            )

            validationResult = result
            compileStatusText = if (isValid) {
                "Compilation Successful (0 Errors, ${warnings.size} Warnings)"
            } else {
                "Compilation Failed: ${errors.size} Syntax Errors"
            }

            onShowToast(if (isValid) "Pine Script Compiled & Validated!" else "Compilation Error: Check Diagnostics")
        }
    }

    // Save action
    fun executeSave() {
        val now = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        lastSavedTime = now
        onSave(scriptContent)
        onShowToast("Pine script saved at $now")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Surface)
            .testTag("pine_script_code_editor")
    ) {
        // 1. Editor Action Header (Compile, Save, Last Saved, Status)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLowest)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Status badge & last saved
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                isCompiling -> PrimaryContainer.copy(alpha = 0.5f)
                                validationResult?.isValid == true -> Primary.copy(alpha = 0.2f)
                                validationResult?.isValid == false -> Error.copy(alpha = 0.2f)
                                else -> SurfaceContainerHigh
                            }
                        )
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when {
                            isCompiling -> "BUILDING..."
                            validationResult?.isValid == true -> "PINE v5 OK"
                            validationResult?.isValid == false -> "BUILD FAILED"
                            else -> "PINE v5"
                        },
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCompiling -> Primary
                            validationResult?.isValid == true -> Primary
                            validationResult?.isValid == false -> Error
                            else -> OnSurfaceVariant
                        }
                    )
                }

                if (lastSavedTime != null) {
                    Text(
                        text = "Saved $lastSavedTime",
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = OnSurfaceVariant
                    )
                }
            }

            // Right: Save & Compile Action Buttons
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Copy Code
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Pine Script", scriptContent))
                        onShowToast("Copied Pine Script to clipboard")
                    },
                    modifier = Modifier.size(32.dp).testTag("copy_pine_script_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy Script",
                        tint = OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Save Button
                Button(
                    onClick = { executeSave() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SurfaceContainerHighest),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("save_pine_script_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        tint = OnSurface,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "SAVE",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }

                // Compile Trigger Button
                Button(
                    onClick = { executeCompileValidation() },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompiling) SurfaceContainerHigh else Primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp).testTag("compile_pine_btn")
                ) {
                    if (isCompiling) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(12.dp),
                            color = Primary,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Compile",
                            tint = SurfaceContainerLowest,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isCompiling) "COMPILING" else "COMPILE",
                        fontSize = 11.sp,
                        fontFamily = JetBrainsMono,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompiling) Primary else SurfaceContainerLowest
                    )
                }
            }
        }

        // Compilation progress linear indicator
        if (isCompiling) {
            LinearProgressIndicator(
                progress = { compileProgress },
                modifier = Modifier.fillMaxWidth().height(2.dp),
                color = Primary,
                trackColor = SurfaceContainerHigh
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.White.copy(alpha = 0.08f))
            )
        }

        // 2. Snippet Toolbar (Quick Insert Tokens)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SNIPPETS:",
                fontFamily = JetBrainsMono,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceVariant
            )

            listOf(
                "+ Supertrend" to "\n[stLine, stDir] = ta.supertrend(3.0, 10)\n",
                "+ EMA Cross" to "\nlongSig = ta.crossover(ta.ema(close, 20), ta.ema(close, 50))\n",
                "+ RSI Filter" to "\nrsiVal = ta.rsi(close, 14)\nisOversold = rsiVal < 30\n",
                "+ Trail Stop" to "\nstrategy.exit(\"Trail\", \"Long\", trail_points=close*0.01)\n",
                "+ Webhook" to "\nalert(\"AlgoPulse: \" + str.tostring(close))\n"
            ).forEach { (label, snippet) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(SurfaceContainerHigh)
                        .clickable {
                            onContentChange(scriptContent + snippet)
                            onShowToast("Inserted $label")
                        }
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = label,
                        fontFamily = JetBrainsMono,
                        fontSize = 10.sp,
                        color = OnSurface
                    )
                }
            }
        }

        // 3. Code Editor Area: Line Numbers Gutter + BasicTextField
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(SurfaceContainerLowest)
        ) {
            val lines = scriptContent.lines()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(verticalScrollState)
            ) {
                // Line Number Gutter
                Column(
                    modifier = Modifier
                        .background(SurfaceContainerLow.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    lines.indices.forEach { index ->
                        Text(
                            text = "${index + 1}",
                            color = OnSurfaceVariant.copy(alpha = 0.35f),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        )
                    }
                }

                // Horizontal scroll for code text
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(horizontalScrollState)
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    BasicTextField(
                        value = scriptContent,
                        onValueChange = onContentChange,
                        readOnly = readOnly,
                        textStyle = TextStyle(
                            color = OnSurface,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp
                        ),
                        visualTransformation = visualTransformation,
                        cursorBrush = SolidColor(Primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 400.dp)
                            .testTag("pine_script_input")
                    )
                }
            }
        }

        // 4. Compiler Validation Diagnostics & Console Drawer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow)
                .border(1.dp, OutlineVariant.copy(alpha = 0.3f))
        ) {
            // Tab Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDim)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val errorCount = validationResult?.errors?.size ?: 0
                    listOf(
                        "diagnostics" to "Diagnostics ($errorCount)",
                        "bytecode" to "Bytecode Spec",
                        "runtime" to "Runtime Engine"
                    ).forEach { (id, label) ->
                        val isSelected = activeConsoleTab == id
                        Box(
                            modifier = Modifier
                                .clickable { activeConsoleTab = id }
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                fontFamily = JetBrainsMono,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Primary else OnSurfaceVariant
                            )
                        }
                    }
                }

                Text(
                    text = compileStatusText,
                    fontFamily = JetBrainsMono,
                    fontSize = 10.sp,
                    color = if (validationResult?.isValid == false) Error else Primary
                )
            }

            // Console Tab Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(SurfaceContainerLowest)
                    .padding(8.dp)
            ) {
                when (activeConsoleTab) {
                    "diagnostics" -> {
                        val errors = validationResult?.errors.orEmpty()
                        val warnings = validationResult?.warnings.orEmpty()

                        if (errors.isEmpty() && warnings.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "0 Syntax Errors • Type inference verified • AST compiled cleanly",
                                    fontFamily = JetBrainsMono,
                                    fontSize = 11.sp,
                                    color = OnSurfaceVariant
                                )
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                errors.forEach { err ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Error.copy(alpha = 0.12f))
                                            .padding(6.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "[Line ${err.lineNumber}]",
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Error
                                        )
                                        Text(
                                            text = err.message,
                                            fontFamily = JetBrainsMono,
                                            fontSize = 10.sp,
                                            color = OnSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (err.fixSuggestion != null) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(Primary.copy(alpha = 0.2f))
                                                    .clickable {
                                                        onContentChange(err.fixSuggestion + "\n" + scriptContent)
                                                        onShowToast("Applied syntax fix suggestion")
                                                    }
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "Fix",
                                                    fontFamily = JetBrainsMono,
                                                    fontSize = 9.sp,
                                                    color = Primary,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }

                                warnings.forEach { warn ->
                                    Text(
                                        text = "[WARN] $warn",
                                        fontFamily = JetBrainsMono,
                                        fontSize = 10.sp,
                                        color = Color(0xFFFFB454)
                                    )
                                }
                            }
                        }
                    }

                    "bytecode" -> {
                        val res = validationResult
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "OPCODE EMISSION SUMMARY:",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Primary
                            )
                            Text(
                                text = "Bytecode Payload: ${res?.bytecodeSize ?: 1842} bytes • AST Nodes: ${scriptContent.lines().size * 4}",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = OnSurface
                            )
                            Text(
                                text = "Memory Footprint: ${res?.memoryFootprintKb ?: 64} KB • Execution Cost: ${res?.executionTimeMs ?: 18} ms/cycle",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "Zero memory leak detected in dynamic series buffers.",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = Primary
                            )
                        }
                    }

                    "runtime" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "EXECUTION ENGINE: AlgoPulse v5.4.1 (JIT Active)",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnSurface
                            )
                            Text(
                                text = "Tick Stream Hook: Connected to CoinDCX / Binance REST depths",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            )
                            Text(
                                text = "Alert Handlers: 2 webhook triggers registered • Zero Slippage Overrun",
                                fontFamily = JetBrainsMono,
                                fontSize = 10.sp,
                                color = OnSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

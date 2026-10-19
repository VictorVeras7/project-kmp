package org.example.project.ui.welcome

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinproject.shared.generated.resources.Res
import kotlinproject.shared.generated.resources.hero_delivery
import org.example.project.ui.theme.RangoTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical))
                .padding(horizontal = 16.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Spacer(Modifier.height(14.dp))
                Logo()
                Spacer(Modifier.height(22.dp))
                Hero()
                Spacer(Modifier.height(26.dp))
                Text(
                    text = "Comida boa,\nentrega rápida.",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 31.sp,
                    lineHeight = 35.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "Os melhores restaurantes da sua região em poucos toques. Pediu, chegou.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.5.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
            Column(
                modifier = Modifier.padding(top = 18.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(11.dp),
            ) {
                Button(
                    onClick = onLogin,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onSignup,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(17.dp),
                    border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                ) {
                    Text("Criar conta", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Text(
                    text = "Ao continuar você aceita os Termos de uso e a Política de privacidade.",
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun Logo() {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(13.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("r", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        }
        Text(
            text = "rango",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun Hero() {
    Image(
        painter = painterResource(Res.drawable.hero_delivery),
        contentDescription = "Entregador de moto",
        modifier = Modifier.fillMaxWidth().height(318.dp),
        contentScale = ContentScale.Fit,
    )
}

@Preview
@Composable
private fun WelcomeScreenPreview() {
    RangoTheme(darkTheme = false) { WelcomeScreen(onLogin = {}, onSignup = {}) }
}

@Preview
@Composable
private fun WelcomeScreenDarkPreview() {
    RangoTheme(darkTheme = true) { WelcomeScreen(onLogin = {}, onSignup = {}) }
}

package com.example.compose_diaryapp.presentation.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_diaryapp.R
import com.example.compose_diaryapp.presentation.components.GoogleButton

@Composable
fun AuthenticationContent(
    loadingState: Boolean,
    onButtonClicked: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .weight(weight = 9f)
                .fillMaxWidth()
                .padding(all = 40.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.weight(weight = 10f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(120.dp),
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo"
                )
                Spacer(modifier = Modifier.height(height = 20.dp))
                Text(
                    text = stringResource(id = R.string.auth_title),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
                Text(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    text = stringResource(id = R.string.auth_subtitle),
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
            Column(
                modifier = Modifier.weight(weight = 2f),
                verticalArrangement = Arrangement.Center
            ) {
                GoogleButton(
                    loadingState = loadingState,
                    onClick = onButtonClicked
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthenticationContentPreview() {
    AuthenticationContent(loadingState = false) {

    }
}
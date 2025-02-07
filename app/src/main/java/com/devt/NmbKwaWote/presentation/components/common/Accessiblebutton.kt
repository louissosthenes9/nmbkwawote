package com.devt.NmbKwaWote.presentation.components.common

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AccessibleButton(
    text: String,
    onClick: ()->Unit,
    modifier: Modifier= Modifier
){
    Button(
        onClick=onClick,
        modifier=modifier
            .height(64.dp)
            .semantics {
                contentDescription = "Action: $text"
                role = Role.Button
            },
        colors = ButtonDefaults.buttonColors(
            contentColor = Color.White,
            containerColor = Color.Blue
        )
    ) {
        Text(
            text = text,
            fontSize = 20.sp
        )
    }
}
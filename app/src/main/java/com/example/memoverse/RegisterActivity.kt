package com.example.memoverse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.memoverse.ui.theme.MemoVerseTheme

class RegisterActivity : ComponentActivity() {
    private lateinit var databaseHelper: UserDatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = UserDatabaseHelper(this)
        setContent {
            MemoVerseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    RegistrationScreen(this,databaseHelper)
                }
            }
        }
    }
}

@Composable
fun RegistrationScreen(context: Context, databaseHelper: UserDatabaseHelper) {

//    Image(
//        painterResource(id = R.drawable.memoverseloginbg), contentDescription = "",
//        contentScale = ContentScale.FillBounds,
//    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    val pontano = FontFamily(
        Font(R.font.pontanosansregular),
        Font(R.font.pontanosanbold, FontWeight.Bold)
    )
    val Typography = Typography(
        h1 = TextStyle(
            fontFamily = pontano,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        ),
        body1 = TextStyle(
            fontFamily = pontano,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        ),
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            fontSize = 35.sp,
            fontWeight = FontWeight.ExtraBold,
            style = MaterialTheme.typography.h1,
            color = colorResource(id = R.color.mainbg),
            text = "Sign-Up"
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(value = username, onValueChange = {username = it},
            label = { Text("Username",style = MaterialTheme.typography.body1)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            leadingIcon = {Icon(Icons.Outlined.Person, contentDescription = null)},
            modifier = Modifier
                .width(280.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.mainbg),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.mainbg),
                unfocusedLabelColor = Color.Gray,
                leadingIconColor = colorResource(id = R.color.mainbg),
                textColor = Color.Black
            )

        )
        OutlinedTextField(value = email, onValueChange = {email = it},
            label = { Text("Email",style = MaterialTheme.typography.body1,)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = {Icon(Icons.Outlined.Person, contentDescription = null)},
            modifier = Modifier
                .width(280.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.mainbg),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.mainbg),
                unfocusedLabelColor = Color.Gray,
                leadingIconColor = colorResource(id = R.color.mainbg),
                textColor = Color.Black
            )

        )
        var showPassword by remember { mutableStateOf(value = false) }
        OutlinedTextField(value = password, onValueChange = {password = it},
            label = { Text("Password", style = MaterialTheme.typography.body1,)},
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            leadingIcon = {Icon(Icons.Outlined.Lock, contentDescription = null)},
            modifier = Modifier
                .width(280.dp),
            visualTransformation = if (showPassword) VisualTransformation.None
            else PasswordVisualTransformation(),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                focusedBorderColor = colorResource(id = R.color.mainbg),
                unfocusedBorderColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.mainbg),
                unfocusedLabelColor = Color.Gray,
                leadingIconColor = colorResource(id = R.color.mainbg),
                textColor = Color.Black,
                trailingIconColor = colorResource(id = R.color.mainbg),
            ),
            trailingIcon = {
                if (showPassword) {
                    TextButton(onClick = { showPassword = false }) {
                        Text(text = "hide", color = colorResource(id = R.color.mainbg), style = MaterialTheme.typography.body1,)
                    }
                } else {
                    TextButton(onClick = { showPassword = true }) {
                        Text(text = "show", color = colorResource(id = R.color.mainbg), style = MaterialTheme.typography.body1,)
                    }
                }
            }
        )


        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                    val user = User(
                        id = null,
                        userName = username,
                        email = email,
                        password = password
                    )
                    databaseHelper.insertUser(user)
                    error = "User registered successfully"
                    // Start LoginActivity using the current context
                    context.startActivity(
                        Intent(
                            context,
                            MainActivity::class.java
                        )
                    )

                } else {
                    error = "Please fill all fields"
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.mainbg)),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .padding(5.dp)
                .width(150.dp)
                .height(40.dp)
        ) {
            Text(text = "Sign-Up",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.body1,
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }

        Row() {
            Text(
                modifier = Modifier.padding(top = 14.dp), text = "Have an account?", color = Color.Black, style = MaterialTheme.typography.body1,
            )
            TextButton(onClick = {
                context.startActivity(Intent(context, LoginActivity::class.java))
            }) {
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = "Sign-in", color = colorResource(id = R.color.mainbg), style = MaterialTheme.typography.body1,)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    MemoVerseTheme {
    }
}